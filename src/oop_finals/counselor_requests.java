package oop_finals;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.SwingConstants;
import java.util.List;

/**
 * Counselor Requests Page - Refactored to use MVC architecture
 * Uses AppointmentController for all appointment operations
 */
public class counselor_requests extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(counselor_requests.class.getName());

    private int currentCounselorId;
    private String currentCounselorName;
    
    // Controllers
    private final AppointmentController appointmentController;

    public counselor_requests() {
        this.appointmentController = new AppointmentController();
        initComponents();
    }
    
    public counselor_requests(int counselorId, String counselorName) {
        this.appointmentController = new AppointmentController();
        initComponents();
        this.currentCounselorId = counselorId;
        this.currentCounselorName = counselorName;
        user.setText(currentCounselorName + "!");
        setupTable();
        loadAllRequests();
    }
    
    private void setupTable() {
        // Enable text wrapping for all cells
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                setText((value == null) ? "" : value.toString());
            }
        };
        
        // Set wider width for Reason column
        TableColumn reasonColumn = allrequeststable.getColumnModel().getColumn(4);
        reasonColumn.setPreferredWidth(300);
        reasonColumn.setMinWidth(200);
        
        // Set reasonable widths for other columns
        allrequeststable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        allrequeststable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Time
        allrequeststable.getColumnModel().getColumn(2).setPreferredWidth(150); // Student Name
        allrequeststable.getColumnModel().getColumn(3).setPreferredWidth(100); // Year Level
        
        allrequeststable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
    }
    
    // Load all pending requests using controller
    private void loadAllRequests() {
        try {
            // Get all pending appointments using controller
            List<Appointment> pendingAppointments = appointmentController.getCounselorAppointments(
                currentCounselorId, "Pending");
            
            // Create table model
            DefaultTableModel model = (DefaultTableModel) allrequeststable.getModel();
            model.setRowCount(0); // Clear existing rows
            
            // Populate table
            for (Appointment apt : pendingAppointments) {
                Object[] row = new Object[5];
                row[0] = apt.getAppointmentDate();
                row[1] = apt.getAppointmentTime();
                row[2] = apt.getStudentName();
                row[3] = apt.getStudentEmail(); // This should be year_level - needs model update
                row[4] = apt.getReason();
                
                model.addRow(row);
            }
            
            logger.info("Loaded " + pendingAppointments.size() + " pending requests");
            
        } catch (Exception e) {
            logger.severe("Error loading all requests: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error loading pending requests: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Get appointment ID from table data using controller
    private int getAppointmentIdFromTable(java.util.Date date, java.sql.Time time, String studentName) {
        try {
            return appointmentController.getAppointmentId(
                currentCounselorId,
                new java.sql.Date(date.getTime()),
                time,
                studentName
            );
        } catch (Exception e) {
            logger.severe("Error getting appointment ID: " + e.getMessage());
            return -1;
        }
    }
    
    // Accept appointment using controller
    private void acceptAppointment(int appointmentId) {
        try {
            boolean success = appointmentController.updateAppointmentStatus(appointmentId, "Upcoming");
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Appointment request accepted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadAllRequests(); // Reload table
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to accept appointment. It may have already been processed.",
                    "Already Processed",
                    JOptionPane.WARNING_MESSAGE);
                loadAllRequests();
            }
            
        } catch (Exception e) {
            logger.severe("Error accepting appointment: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error accepting appointment: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Reject appointment using controller
    private void rejectAppointment(int appointmentId, String reason) {
        try {
            boolean success = appointmentController.updateAppointmentStatus(appointmentId, "Rejected");
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Appointment request rejected.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadAllRequests(); // Reload table
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to reject appointment. It may have already been processed.",
                    "Already Processed",
                    JOptionPane.WARNING_MESSAGE);
                loadAllRequests();
            }
            
        } catch (Exception e) {
            logger.severe("Error rejecting appointment: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error rejecting appointment: " + e.getMessage(),
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
        requestspage = new javax.swing.JButton();
        requestlogo = new javax.swing.JLabel();
        schedulelogo = new javax.swing.JLabel();
        schedulepage = new javax.swing.JButton();
        profilelogo = new javax.swing.JLabel();
        profilepage = new javax.swing.JButton();
        welcome = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        logo = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        allrequeststable = new javax.swing.JTable();
        accept = new javax.swing.JButton();
        reject = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        requestspage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        requestspage.setText("REQUESTS");
        requestspage.setBorderPainted(false);
        requestspage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requestspageActionPerformed(evt);
            }
        });

        requestlogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        requestlogo.setText("icon");
        requestlogo.setToolTipText("");

        schedulelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/myappointments.png"))); // NOI18N
        schedulelogo.setText("icon");

        schedulepage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        schedulepage.setText("MY SCHEDULE");
        schedulepage.setBorderPainted(false);
        schedulepage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                schedulepageActionPerformed(evt);
            }
        });

        profilelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        profilelogo.setText("icon");

        profilepage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        profilepage.setText("VIEW PROFILE");
        profilepage.setBorderPainted(false);
        profilepage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profilepageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(requestlogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(requestspage)
                .addGap(65, 65, 65)
                .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(schedulepage)
                .addGap(64, 64, 64)
                .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profilepage)
                .addContainerGap(147, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(requestspage)
                    .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(profilepage)
                    .addComponent(requestlogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(schedulepage))
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

        logo.setBackground(new java.awt.Color(38, 36, 68));
        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        logo.setBorderPainted(false);
        logo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("REQUESTS");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        jLabel1.setText("icon");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        allrequeststable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
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
        allrequeststable.setRowHeight(30);
        allrequeststable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(allrequeststable);

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
                .addGap(37, 37, 37)
                .addComponent(logout)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 672, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(343, 343, 343)
                        .addComponent(accept)
                        .addGap(18, 18, 18)
                        .addComponent(reject)))
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
                            .addComponent(user)
                            .addComponent(jButton2)))
                    .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accept)
                    .addComponent(reject))
                .addContainerGap(31, Short.MAX_VALUE))
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

    private void requestspageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestspageActionPerformed
        // TODO add your handling code here:
        counselor_requests a = new counselor_requests(currentCounselorId, currentCounselorName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_requestspageActionPerformed

    private void schedulepageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_schedulepageActionPerformed
        // TODO add your handling code here:
        counselor_myschedule b = new counselor_myschedule(currentCounselorId, currentCounselorName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_schedulepageActionPerformed

    private void profilepageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profilepageActionPerformed
        // TODO add your handling code here:
        counselor_viewprofile c = new counselor_viewprofile(currentCounselorId, currentCounselorName);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_profilepageActionPerformed

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

    private void logoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoActionPerformed
        // TODO add your handling code here:
        counselor_dashboard d = new counselor_dashboard(currentCounselorId, currentCounselorName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoActionPerformed

    private void rejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rejectActionPerformed
        int selectedRow = allrequeststable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a request from the table to reject.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get appointment details from selected row
        java.util.Date appointmentDate = (java.util.Date) allrequeststable.getValueAt(selectedRow, 0);
        java.sql.Time appointmentTime = (java.sql.Time) allrequeststable.getValueAt(selectedRow, 1);
        String studentName = (String) allrequeststable.getValueAt(selectedRow, 2);
        
        // Find appointment ID using controller
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
        } else {
            JOptionPane.showMessageDialog(this,
                "Could not find appointment. Please refresh and try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_rejectActionPerformed

    private void acceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptActionPerformed
        int selectedRow = allrequeststable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a request from the table to accept.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get appointment details from selected row
        java.util.Date appointmentDate = (java.util.Date) allrequeststable.getValueAt(selectedRow, 0);
        java.sql.Time appointmentTime = (java.sql.Time) allrequeststable.getValueAt(selectedRow, 1);
        String studentName = (String) allrequeststable.getValueAt(selectedRow, 2);
        
        // Find appointment ID using controller
        int appointmentId = getAppointmentIdFromTable(appointmentDate, appointmentTime, studentName);
        
        if (appointmentId != -1) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Accept appointment request from " + studentName + "?",
                "Confirm Acceptance",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                acceptAppointment(appointmentId);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Could not find appointment. Please refresh and try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_acceptActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new counselor_requests().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accept;
    private javax.swing.JTable allrequeststable;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton logo;
    private javax.swing.JButton logout;
    private javax.swing.JLabel profilelogo;
    private javax.swing.JButton profilepage;
    private javax.swing.JButton reject;
    private javax.swing.JLabel requestlogo;
    private javax.swing.JButton requestspage;
    private javax.swing.JLabel schedulelogo;
    private javax.swing.JButton schedulepage;
    private javax.swing.JLabel user;
    private javax.swing.JLabel welcome;
    // End of variables declaration//GEN-END:variables
}
