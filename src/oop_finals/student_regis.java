package oop_finals;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Gian
 */
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class student_regis extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(student_regis.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Creates new form student_regis
     */
    public student_regis() {
        initComponents();
        setupPlaceholders();
    }
    
    private boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return true;
        }
    }

    private boolean studentNumberExists(String studentNumber) {
        String sql = "SELECT 1 FROM students WHERE student_number = ?";
        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return true;
        }
    }
    
    private void setupPlaceholders(){
        setupPlaceholder(jTextField1, "Full name");
        setupPlaceholder(jTextField2, "Email");
        setupPlaceholder(jTextField3, "Year level & Program");
        setupPlaceholder(jTextField4, "ID num");
        setupPasswordPlaceholder(jTextField5, "Password");
        setupPasswordPlaceholder(jPasswordField1, "Confirm Password");
    }
    
    private void setupPlaceholder(javax.swing.JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            
        @Override
        public void focusGained(FocusEvent e) {
            if (textField.getText().equals(placeholder)) {
                textField.setText("");
                textField.setForeground(new Color(255, 195, 51));
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (textField.getText().isEmpty()) {
                textField.setText(placeholder);
                textField.setForeground(Color.GRAY);
            }
        }
    });
    }
    
    private void setupPasswordPlaceholder(javax.swing.JComponent passwordField, String placeholder) {
        if (passwordField instanceof javax.swing.JPasswordField) {
            javax.swing.JPasswordField pf = (javax.swing.JPasswordField) passwordField;
            pf.setText(placeholder);
            pf.setForeground(Color.GRAY);
            pf.setEchoChar((char) 0);

            pf.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(pf.getPassword()).equals(placeholder)) {
                    pf.setText("");
                    pf.setForeground(new Color(255, 195, 51));
                    pf.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(pf.getPassword()).isEmpty()) {
                    pf.setText(placeholder);
                    pf.setForeground(Color.GRAY);
                    pf.setEchoChar((char) 0);
                }
            }
        });
        } else if (passwordField instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) passwordField;
            tf.setText(placeholder);
            tf.setForeground(Color.GRAY);

            tf.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(new Color(255, 195, 51));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                }
            }
        });
        }
    }
    
    private boolean isValidEmail(String email) {
    return EMAIL_PATTERN.matcher(email).matches();
    
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton2 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(38, 36, 68));

        jLabel1.setBackground(new java.awt.Color(38, 36, 68));
        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 32)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("STUDENT REGISTRATION");

        jButton1.setBackground(new java.awt.Color(38, 36, 68));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setForeground(new java.awt.Color(255, 195, 51));
        jTextField1.setText("Full name");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setForeground(new java.awt.Color(255, 195, 51));
        jTextField2.setText("Email");

        jTextField3.setForeground(new java.awt.Color(255, 195, 51));
        jTextField3.setText("Year level");

        jTextField4.setForeground(new java.awt.Color(255, 195, 51));
        jTextField4.setText("ID num");

        jTextField5.setForeground(new java.awt.Color(255, 195, 51));
        jTextField5.setText("Password");

        jPasswordField1.setForeground(new java.awt.Color(255, 195, 51));
        jPasswordField1.setText("jPasswordField1");
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 195, 51));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("REGISTER");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField6.setForeground(new java.awt.Color(255, 195, 51));
        jTextField6.setText("Course");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(244, 244, 244)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField1)
                                .addComponent(jTextField2)
                                .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                                .addComponent(jTextField4)
                                .addComponent(jTextField5)
                                .addComponent(jPasswordField1))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(345, 345, 345)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 193, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(190, 190, 190))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
        new new_account().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String fullname = jTextField1.getText().trim();
    String email = jTextField2.getText().trim();
    String yearlvl = jTextField3.getText().trim();
    String course = jTextField6.getText().trim();
    String studentNumber = jTextField4.getText().trim();
    String password = jTextField5.getText().trim();
    String confirmPassword = String.valueOf(jPasswordField1.getPassword()).trim();

    // ================= VALIDATION =================
    if (fullname.isEmpty() || fullname.equals("Full name") ||
        email.isEmpty() || email.equals("Email") ||
        yearlvl.isEmpty() || yearlvl.equals("Year level") ||
        course.isEmpty() || course.equals("Course") ||
        studentNumber.isEmpty() || studentNumber.equals("ID num") ||
        password.isEmpty() || password.equals("Password") ||
        confirmPassword.isEmpty()) {

        JOptionPane.showMessageDialog(this,
                "Please complete all fields.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!isValidEmail(email)) {
        JOptionPane.showMessageDialog(this,
                "Invalid email format.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (password.length() < 6) {
        JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (emailExists(email)) {
        JOptionPane.showMessageDialog(this,
                "Email already registered.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (studentNumberExists(studentNumber)) {
        JOptionPane.showMessageDialog(this,
                "Student number already registered.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        return;
    }

    // ================= DATABASE INSERT =================
    String insertUser =
        "INSERT INTO users (user_type, name, email, password, status) " +
        "VALUES ('Student', ?, ?, ?, 'Pending')";

    String insertStudent =
        "INSERT INTO students (user_id, name, email, yearlvl, course, student_number, password, status) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";

    try (Connection con = DatabaseConnection.getConnection()) {
        con.setAutoCommit(false);

        // Insert into USERS
        int userId;
        try (PreparedStatement ps = con.prepareStatement(insertUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fullname);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                con.rollback();
                JOptionPane.showMessageDialog(this, "User creation failed.");
                return;
            }
            userId = rs.getInt(1);
        }

        // Insert into STUDENTS
        try (PreparedStatement ps = con.prepareStatement(insertStudent)) {
            ps.setInt(1, userId);
            ps.setString(2, fullname);
            ps.setString(3, email);
            ps.setString(4, yearlvl);
            ps.setString(5, course);
            ps.setString(6, studentNumber);
            ps.setString(7, password);
            ps.executeUpdate();
        }

        con.commit();

        JOptionPane.showMessageDialog(this,
                "Registration successful!\n\nPlease wait for admin approval.",
                "Registration Submitted",
                JOptionPane.INFORMATION_MESSAGE);

        this.dispose();
        new new_account().setVisible(true);

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Registration failed:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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

        java.awt.EventQueue.invokeLater(() -> new student_regis().setVisible(true));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
