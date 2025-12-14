/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Admin
 */
public class student_bookappointment2nd extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(student_bookappointment2nd.class.getName());
    
    private int currentStudentId;
    private String currentStudentName;
    private int selectedCounselorId;
    private java.util.Date selectedDate;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Creates new form student_bookappointment2nd
     */
    public student_bookappointment2nd() {
        initComponents();
        setLocationRelativeTo(null);
        
        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setMinSelectableDate(new java.util.Date());
        populateTimeSlots();
    }
    
    public student_bookappointment2nd(int studentId, String studentName, int counselorId, java.util.Date appointmentDate) {
        initComponents();
        this.currentStudentId = studentId;
        this.currentStudentName = studentName;
        this.selectedCounselorId = counselorId;
        this.selectedDate = appointmentDate;
        
        setLocationRelativeTo(null);
        jLabel5.setText(studentName + "!");
        
        // Set the pre-selected date
        jDateChooser1.setDate(appointmentDate);
        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setMinSelectableDate(new java.util.Date());
        
        populateTimeSlots();
    }

    private void populateTimeSlots() {
        java.util.List<String> timeSlots = new java.util.ArrayList<>();
        
        // Generate time slots from 8 AM to 4:30 PM (excluding 12 PM to 1 PM)
        for (int hour = 8; hour <= 16; hour++) {
            // Skip 12 PM hour (lunch break)
            if (hour == 12) {
                continue;
            }
            
            for (int minute = 0; minute < 60; minute += 30) {
                
                // Stop at 4:30 PM (16:30)
                if (hour == 16 && minute == 30) {
                    int displayHour = 4;
                    String ampm = "PM";
                    String timeSlot = String.format("%02d:%02d %s", displayHour, minute, ampm);
                    timeSlots.add(timeSlot);
                    break;
                }
                
                // Convert to 12-hour format
                int displayHour = hour;
                String ampm = "AM";
                
                if (hour >= 12) {
                    ampm = "PM";
                    if (hour > 12) {
                        displayHour = hour - 12;
                    }
                }
                
                String timeSlot = String.format("%02d:%02d %s", displayHour, minute, ampm);
                timeSlots.add(timeSlot);
            }
        }
        
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(
            timeSlots.toArray(new String[0])
        ));
    }
    
    private void bookAppointment() {
         java.util.Date selectedDate = jDateChooser1.getDate();
    String selectedTime = (String) jComboBox1.getSelectedItem();
    String reason = jTextArea1.getText().trim();

    // Validate inputs
    if (selectedDate == null) {
        JOptionPane.showMessageDialog(this, 
            "Please select a date", 
            "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (selectedTime == null || selectedTime.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Please select a time", 
            "Validation Error", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Convert time from 12-hour format to 24-hour format for database
    String time24Hour = convertTo24Hour(selectedTime);
    
    // Check if time slot is already taken
    if (isTimeSlotTaken(selectedDate, time24Hour)) {
        JOptionPane.showMessageDialog(this,
            "This time slot is already booked. Please select another time.",
            "Time Slot Unavailable",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Insert appointment into database
    String insertQuery = "INSERT INTO appointments (student_id, counselor_id, appointment_date, " +
                       "appointment_time, reason, status, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, 'Pending', NOW(), NOW())";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
        
        pstmt.setInt(1, currentStudentId);
        pstmt.setInt(2, selectedCounselorId);
        pstmt.setDate(3, new java.sql.Date(selectedDate.getTime()));
        pstmt.setTime(4, java.sql.Time.valueOf(time24Hour + ":00"));
        pstmt.setString(5, reason.isEmpty() ? null : reason);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(selectedDate);
            
            JOptionPane.showMessageDialog(this, 
                "Appointment request submitted successfully!\n\n" +
                "Date: " + dateStr + "\n" +
                "Time: " + selectedTime + "\n" +
                "Status: Pending approval\n\n" +
                "You will be notified once the counselor approves your appointment.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate back to dashboard
            student_dashboard dashboard = new student_dashboard(currentStudentId, currentStudentName);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to submit appointment request. Please try again.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error booking appointment", e);
        JOptionPane.showMessageDialog(this, 
            "Database error: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    private boolean isTimeSlotTaken(java.util.Date date, String time24Hour) {
    String query = "SELECT COUNT(*) as count FROM appointments " +
                  "WHERE counselor_id = ? " +
                  "AND appointment_date = ? " +
                  "AND appointment_time = ? " +
                  "AND status IN ('Upcoming', 'Pending')";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, selectedCounselorId);
        pstmt.setDate(2, new java.sql.Date(date.getTime()));
        pstmt.setTime(3, java.sql.Time.valueOf(time24Hour + ":00"));
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("count") > 0;
        }
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error checking time slot", e);
    }
    
    return false;
}
    
    /**
     * Convert 12-hour time format to 24-hour format
     */
    private String convertTo24Hour(String time12Hour) {
        try {
            java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("hh:mm a");
            java.text.SimpleDateFormat dbFormat = new java.text.SimpleDateFormat("HH:mm");
            java.util.Date date = displayFormat.parse(time12Hour);
            return dbFormat.format(date);
        } catch (java.text.ParseException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error parsing time", e);
            return "08:00"; // Default to 8:00 AM
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jComboBox1 = new javax.swing.JComboBox<>();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("BOOK APPOINTMENT");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setText("VIEW PROFILE");
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setText("MY APPOINTMENTS");
        jButton6.setBorderPainted(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        jLabel10.setText("icon");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        jLabel11.setText("icon");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/book appointment.png"))); // NOI18N
        jLabel2.setText("icon");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(42, 42, 42)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addGap(49, 49, 49)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap(105, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(jButton5)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("WELCOME,");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("USER!");

        jButton7.setBackground(new java.awt.Color(204, 0, 0));
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("LOGOUT");
        jButton7.setBorderPainted(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(38, 36, 68));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        jButton8.setBorderPainted(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 195, 51));
        jLabel6.setText("CHOOSE DATE & TIME");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("BOOK APPOINTMENT");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/book appointment.png"))); // NOI18N
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

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setBackground(new java.awt.Color(255, 195, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("SUBMIT REQUEST");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 195, 51));
        jLabel9.setText("REASON (OPTIONAL)");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(37, 37, 37)
                .addComponent(jButton7)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addContainerGap(565, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addGap(282, 282, 282)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(52, 52, 52))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)))
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton1)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        student_bookappointment a = new student_bookappointment(currentStudentId, currentStudentName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        student_myappointment b = new student_myappointment(currentStudentId, currentStudentName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
    
    if (confirmation == JOptionPane.YES_OPTION) {
        try {
            this.dispose();
            new login_page().setVisible(true);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error during logout", e);
            JOptionPane.showMessageDialog(this, 
                "Error during logout", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        student_dashboard d = new student_dashboard(currentStudentId, currentStudentName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        java.util.Date selectedDate = jDateChooser1.getDate();
    
    // Get selected time from combo box
        String selectedTime = (String) jComboBox1.getSelectedItem();

        // Get reason
        String reason = jTextArea1.getText();

        // Validate
        if (selectedDate == null) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Please select a date", 
                "Validation Error", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedTime == null || selectedTime.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Please select a time", 
                "Validation Error", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Format date for display/storage
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(selectedDate);

        // Display or save the appointment
        bookAppointment();

        // Show confirmation
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Appointment booked for:\n" + dateStr + " at " + selectedTime, 
            "Success", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        student_viewprofile d = new student_viewprofile(currentStudentId, currentStudentName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jComboBox1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new student_bookappointment2nd().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
