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

public class counselor_regis extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(student_regis.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Creates new form counselor_regis
     */
    public counselor_regis() {
        initComponents();
        setupPlaceholders();
    }
    
    private boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return true;
        }
    }

    private boolean counselorNumberExists(String studentNumber) {
        String sql = "SELECT 1 FROM students WHERE student_number = ?";
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return true;
        }
    }

    private void setupPlaceholders() {
        setupPlaceholder(jTextField141, "Full name");
        setupPlaceholder(jTextField142, "Email");
        setupPlaceholder(jTextField143, "Specialization");
        setupPlaceholder(jTextField144, "License ID");
        setupPasswordPlaceholder(jTextField146, "Password");
        setupPasswordPlaceholder(jPasswordField29, "Confirm Password");
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

        jPanel24 = new javax.swing.JPanel();
        jButton57 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jTextField141 = new javax.swing.JTextField();
        jTextField142 = new javax.swing.JTextField();
        jTextField143 = new javax.swing.JTextField();
        jTextField144 = new javax.swing.JTextField();
        jPasswordField29 = new javax.swing.JPasswordField();
        jButton58 = new javax.swing.JButton();
        jTextField146 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel24.setBackground(new java.awt.Color(38, 36, 68));

        jButton57.setBackground(new java.awt.Color(38, 36, 68));
        jButton57.setForeground(new java.awt.Color(255, 255, 255));
        jButton57.setText("Back");
        jButton57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton57ActionPerformed(evt);
            }
        });

        jLabel29.setBackground(new java.awt.Color(38, 36, 68));
        jLabel29.setFont(new java.awt.Font("Segoe UI Black", 1, 32)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("COUNSELOR REGISTRATION");

        jTextField141.setForeground(new java.awt.Color(255, 195, 51));
        jTextField141.setText("Name");

        jTextField142.setForeground(new java.awt.Color(255, 195, 51));
        jTextField142.setText("Email");
        jTextField142.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField142ActionPerformed(evt);
            }
        });

        jTextField143.setForeground(new java.awt.Color(255, 195, 51));
        jTextField143.setText("Specialization");

        jTextField144.setForeground(new java.awt.Color(255, 195, 51));
        jTextField144.setText("License ID");

        jPasswordField29.setText("jPasswordField29");

        jButton58.setBackground(new java.awt.Color(255, 195, 51));
        jButton58.setForeground(new java.awt.Color(255, 255, 255));
        jButton58.setText("Register");
        jButton58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton58ActionPerformed(evt);
            }
        });

        jTextField146.setForeground(new java.awt.Color(255, 195, 51));
        jTextField146.setText("Password");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap(177, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addGap(160, 160, 160))
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(238, 238, 238)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField141)
                            .addComponent(jTextField142)
                            .addComponent(jTextField143)
                            .addComponent(jTextField144)
                            .addComponent(jPasswordField29, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                            .addComponent(jTextField146)))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(336, 336, 336)
                        .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91)
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(jTextField141, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField142, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField143, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField144, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField146, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPasswordField29, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(149, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton57ActionPerformed
        this.dispose();
        new new_account().setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton57ActionPerformed

    private void jTextField142ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField142ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField142ActionPerformed

    private void jButton58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton58ActionPerformed
        // TODO add your handling code here:
        String fullname = jTextField141.getText().trim();
        String email = jTextField142.getText().trim();
        String specialization = jTextField143.getText().trim();
        String licenseId = jTextField144.getText().trim();
        String password = jTextField146.getText().trim();
        String confirmPassword = String.valueOf(jPasswordField29.getPassword()).trim();
        
        // ================= VALIDATION =================
        if (fullname.isEmpty() || fullname.equals("Full name") 
                || email.isEmpty() || email.equals("Email") 
                || specialization.isEmpty() || specialization.equals("Specialization") 
                || licenseId.isEmpty() || licenseId.equals("License ID") 
                || password.isEmpty() || password.equals("Password") 
                || confirmPassword.isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please complete all fields.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
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

        if (counselorNumberExists(licenseId)) {
            JOptionPane.showMessageDialog(this,
                    "Student number already registered.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // ================= DATABASE INSERT =================
        String insertUser
                = "INSERT INTO users (user_type, name, email, password, status) "
                + "VALUES ('Counselor', ?, ?, ?, 'Pending')";

        String insertCounselor 
                = "INSERT INTO students (user_id, name, email, course, student_number, password, status) " 
                + "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";

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

            // Insert into COUNSELORS
            try (PreparedStatement ps = con.prepareStatement(insertCounselor)) {
                ps.setInt(1, userId);
                ps.setString(2, fullname);
                ps.setString(3, email);
                ps.setString(4, specialization);
                ps.setString(5, licenseId);
                ps.setString(6, password);
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
    }//GEN-LAST:event_jButton58ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new counselor_regis().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPasswordField jPasswordField29;
    private javax.swing.JTextField jTextField141;
    private javax.swing.JTextField jTextField142;
    private javax.swing.JTextField jTextField143;
    private javax.swing.JTextField jTextField144;
    private javax.swing.JTextField jTextField146;
    // End of variables declaration//GEN-END:variables
}
