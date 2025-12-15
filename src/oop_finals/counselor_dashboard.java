/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class counselor_dashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(counselor_dashboard.class.getName());
    private int currentCounselorId;
    private String currentCounselorName;
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Change to your MySQL password
    
    private int currentAppointmentId1 = -1;  // First text area
    private int currentAppointmentId2 = -1; 

    // Default constructor
    public counselor_dashboard() {
        initComponents();
        pendingrequesttable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pendingrequesttable.setColumnSelectionAllowed(false);
        pendingrequesttable.setRowSelectionAllowed(true);
        loadDashboardData();
    }
    
    // Constructor with student information (call this from login page)
    public counselor_dashboard(int counselorId, String counselorName) {
        initComponents();
        this.currentCounselorId = counselorId;
        this.currentCounselorName = counselorName;
        loadDashboardData();
    }
    
    // Load all dashboard data from database
    private void loadDashboardData() {
        // Update welcome message
        user.setText(currentCounselorName + "!");
        loadAppointmentCounts();
        loadPendingRequestsTable();
    }
    
    // Load appointment counts for dashboard cards (jLabel1, jLabel3, jLabel8)
    private void loadAppointmentCounts() {
        String query = "SELECT " +
                      "COUNT(CASE WHEN status = 'Upcoming' THEN 1 END) as upcoming_count, " +
                      "COUNT(CASE WHEN status = 'Pending' THEN 1 END) as pending_count, " +
                      "COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completed_count " +
                      "FROM appointments WHERE counselor_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, currentCounselorId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int upcomingCount = rs.getInt("upcoming_count");
                int pendingCount = rs.getInt("pending_count");
                int completedCount = rs.getInt("completed_count");
                
                // Update the labels
                upcoming.setText(String.valueOf(upcomingCount));
                pending.setText(String.valueOf(pendingCount));
                completed.setText(String.valueOf(completedCount));
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading appointment counts", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading appointment counts: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPendingRequestsTable() {
    String query = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, " +
                      "s.name as student_name, s.year_level, s.course, a.reason " +
                      "FROM appointments a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "WHERE a.counselor_id = ? AND a.status = 'Pending' " +
                      "ORDER BY a.appointment_date, a.appointment_time LIMIT 2";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, currentCounselorId);
            ResultSet rs = pstmt.executeQuery();
            
            // Create table model
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) pendingrequesttable.getModel();
            model.setRowCount(0); // Clear existing rows
            
            // Populate table
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getDate("appointment_date");
                row[1] = rs.getTime("appointment_time");
                row[2] = rs.getString("student_name");
                row[3] = rs.getString("year_level");
                row[4] = rs.getString("course");
                row[5] = rs.getString("reason");
                
                model.addRow(row);
            }
            
            logger.log(java.util.logging.Level.INFO, "Loaded " + model.getRowCount() + " pending requests (max 2 for dashboard)");
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading pending requests table", e);
            JOptionPane.showMessageDialog(this,
                "Error loading pending requests: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getAppointmentIdFromTable(java.util.Date date, java.sql.Time time, String studentName) {
        String query = "SELECT a.appointment_id " +
                      "FROM appointments a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "WHERE a.counselor_id = ? AND a.appointment_date = ? " +
                      "AND a.appointment_time = ? AND s.name = ? AND a.status = 'Pending'";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, currentCounselorId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            pstmt.setTime(3, time);
            pstmt.setString(4, studentName);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("appointment_id");
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error getting appointment ID", e);
        }
        
        return -1;
    }

    // Accept appointment method
    private void acceptAppointment(int appointmentId) {
        String updateQuery = "UPDATE appointments SET status = 'Upcoming', updated_at = NOW() WHERE appointment_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            
            pstmt.setInt(1, appointmentId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "Appointment request accepted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reload dashboard to show updated data
                loadDashboardData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to accept appointment. It may have been already processed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error accepting appointment", e);
            JOptionPane.showMessageDialog(this,
                "Error accepting appointment: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reject appointment method
    private void rejectAppointment(int appointmentId, String reason) {
        String updateQuery = "UPDATE appointments SET status = 'Rejected', notes = ?, updated_at = NOW() WHERE appointment_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            
            pstmt.setString(1, "Rejected: " + reason);
            pstmt.setInt(2, appointmentId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "Appointment request rejected.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reload dashboard to show updated data
                loadDashboardData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to reject appointment. It may have been already processed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error rejecting appointment", e);
            JOptionPane.showMessageDialog(this,
                "Error rejecting appointment: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Creates new form counselor_dashboard
     */

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        requests = new javax.swing.JButton();
        viewprofile = new javax.swing.JButton();
        myschedule = new javax.swing.JButton();
        profilelogo = new javax.swing.JLabel();
        schedulelogo = new javax.swing.JLabel();
        requestslogo = new javax.swing.JLabel();
        welcome = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        logo_home = new javax.swing.JButton();
        dashboard = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        upcoming = new javax.swing.JLabel();
        pendingrequests = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        pending = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        completed = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pendingrequesttable = new javax.swing.JTable();
        accept = new javax.swing.JButton();
        reject = new javax.swing.JButton();
        viewall = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jButton1.setBackground(new java.awt.Color(255, 195, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("SUBMIT REQUEST");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        requests.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        requests.setText("REQUESTS");
        requests.setBorderPainted(false);
        requests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requestsActionPerformed(evt);
            }
        });

        viewprofile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewprofile.setText("VIEW PROFILE");
        viewprofile.setBorderPainted(false);
        viewprofile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewprofileActionPerformed(evt);
            }
        });

        myschedule.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myschedule.setText("MY SCHEDULE");
        myschedule.setBorderPainted(false);
        myschedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myscheduleActionPerformed(evt);
            }
        });

        profilelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        profilelogo.setText("icon");

        schedulelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/myappointments.png"))); // NOI18N
        schedulelogo.setText("icon");

        requestslogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        requestslogo.setText("icon");
        requestslogo.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(requestslogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(requests)
                .addGap(51, 51, 51)
                .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myschedule)
                .addGap(50, 50, 50)
                .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewprofile)
                .addContainerGap(123, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(requests)
                    .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myschedule)
                    .addComponent(viewprofile)
                    .addComponent(requestslogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        welcome.setForeground(new java.awt.Color(255, 255, 255));
        welcome.setText("WELCOME,");

        user.setForeground(new java.awt.Color(255, 255, 255));
        user.setText("USER!");

        logout.setBackground(new java.awt.Color(204, 0, 0));
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("LOGOUT");
        logout.setBorderPainted(false);
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        logo_home.setBackground(new java.awt.Color(38, 36, 68));
        logo_home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        logo_home.setBorderPainted(false);
        logo_home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logo_homeActionPerformed(evt);
            }
        });

        dashboard.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dashboard.setForeground(new java.awt.Color(255, 195, 51));
        dashboard.setText("DASHBOARD");

        jPanel6.setBackground(new java.awt.Color(38, 36, 68));

        jPanel7.setPreferredSize(new java.awt.Dimension(150, 150));

        upcoming.setFont(new java.awt.Font("Segoe UI", 1, 84)); // NOI18N
        upcoming.setForeground(new java.awt.Color(0, 0, 204));
        upcoming.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        upcoming.setText("jLabel1");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(upcoming, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(upcoming, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        pendingrequests.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pendingrequests.setForeground(new java.awt.Color(255, 195, 51));
        pendingrequests.setText("PENDING APPOINTMENT REQUESTS");

        jPanel8.setPreferredSize(new java.awt.Dimension(150, 150));

        pending.setFont(new java.awt.Font("Segoe UI", 1, 84)); // NOI18N
        pending.setForeground(new java.awt.Color(204, 0, 0));
        pending.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pending.setText("jLabel2");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pending, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pending, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jPanel9.setPreferredSize(new java.awt.Dimension(150, 150));

        completed.setFont(new java.awt.Font("Segoe UI", 1, 84)); // NOI18N
        completed.setForeground(new java.awt.Color(0, 204, 0));
        completed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        completed.setText("jLabel3");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(completed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(completed, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("UPCOMING");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("PENDING");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("COMPLETED");

        pendingrequesttable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Student Name", "Year Level", "Course", "Reason"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        pendingrequesttable.setRowHeight(90);
        pendingrequesttable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pendingrequesttable.setShowGrid(false);
        pendingrequesttable.setShowHorizontalLines(true);
        jScrollPane3.setViewportView(pendingrequesttable);

        accept.setBackground(new java.awt.Color(255, 195, 51));
        accept.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        accept.setForeground(new java.awt.Color(255, 255, 255));
        accept.setText("ACCEPT");
        accept.setBorderPainted(false);
        accept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptActionPerformed(evt);
            }
        });

        reject.setBackground(new java.awt.Color(255, 195, 51));
        reject.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        reject.setForeground(new java.awt.Color(255, 255, 255));
        reject.setText("REJECT");
        reject.setBorderPainted(false);
        reject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rejectActionPerformed(evt);
            }
        });

        viewall.setBackground(new java.awt.Color(255, 195, 51));
        viewall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewall.setForeground(new java.awt.Color(255, 255, 255));
        viewall.setText("VIEW ALL");
        viewall.setBorderPainted(false);
        viewall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewallActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jLabel4)
                        .addGap(140, 140, 140)
                        .addComponent(jLabel5)
                        .addGap(148, 148, 148)
                        .addComponent(jLabel6))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pendingrequests)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(accept, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(reject, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(viewall, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(pendingrequests)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reject)
                    .addComponent(accept)
                    .addComponent(viewall))
                .addGap(184, 184, 184))
        );

        jButton2.setText("Home");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(logo_home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(welcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user)
                .addGap(37, 37, 37)
                .addComponent(logout)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(dashboard)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logout)
                            .addComponent(welcome)
                            .addComponent(user)
                            .addComponent(jButton2)))
                    .addComponent(logo_home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(dashboard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void viewallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewallActionPerformed
        // TODO add your handling code here:
        // CORRECT - Pass the parameters!
        counselor_requests r = new counselor_requests(currentCounselorId, currentCounselorName);
        r.setVisible(true);
        this.dispose();     
    }//GEN-LAST:event_viewallActionPerformed

    private void rejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rejectActionPerformed
        // TODO add your handling code here:
        int selectedRow = pendingrequesttable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a request from the table to reject.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get appointment details from selected row
        java.util.Date appointmentDate = (java.util.Date) pendingrequesttable.getValueAt(selectedRow, 0);
        java.sql.Time appointmentTime = (java.sql.Time) pendingrequesttable.getValueAt(selectedRow, 1);
        String studentName = (String) pendingrequesttable.getValueAt(selectedRow, 2);
        
        // Find appointment ID
        int appointmentId = getAppointmentIdFromTable(appointmentDate, appointmentTime, studentName);
        
        if (appointmentId != -1) {
            String reason = JOptionPane.showInputDialog(this,
                "Enter reason for rejection:",
                "Reject Appointment",
                JOptionPane.QUESTION_MESSAGE);
            
            if (reason != null && !reason.trim().isEmpty()) {
                rejectAppointment(appointmentId, reason);
            } else if (reason != null) {
                JOptionPane.showMessageDialog(this,
                    "Rejection reason is required.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_rejectActionPerformed

    private void acceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptActionPerformed
        // TODO add your handling code here:
        int selectedRow = pendingrequesttable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a request from the table to accept.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get appointment details from selected row
        java.util.Date appointmentDate = (java.util.Date) pendingrequesttable.getValueAt(selectedRow, 0);
        java.sql.Time appointmentTime = (java.sql.Time) pendingrequesttable.getValueAt(selectedRow, 1);
        String studentName = (String) pendingrequesttable.getValueAt(selectedRow, 2);
        
        // Find appointment ID
        int appointmentId = getAppointmentIdFromTable(appointmentDate, appointmentTime, studentName);
        
        if (appointmentId != -1) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Accept appointment request from " + studentName + "?",
                "Confirm Acceptance",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                acceptAppointment(appointmentId);
            }
        }
    }//GEN-LAST:event_acceptActionPerformed

    private void logo_homeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logo_homeActionPerformed
        // TODO add your handling code here:
        counselor_dashboard d = new counselor_dashboard(currentCounselorId, currentCounselorName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logo_homeActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        // TODO add your handling code here:
        int confirmation = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to logout?",
            "Confirm logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            this.dispose();
            new login_page().setVisible(true);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_logoutActionPerformed

    private void myscheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myscheduleActionPerformed
        // TODO add your handling code here:
        counselor_myschedule b = new counselor_myschedule(currentCounselorId, currentCounselorName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_myscheduleActionPerformed

    private void viewprofileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewprofileActionPerformed
        // TODO add your handling code here:
        counselor_viewprofile c = new counselor_viewprofile(currentCounselorId, currentCounselorName);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_viewprofileActionPerformed

    private void requestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestsActionPerformed
        // TODO add your handling code here:
        counselor_requests a = new counselor_requests(currentCounselorId, currentCounselorName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_requestsActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        counselor_dashboard c = new counselor_dashboard(currentCounselorId, currentCounselorName);
        c.setVisible(true);
        this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new counselor_dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accept;
    private javax.swing.JLabel completed;
    private javax.swing.JLabel dashboard;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton logo_home;
    private javax.swing.JButton logout;
    private javax.swing.JButton myschedule;
    private javax.swing.JLabel pending;
    private javax.swing.JLabel pendingrequests;
    private javax.swing.JTable pendingrequesttable;
    private javax.swing.JLabel profilelogo;
    private javax.swing.JButton reject;
    private javax.swing.JButton requests;
    private javax.swing.JLabel requestslogo;
    private javax.swing.JLabel schedulelogo;
    private javax.swing.JLabel upcoming;
    private javax.swing.JLabel user;
    private javax.swing.JButton viewall;
    private javax.swing.JButton viewprofile;
    private javax.swing.JLabel welcome;
    // End of variables declaration//GEN-END:variables
}
