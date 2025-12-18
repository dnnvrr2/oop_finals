package oop_finals;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;

/**
 * Student Login Page
 * Handles student authentication UI using MVC architecture
 */
public class student_login_page extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(student_login_page.class.getName());
    
    private final LoginController logincontroller;
    
    /**
     * Creates new form student_login_page
     */
    public student_login_page() {
        this.logincontroller = new LoginController();
        initComponents();
        setupPlaceholders();
        setLocationRelativeTo(null);
        setTitle("Student Login");
    }
    
    /**
     * Setup placeholder text for input fields
     */
    private void setupPlaceholders() {
        setupPlaceholder(username, "Email");
        setupPasswordPlaceholder(password, "Password");
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
    private void setupPasswordPlaceholder(javax.swing.JPasswordField passwordField, String placeholder) {
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•');
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).trim().isEmpty()) {
                    passwordField.setText(placeholder);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    }
    
    /**
     * Validate input fields before login
     */
    private boolean validateInput() {
        String emailText = username.getText().trim();
        String passwordText = String.valueOf(password.getPassword()).trim();
        
        if (emailText.isEmpty() || emailText.equals("Email")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your email!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            username.requestFocus();
            return false;
        }
        
        if (passwordText.isEmpty() || passwordText.equals("Password")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter your password!", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            password.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Handle login action using StudentController
     */
    /**
 * Handle login action using LoginController
 */
private void performLogin() {
    if (!validateInput()) {
        return;
    }
    
    String email = username.getText().trim();
    String pass = String.valueOf(password.getPassword()).trim();
    
    try {
        // Use LoginController for authentication - returns StudentLoginResult
        LoginController.StudentLoginResult result = logincontroller.loginStudent(email, pass);
        
        if (result.isSuccess()) {
            // Login successful
            Student student = result.getStudent();
            JOptionPane.showMessageDialog(this, 
                "Welcome, " + student.getName() + "!", 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open student dashboard
            this.dispose();
            new student_dashboard(student.getStudentId(), student.getName()).setVisible(true);
        } else {
            // Login failed - show the error message from LoginController
            JOptionPane.showMessageDialog(this, 
                result.getMessage(), 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            
            // Clear password field
            password.setText("");
            setupPasswordPlaceholder(password, "Password");
        }
        
    } catch (Exception e) {
        logger.severe("Login error: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "An error occurred during login. Please try again.", 
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
        student = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();
        back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(38, 36, 68));

        student.setFont(new java.awt.Font("Segoe UI Black", 1, 32)); // NOI18N
        student.setForeground(new java.awt.Color(255, 255, 255));
        student.setText("STUDENT");

        username.setForeground(new java.awt.Color(255, 195, 51));
        username.setText("Username");
        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });

        password.setForeground(new java.awt.Color(255, 195, 51));
        password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordActionPerformed(evt);
            }
        });

        login.setBackground(new java.awt.Color(255, 195, 51));
        login.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        login.setForeground(new java.awt.Color(255, 255, 255));
        login.setText("LOGIN");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        back.setBackground(new java.awt.Color(38, 36, 68));
        back.setForeground(new java.awt.Color(255, 255, 255));
        back.setText("Back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(back, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(230, 230, 230)
                                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(325, 325, 325)
                        .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(304, 304, 304)
                        .addComponent(student)))
                .addContainerGap(266, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(back, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120)
                .addComponent(student)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(login, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(339, Short.MAX_VALUE))
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

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        this.dispose();
        new login_page().setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_backActionPerformed

    private void passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordActionPerformed
        // TODO add your handling code here:
        performLogin();
    }//GEN-LAST:event_passwordActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        performLogin();
    }//GEN-LAST:event_loginActionPerformed

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:
        password.requestFocus();
    }//GEN-LAST:event_usernameActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new student_login_page().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton back;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton login;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel student;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
