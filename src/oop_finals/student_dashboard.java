/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
import java.sql.*;
import javax.swing.JOptionPane;

public class student_dashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(student_dashboard.class.getName());
    private int currentStudentId;
    private String currentStudentName;
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Change to your MySQL password

    // Default constructor
    public student_dashboard() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    // Constructor with student information (call this from login page)
    public student_dashboard(int studentId, String studentName) {
        this.currentStudentId = studentId;
        this.currentStudentName = studentName;
        initComponents();

        loadDashboardData();
    }
    
    // Load all dashboard data from database
    private void loadDashboardData() {
        // Update welcome message
        user.setText(currentStudentName + "!");
        
        // Load appointment counts for the cards
        loadAppointmentCounts();
        
        // Load upcoming appointments into the table
        loadUpcomingAppointmentsTable();
        
    }
    
    // Add this new method to load upcoming appointments into the table
    private void loadUpcomingAppointmentsTable() {
        System.out.println("=== Loading Upcoming Appointments Into Table ===");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Clear existing table data
            javax.swing.table.DefaultTableModel model = 
                (javax.swing.table.DefaultTableModel) pendingrequesttable.getModel();
            model.setRowCount(0); // Clear all rows

            // Load upcoming appointments for this student
            String query = "SELECT a.appointment_date, a.appointment_time, c.name as counselor_name " +
                          "FROM appointments a " +
                          "JOIN counselors c ON a.counselor_id = c.counselor_id " +
                          "WHERE a.student_id = ? AND a.status = 'Upcoming' " +
                          "ORDER BY a.appointment_date, a.appointment_time";

            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, currentStudentId);
                ResultSet rs = pst.executeQuery();

                int count = 0;
                while (rs.next()) {
                    String date = rs.getDate("appointment_date").toString();
                    String time = rs.getTime("appointment_time").toString();
                    String counselorName = rs.getString("counselor_name");

                    // Add row to table
                    model.addRow(new Object[]{date, time, counselorName});
                    count++;

                    System.out.println("✓ Added appointment: " + date + " at " + time + " with " + counselorName);
                }

                if (count == 0) {
                    System.out.println("○ No upcoming appointments found");
                    // Add empty row to show "No data"
                    model.addRow(new Object[]{"No upcoming appointments", "", ""});
                } else {
                    System.out.println("✓ Loaded " + count + " upcoming appointments into table");
                }

            }

            System.out.println("=== Finished Loading Upcoming Appointments ===");

        } catch (SQLException e) {
            System.err.println("✗ Error loading upcoming appointments table: " + e.getMessage());
            e.printStackTrace();
            logger.log(java.util.logging.Level.SEVERE, "Error loading upcoming appointments table", e);
        }
    }

        // Load appointment counts for dashboard cards
    private void loadAppointmentCounts() {
        String query = "SELECT " +
                      "COUNT(CASE WHEN status = 'Upcoming' THEN 1 END) as upcoming_count, " +
                      "COUNT(CASE WHEN status = 'Pending' THEN 1 END) as pending_count, " +
                      "COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completed_count " +
                      "FROM appointments WHERE student_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, currentStudentId);
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
  


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        bookappointment = new javax.swing.JButton();
        view_profile = new javax.swing.JButton();
        myappointments = new javax.swing.JButton();
        profile = new javax.swing.JLabel();
        appointments = new javax.swing.JLabel();
        book = new javax.swing.JLabel();
        welcome = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        logo = new javax.swing.JButton();
        dashboard = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        upcoming = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        pending = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        completed = new javax.swing.JLabel();
        upcominglabel = new javax.swing.JLabel();
        pendinglabel = new javax.swing.JLabel();
        completedlabel = new javax.swing.JLabel();
        upcomingappointments = new javax.swing.JLabel();
        viewall = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        pendingrequesttable = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(38, 36, 68));

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        bookappointment.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bookappointment.setText("BOOK APPOINTMENT");
        bookappointment.setBorderPainted(false);
        bookappointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookappointmentActionPerformed(evt);
            }
        });

        view_profile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        view_profile.setText("VIEW PROFILE");
        view_profile.setBorderPainted(false);
        view_profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_profileActionPerformed(evt);
            }
        });

        myappointments.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myappointments.setText("MY APPOINTMENTS");
        myappointments.setBorderPainted(false);
        myappointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myappointmentsActionPerformed(evt);
            }
        });

        profile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        profile.setText("icon");

        appointments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        appointments.setText("icon");

        book.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/book appointment.png"))); // NOI18N
        book.setText("icon");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(book, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bookappointment)
                .addGap(42, 42, 42)
                .addComponent(appointments, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myappointments)
                .addGap(49, 49, 49)
                .addComponent(profile, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(view_profile)
                .addContainerGap(93, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookappointment)
                    .addComponent(profile, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myappointments)
                    .addComponent(view_profile)
                    .addComponent(book, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointments, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        welcome.setForeground(new java.awt.Color(255, 255, 255));
        welcome.setText("WELCOME,");

        user.setForeground(new java.awt.Color(255, 255, 255));
        user.setText("USER!");

        logo.setBackground(new java.awt.Color(38, 36, 68));
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        logo.setBorderPainted(false);
        logo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoActionPerformed(evt);
            }
        });

        dashboard.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dashboard.setForeground(new java.awt.Color(255, 195, 51));
        dashboard.setText("DASHBOARD");

        jPanel5.setBackground(new java.awt.Color(38, 36, 68));

        jPanel6.setPreferredSize(new java.awt.Dimension(150, 150));

        upcoming.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        upcoming.setForeground(new java.awt.Color(0, 0, 204));
        upcoming.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(upcoming, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(upcoming, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jPanel12.setPreferredSize(new java.awt.Dimension(150, 150));

        pending.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        pending.setForeground(new java.awt.Color(204, 0, 0));
        pending.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pending, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pending, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jPanel13.setPreferredSize(new java.awt.Dimension(150, 150));

        completed.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        completed.setForeground(new java.awt.Color(0, 153, 0));
        completed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(completed, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(completed, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        upcominglabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        upcominglabel.setForeground(new java.awt.Color(255, 255, 255));
        upcominglabel.setText("UPCOMING");

        pendinglabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pendinglabel.setForeground(new java.awt.Color(255, 255, 255));
        pendinglabel.setText("PENDING");

        completedlabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        completedlabel.setForeground(new java.awt.Color(255, 255, 255));
        completedlabel.setText("COMPLETED");

        upcomingappointments.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        upcomingappointments.setForeground(new java.awt.Color(255, 195, 51));
        upcomingappointments.setText("UPCOMING APPOINTMENTS");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(upcominglabel)
                        .addGap(135, 135, 135)
                        .addComponent(pendinglabel)
                        .addGap(142, 142, 142)
                        .addComponent(completedlabel))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(upcomingappointments)))
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(upcominglabel)
                    .addComponent(pendinglabel)
                    .addComponent(completedlabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(upcomingappointments))
        );

        viewall.setBackground(new java.awt.Color(255, 195, 51));
        viewall.setForeground(new java.awt.Color(255, 255, 255));
        viewall.setText("View All");
        viewall.setBorderPainted(false);
        viewall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewallActionPerformed(evt);
            }
        });

        logout.setBackground(new java.awt.Color(204, 0, 0));
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("LOGOUT");
        logout.setBorderPainted(false);
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        pendingrequesttable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Date", "Time", "Counselor Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(welcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user)
                .addGap(27, 27, 27)
                .addComponent(logout)
                .addGap(49, 49, 49))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(dashboard))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(601, 601, 601)
                        .addComponent(viewall)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(welcome)
                            .addComponent(user)
                            .addComponent(logout)
                            .addComponent(jButton2)))
                    .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(dashboard)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(viewall)
                .addContainerGap(45, Short.MAX_VALUE))
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
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewallActionPerformed
        // TODO add your handling code here:
        student_myappointment d = new student_myappointment(currentStudentId, currentStudentName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_viewallActionPerformed

    private void logoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoActionPerformed
        // TODO add your handling code here:
        student_dashboard d = new student_dashboard(currentStudentId, currentStudentName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoActionPerformed

    private void myappointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myappointmentsActionPerformed
        // TODO add your handling code here:
        student_myappointment b = new student_myappointment(currentStudentId, currentStudentName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_myappointmentsActionPerformed

    private void view_profileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_profileActionPerformed
        // TODO add your handling code here:
        student_viewprofile c = new student_viewprofile(currentStudentId, currentStudentName);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_view_profileActionPerformed

    private void bookappointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookappointmentActionPerformed
        // TODO add your handling code here:
        student_bookappointment a = new student_bookappointment(currentStudentId, currentStudentName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bookappointmentActionPerformed

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
        // TODO add your handling code here        
    }//GEN-LAST:event_logoutActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        student_dashboard d = new student_dashboard(currentStudentId, currentStudentName);
        d.setVisible(true);
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
        java.awt.EventQueue.invokeLater(() -> new student_dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appointments;
    private javax.swing.JLabel book;
    private javax.swing.JButton bookappointment;
    private javax.swing.JLabel completed;
    private javax.swing.JLabel completedlabel;
    private javax.swing.JLabel dashboard;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton logo;
    private javax.swing.JButton logout;
    private javax.swing.JButton myappointments;
    private javax.swing.JLabel pending;
    private javax.swing.JLabel pendinglabel;
    private javax.swing.JTable pendingrequesttable;
    private javax.swing.JLabel profile;
    private javax.swing.JLabel upcoming;
    private javax.swing.JLabel upcomingappointments;
    private javax.swing.JLabel upcominglabel;
    private javax.swing.JLabel user;
    private javax.swing.JButton view_profile;
    private javax.swing.JButton viewall;
    private javax.swing.JLabel welcome;
    // End of variables declaration//GEN-END:variables
}
