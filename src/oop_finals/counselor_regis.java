package oop_finals;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.util.List;

/**
 * Counselor Registration - Refactored to use MVC architecture
 * Uses CounselorController for registration logic and validation
 */
public class counselor_regis extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(counselor_regis.class.getName());
    
    // Controllers
    private final CounselorController counselorController;
    
    public counselor_regis() {
        this.counselorController = new CounselorController();
        initComponents();
        setupPlaceholders();
        loadSpecializations();
    }
    
    private void setupPlaceholders() {
        setupPlaceholder(jTextField141, "Full name");
        setupPlaceholder(jTextField142, "Email");
        setupPlaceholder(jTextField144, "License ID");
        setupPasswordPlaceholder(jTextField147, "Password");
        setupPasswordPlaceholder(passwordfield, "Confirm Password");
    }
    
    private void setupPlaceholder(javax.swing.JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(new Color(0, 0, 0));
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
                        pf.setForeground(new Color(0, 0, 0));
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
                        tf.setForeground(new Color(0, 0, 0));
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
    
    // Load specializations using controller
    private void loadSpecializations() {
        try {
            List<String> specializations = counselorController.getAllSpecializations();
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Select Specialization --");
            
            for (String spec : specializations) {
                model.addElement(spec);
            }
            
            jComboBox1.setModel(model);
            logger.info("Loaded " + specializations.size() + " specializations");
            
        } catch (Exception e) {
            logger.severe("Error loading specializations: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading specializations: " + e.getMessage(),
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

        jPanel24 = new javax.swing.JPanel();
        jButton57 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jTextField141 = new javax.swing.JTextField();
        jTextField142 = new javax.swing.JTextField();
        jTextField144 = new javax.swing.JTextField();
        jTextField147 = new javax.swing.JTextField();
        jTextField146 = new javax.swing.JTextField();
        register = new javax.swing.JButton();
        passwordfield = new javax.swing.JPasswordField();
        jComboBox1 = new javax.swing.JComboBox<>();

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

        jTextField141.setText("Name");

        jTextField142.setText("Email");
        jTextField142.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField142ActionPerformed(evt);
            }
        });

        jTextField144.setText("License ID");

        jTextField147.setText("Password");
        jTextField147.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField147ActionPerformed(evt);
            }
        });

        jTextField146.setForeground(new java.awt.Color(255, 195, 51));
        jTextField146.setText("Password");

        register.setBackground(new java.awt.Color(255, 195, 51));
        register.setForeground(new java.awt.Color(255, 255, 255));
        register.setText("REGISTER");
        register.setBorderPainted(false);
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });

        passwordfield.setText("jPasswordField1");
        passwordfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordfieldActionPerformed(evt);
            }
        });

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButton57, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(165, 165, 165)
                        .addComponent(jLabel29))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(349, 349, 349)
                        .addComponent(register))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(238, 238, 238)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField141)
                            .addComponent(jTextField142)
                            .addComponent(jTextField144)
                            .addComponent(jTextField147)
                            .addComponent(passwordfield, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(174, Short.MAX_VALUE))
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
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jTextField144, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField147, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(passwordfield, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(register)
                .addContainerGap(179, Short.MAX_VALUE))
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
    }//GEN-LAST:event_jButton57ActionPerformed

    private void jTextField142ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField142ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField142ActionPerformed

    private void jTextField147ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField147ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField147ActionPerformed

    private void registerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerActionPerformed
        String fullname = jTextField141.getText().trim();
        String email = jTextField142.getText().trim();
        String selectedSpec = (String) jComboBox1.getSelectedItem();
        String licenseId = jTextField144.getText().trim();
        String password = jTextField147.getText().trim();
        String confirmPassword = new String(passwordfield.getPassword()).trim();

        // Basic field validation
        if (fullname.isEmpty() || fullname.equals("Full name") ||
            email.isEmpty() || email.equals("Email") ||
            selectedSpec == null || selectedSpec.equals("-- Select Specialization --") ||
            licenseId.isEmpty() || licenseId.equals("License ID") ||
            password.isEmpty() || password.equals("Password") ||
            confirmPassword.isEmpty() || confirmPassword.equals("Confirm Password")) {

            JOptionPane.showMessageDialog(this,
                    "Please complete all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use controller to register counselor (includes all validation)
        String result = counselorController.registerCounselor(
            fullname, email, selectedSpec, licenseId, password, confirmPassword);

        if ("SUCCESS".equals(result)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful!\n\nYour application has been submitted.\n" +
                    "Please wait for admin approval.",
                    "Registration Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

            logger.info("Counselor registration submitted: " + fullname);
            this.dispose();
            new new_account().setVisible(true);
        } else {
            // Show error message from controller
            JOptionPane.showMessageDialog(this,
                    result,
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_registerActionPerformed

    private void passwordfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordfieldActionPerformed
        // TODO add your handling code here:
        registerActionPerformed (evt);
    }//GEN-LAST:event_passwordfieldActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

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
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JTextField jTextField141;
    private javax.swing.JTextField jTextField142;
    private javax.swing.JTextField jTextField144;
    private javax.swing.JTextField jTextField146;
    private javax.swing.JTextField jTextField147;
    private javax.swing.JPasswordField passwordfield;
    private javax.swing.JButton register;
    // End of variables declaration//GEN-END:variables
}
