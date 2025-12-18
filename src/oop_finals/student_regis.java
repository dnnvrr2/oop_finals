package oop_finals;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;

/**
 * Student Registration Page
 * Handles student registration UI using MVC architecture
 */
public class student_regis extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(student_regis.class.getName());
    
    private final RegistrationController registrationController;

    public student_regis() {
        this.registrationController = new RegistrationController();
        initComponents();
        setupPlaceholders();
        setLocationRelativeTo(null);
        setTitle("Student Registration");
    }
    
    /**
     * Setup placeholders for all input fields
     */
    private void setupPlaceholders() {
        setupPlaceholder(jTextField1, "Full name");
        setupPlaceholder(jTextField2, "Email");
        setupPlaceholder(jTextField3, "Year level");
        setupPlaceholder(jTextField6, "Course");
        setupPlaceholder(jTextField4, "ID Number");
        setupPasswordPlaceholder(jTextField5, "Password");
        setupPasswordPlaceholder(jPasswordField1, "Confirm Password");
    }
    
    /**
     * Setup placeholder for text field
     */
    private void setupPlaceholder(javax.swing.JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    /**
     * Setup placeholder for password field
     */
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
                        pf.setForeground(Color.BLACK);
                        pf.setEchoChar('•');
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (String.valueOf(pf.getPassword()).trim().isEmpty()) {
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
                        tf.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (tf.getText().trim().isEmpty()) {
                        tf.setText(placeholder);
                        tf.setForeground(Color.GRAY);
                    }
                }
            });
        }
    }
    
    /**
     * Get input values from form fields
     */
    private String[] getFormInputs() {
        return new String[] {
            jTextField1.getText().trim(),      // Full name
            jTextField2.getText().trim(),      // Email
            jTextField3.getText().trim(),      // Year level
            jTextField6.getText().trim(),      // Course
            jTextField4.getText().trim(),      // Student number
            jTextField5.getText().trim(),      // Password
            String.valueOf(jPasswordField1.getPassword()).trim()  // Confirm password
        };
    }
    
    /**
     * Clear all form fields
     */
    private void clearForm() {
        setupPlaceholders();
    }
    
    /**
     * Handle registration action using StudentController
     */
    /**
 * Handle registration action using RegistrationController
 */
private void performRegistration() {
    try {
        // Get form inputs
        String[] inputs = getFormInputs();
        String fullname = inputs[0];
        String email = inputs[1];
        String yearLevel = inputs[2];
        String course = inputs[3];
        String studentNumber = inputs[4];
        String password = inputs[5];
        String confirmPassword = inputs[6];
        
        // Create RegistrationController instead of using StudentController
        RegistrationController registrationController = new RegistrationController();
        
        // Use RegistrationController to handle registration
        RegistrationController.RegistrationResult result = registrationController.registerStudent(
            fullname, 
            email, 
            password, 
            confirmPassword, 
            studentNumber, 
            course, 
            yearLevel
        );
        
        // Check result
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                result.getMessage(),
                "Registration Submitted",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate back to account selection
            this.dispose();
            new new_account().setVisible(true);
        } else {
            // Show error message
            JOptionPane.showMessageDialog(this,
                result.getMessage(),
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception e) {
        logger.severe("Registration error: " + e.getMessage());
        JOptionPane.showMessageDialog(this,
            "An error occurred during registration. Please try again.",
            "Error",
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
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setForeground(new java.awt.Color(255, 195, 51));
        jTextField4.setText("ID Number");

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
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

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
        performRegistration();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        // TODO add your handling code here:
        performRegistration();
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        jTextField2.requestFocus();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
        jTextField6.requestFocus();
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
        jTextField4.requestFocus();
    }//GEN-LAST:event_jTextField6ActionPerformed

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