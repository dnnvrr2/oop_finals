/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

/**
 *
 * @author Admin
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class counselor_myscheduleappointments extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(counselor_myscheduleappointments.class.getName());

    private int currentCounselorId;
    private String currentCounselorName;
    private Connection conn;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    /**
     * Creates new form counselor_myscheduleappointments
     */
    public counselor_myscheduleappointments() {
        initComponents();
    }
    
    public counselor_myscheduleappointments(int counselorId, String counselorName) {
        initComponents();
        this.currentCounselorId = counselorId;
        this.currentCounselorName = counselorName;
        user.setText(currentCounselorName + "!");
        loadUpcomingAppointments();
    }
    
    private void loadUpcomingAppointments() {
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            // Query to get upcoming appointments for this counselor
            String query = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, " +
                          "s.name as student_name, s.year_level, a.reason " +
                          "FROM appointments a " +
                          "JOIN students s ON a.student_id = s.student_id " +
                          "WHERE a.counselor_id = ? " +
                          "AND a.appointment_date >= CURDATE() " +
                          "AND a.status = 'Upcoming' " +
                          "ORDER BY a.appointment_date ASC, a.appointment_time ASC " +
                          "LIMIT 50";
            
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, currentCounselorId);
            ResultSet rs = pst.executeQuery();
            
            // Create table modela
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "Time", "Student Name", "Year Level", "Reason"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Populate table
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            
            while (rs.next()) {
                LocalDate date = rs.getDate("appointment_date").toLocalDate();
                LocalTime time = rs.getTime("appointment_time").toLocalTime();
                
                model.addRow(new Object[]{
                    date.format(dateFormatter),
                    time.format(timeFormatter),
                    rs.getString("student_name"),
                    rs.getString("year_level"),
                    rs.getString("reason")
                });
            }
            
            jTable2.setModel(model);
            
            // Set column widths
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
            jTable2.getColumnModel().getColumn(1).setPreferredWidth(80);  // Time
            jTable2.getColumnModel().getColumn(2).setPreferredWidth(150); // Student Name
            jTable2.getColumnModel().getColumn(3).setPreferredWidth(80);  // Year Level
            jTable2.getColumnModel().getColumn(4).setPreferredWidth(250); // Reason
            
            // Center align specific columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            jTable2.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            jTable2.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            jTable2.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            
            rs.close();
            pst.close();
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading upcoming appointments", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading appointments: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Database connection error", e);
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Add dispose method to close connection
    @Override
    public void dispose() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error closing connection", e);
        }
        super.dispose();
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
        requestslogo = new javax.swing.JLabel();
        requests = new javax.swing.JButton();
        schedulelogo = new javax.swing.JLabel();
        viewprofile = new javax.swing.JButton();
        profilelogo = new javax.swing.JLabel();
        myschedule = new javax.swing.JButton();
        welcome = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        home = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        profilelogo2 = new javax.swing.JLabel();
        myschedule2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        requestslogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        requestslogo.setText("icon");
        requestslogo.setToolTipText("");

        requests.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        requests.setText("REQUESTS");
        requests.setBorderPainted(false);
        requests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requestsActionPerformed(evt);
            }
        });

        schedulelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/myappointments.png"))); // NOI18N
        schedulelogo.setText("icon");

        viewprofile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewprofile.setText("VIEW PROFILE");
        viewprofile.setBorderPainted(false);
        viewprofile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewprofileActionPerformed(evt);
            }
        });

        profilelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        profilelogo.setText("icon");

        myschedule.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myschedule.setText("MY SCHEDULE");
        myschedule.setBorderPainted(false);
        myschedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myscheduleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(requestslogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(requests)
                .addGap(65, 65, 65)
                .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(myschedule)
                .addGap(64, 64, 64)
                .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewprofile)
                .addContainerGap(89, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(requests)
                    .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewprofile)
                    .addComponent(requestslogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myschedule))
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

        home.setBackground(new java.awt.Color(38, 36, 68));
        home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        home.setBorderPainted(false);
        home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        profilelogo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/myappointments.png"))); // NOI18N
        profilelogo2.setText("icon");

        myschedule2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myschedule2.setText("APPOINTMENTS");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(profilelogo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myschedule2)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(profilelogo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myschedule2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Time", "Student Name", "Year Level", "Reason"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setFocusable(false);
        jTable2.setRowHeight(50);
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable2.setShowHorizontalLines(true);
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(welcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user)
                .addGap(37, 37, 37)
                .addComponent(logout)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                            .addComponent(user)))
                    .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void requestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestsActionPerformed
        // TODO add your handling code here:
        counselor_requests a = new counselor_requests(currentCounselorId, currentCounselorName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_requestsActionPerformed

    private void viewprofileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewprofileActionPerformed
        // TODO add your handling code here:
        counselor_viewprofile c = new counselor_viewprofile(currentCounselorId, currentCounselorName);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_viewprofileActionPerformed

    private void myscheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myscheduleActionPerformed
        // TODO add your handling code here:
        counselor_myschedule b = new counselor_myschedule(currentCounselorId, currentCounselorName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_myscheduleActionPerformed

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
    }//GEN-LAST:event_logoutActionPerformed

    private void homeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeActionPerformed
        // TODO add your handling code here:
        counselor_dashboard d = new counselor_dashboard(currentCounselorId, currentCounselorName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_homeActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new counselor_myscheduleappointments().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton home;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JButton logout;
    private javax.swing.JButton myschedule;
    private javax.swing.JLabel myschedule2;
    private javax.swing.JLabel profilelogo;
    private javax.swing.JLabel profilelogo2;
    private javax.swing.JButton requests;
    private javax.swing.JLabel requestslogo;
    private javax.swing.JLabel schedulelogo;
    private javax.swing.JLabel user;
    private javax.swing.JButton viewprofile;
    private javax.swing.JLabel welcome;
    // End of variables declaration//GEN-END:variables
}
