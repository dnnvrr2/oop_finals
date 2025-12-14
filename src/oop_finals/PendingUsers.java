/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

/**
 *
 * @author lain
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PendingUsers extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PendingUsers.class.getName());

    /**
     * Creates new form PendingUsers
     */
    public PendingUsers() {
        initComponents();
        loadPendingUsers();
        setupSearch();
    }
    
    // LOAD PENDING USERS
    private void loadPendingUsers() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        String sql =
            "SELECT u.user_id, u.name, u.user_type, u.email, " +
            "COALESCE(s.course, c.specialization, 'N/A') AS info, " +
            "COALESCE(s.student_number, c.license_number, 'N/A') AS id_number, " +
            "u.status " +
            "FROM users u " +
            "LEFT JOIN students s ON u.user_id = s.user_id " +
            "LEFT JOIN counselors c ON u.user_id = c.user_id " +
            "WHERE u.status = 'Pending' " +
            "ORDER BY u.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("user_type"),
                    rs.getString("email"),
                    rs.getString("info"),
                    rs.getString("id_number"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading pending users:\n" + e.getMessage(),
                    "DAtabase error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // UPDATE STATUS (APPROVE / REJECT)
    private void updateUserStatus(String newStatus) {
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user first.",
                    "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) jTable1.getValueAt(row, 0);
        String userName = jTable1.getValueAt(row, 1).toString();
        String userType = jTable1.getValueAt(row, 2).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + newStatus.toLowerCase() + " " + userName + "?",
                "Confirm " + newStatus,
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            // Update USERS table
            String updateUser = "UPDATE users SET status = ? WHERE user_id = ?";
            try (PreparedStatement ps = con.prepareStatement(updateUser)) {
                ps.setString(1, newStatus);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            // Update role-specific table
            String roleSql;
            if (userType.equalsIgnoreCase("Student")) {
                roleSql = "UPDATE students SET status = ? WHERE user_id = ?";
            } else if (userType.equalsIgnoreCase("Counselor")) {
                roleSql = "UPDATE counselors SET status = ? WHERE user_id = ?";
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Unknown user type.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(roleSql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, userId);
                ps.executeUpdate();
            }

            con.commit();

            String message = newStatus.equals("APPROVED") 
                ? "User approved successfully!\n" + userName + " can now login."
                : "User rejected successfully.";

            JOptionPane.showMessageDialog(this,
                    message,
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            
            loadPendingUsers();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Update failed:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


        // SEARCH FILTER
        private void setupSearch() {
        Search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }

            private void search() {
                String keyword = Search.getText().trim();
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0);

                String sql =
                    "SELECT u.user_id, u.name, u.user_type, u.email, " +
                    "COALESCE(s.course, c.specialization, 'N/A') AS info, " +
                    "COALESCE(s.student_number, c.license_number, 'N/A') AS id_number, " +
                    "u.status " +
                    "FROM users u " +
                    "LEFT JOIN students s ON u.user_id = s.user_id " +
                    "LEFT JOIN counselors c ON u.user_id = c.user_id " +
                    "WHERE u.status = 'Pending' " +
                    "AND (u.name LIKE ? OR u.email LIKE ? OR u.user_type LIKE ?) " +
                    "ORDER BY u.created_at DESC";

                try (Connection con = DatabaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    String searchPattern = "%" + keyword + "%";
                    ps.setString(1, searchPattern);
                    ps.setString(2, searchPattern);
                    ps.setString(3, searchPattern);
                    
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("user_type"),
                            rs.getString("email"),
                            rs.getString("info"),
                            rs.getString("id_number"),
                            rs.getString("status")
                        });
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, 
                            "Search failed:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        Search = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(38, 36, 68));
        setForeground(java.awt.Color.black);

        jPanel1.setBackground(new java.awt.Color(38, 36, 68));

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("PENDING USER REQUESTS");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setText("ALL USERS");
        jButton6.setBorderPainted(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/all users.png"))); // NOI18N
        jLabel11.setText("icon");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        jLabel2.setText("icon");
        jLabel2.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(122, 122, 122)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addContainerGap(169, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("WELCOME,");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("USER!");

        logout.setBackground(new java.awt.Color(204, 0, 0));
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("LOGOUT");
        logout.setBorderPainted(false);
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
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

        jScrollPane1.setToolTipText("");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "Name", "Type", "Email", "Extra Info", "ID Number", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 195, 51));
        jLabel1.setText("PENDING APPLICATIONS");

        Search.setText("Search");

        jButton9.setBackground(new java.awt.Color(255, 195, 51));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("REJECT");
        jButton9.setBorderPainted(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(255, 195, 51));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("ACCEPT");
        jButton10.setBorderPainted(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SEARCH FOR USER");

        jButton1.setBackground(new java.awt.Color(38, 36, 68));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                .addComponent(logout)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
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
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)))
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(8, 8, 8)
                        .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

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

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
    int row = jTable1.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a user first.",
                "No selection",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int userId = (int) jTable1.getValueAt(row, 0);
    String userName = jTable1.getValueAt(row, 1).toString();
    String userType = jTable1.getValueAt(row, 2).toString();
    
    // Ask for rejection reason
    String reason = JOptionPane.showInputDialog(this,
            "Enter reason for rejection (optional):",
            "Reject User",
            JOptionPane.QUESTION_MESSAGE);
    
    // If user cancelled the dialog, return
    if (reason == null) {
        return;
    }
    
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reject " + userName + "?\nThis will prevent them from logging in.",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try (Connection con = DatabaseConnection.getConnection()) {
        con.setAutoCommit(false);

        // Update USERS table to Rejected status
        String updateUser = "UPDATE users SET status = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(updateUser)) {
            ps.setString(1, "Rejected");
            ps.setInt(2, userId);
            ps.executeUpdate();
        }

        // Update role-specific table
        String roleSql;
        if (userType.equalsIgnoreCase("Student")) {
            roleSql = "UPDATE students SET status = ? WHERE user_id = ?";
        } else if (userType.equalsIgnoreCase("Counselor")) {
            roleSql = "UPDATE counselors SET status = ? WHERE user_id = ?";
        } else {
            con.rollback();
            JOptionPane.showMessageDialog(this, "Unknown user type.");
            return;
        }

        try (PreparedStatement ps = con.prepareStatement(roleSql)) {
            ps.setString(1, "Rejected");
            ps.setInt(2, userId);
            ps.executeUpdate();
        }

        con.commit();

        JOptionPane.showMessageDialog(this,
                "User rejected successfully.\n" + userName + " will not be able to login.",
                "Status Updated",
                JOptionPane.INFORMATION_MESSAGE);
        
        loadPendingUsers();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
                "Update failed:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
    
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        int row = jTable1.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this,
                "Please select a user first.",
                "No selection",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int userId = (int) jTable1.getValueAt(row, 0);
    String userName = jTable1.getValueAt(row, 1).toString();
    String userType = jTable1.getValueAt(row, 2).toString();
    
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to approve " + userName + "?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try (Connection con = DatabaseConnection.getConnection()) {
        con.setAutoCommit(false);

        // Update USERS table to Active status
        String updateUser = "UPDATE users SET status = ? WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(updateUser)) {
            ps.setString(1, "Active");
            ps.setInt(2, userId);
            ps.executeUpdate();
        }

        // Update role-specific table
        String roleSql;
        if (userType.equalsIgnoreCase("Student")) {
            roleSql = "UPDATE students SET status = ? WHERE user_id = ?";
        } else if (userType.equalsIgnoreCase("Counselor")) {
            roleSql = "UPDATE counselors SET status = ? WHERE user_id = ?";
        } else {
            con.rollback();
            JOptionPane.showMessageDialog(this, "Unknown user type.");
            return;
        }

        try (PreparedStatement ps = con.prepareStatement(roleSql)) {
            ps.setString(1, "Active");
            ps.setInt(2, userId);
            ps.executeUpdate();
        }

        con.commit();

        JOptionPane.showMessageDialog(this,
                "User approved successfully!\n" + userName + " can now login.",
                "Status Updated",
                JOptionPane.INFORMATION_MESSAGE);
        
        loadPendingUsers();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
                "Update failed:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
        new admin_dashboard().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new PendingUsers().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Search;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton logout;
    // End of variables declaration//GEN-END:variables
}
