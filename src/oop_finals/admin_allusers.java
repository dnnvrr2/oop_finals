/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

/**
 *
 * @author Admin
 */
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel; // ADD THIS IMPORT
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

public class admin_allusers extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(admin_allusers.class.getName());
    private TableRowSorter<DefaultTableModel> sorter;
    private DefaultTableModel tableModel;

    /**
     * Creates new form admin_allusers
     */
    public admin_allusers() {
        initComponents();
        setupTable();
        setupTableClickListener();
        setupButtons();
        loadUsers();
        setupSearchBar();
        setupPlaceholders();
    }
    
    private void setupSearchBar() {
    // Create a sorter for the table
    sorter = new TableRowSorter<>(tableModel);
    jTable1.setRowSorter(sorter);
    
    // Add document listener for real-time search
    Search.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            search();
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
            search();
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
            search();
        }
        
        private void search() {
            String text = Search.getText().trim();
            
            // Ignore placeholder text
            if (text.length() == 0 || text.equals("Search")) {
                sorter.setRowFilter(null);
            } else {
                // Search across all columns
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    });
}
    private void setupPlaceholders() {
    // Setup username placeholder
    Search.setText("Search");
    Search.setForeground(Color.GRAY);
    
    Search.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (Search.getText().equals("Search")) {
                Search.setText("");
                Search.setForeground(new Color(0, 0, 0)); 
            }
        }
        
        @Override
        public void focusLost(FocusEvent e) {
            if (Search.getText().isEmpty()) {
                Search.setText("Search");
                Search.setForeground(Color.GRAY);
                // Clear any filters when placeholder is restored
                if (sorter != null) {
                    sorter.setRowFilter(null);
                }
            }
        }
    });
}
    private void setupButtons() {
    // Get references to your activate/deactivate buttons
    // Assuming you have buttons in your GUI - adjust names as needed
    // You'll need to add these buttons in the GUI designer first
    
    // For now, I'll show you the logic that should go in button action handlers
}
    private void activateButtonActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = jTable1.getSelectedRow();
    
    if (selectedRow == -1) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please select a user from the table first.",
            "No Selection",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Convert view row to model row (important when using sorter/filter)
    int modelRow = jTable1.convertRowIndexToModel(selectedRow);
    
    String name = tableModel.getValueAt(modelRow, 0).toString();
    String currentStatus = tableModel.getValueAt(modelRow, 4).toString();
    
    if (currentStatus.equals("Active")) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "User " + name + " is already active.",
            "Already Active",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    // Confirm action
    int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
        "Are you sure you want to activate user: " + name + "?",
        "Confirm Activation",
        javax.swing.JOptionPane.YES_NO_OPTION);
    
    if (confirm == javax.swing.JOptionPane.YES_OPTION) {
        // Update the status in the table
        tableModel.setValueAt("Active", modelRow, 4);
        
        // TODO: Update in database
        /*
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE users SET status = 'Active' WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, tableModel.getValueAt(modelRow, 2).toString()); // ID
            pst.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        
        javax.swing.JOptionPane.showMessageDialog(this,
            "User " + name + " has been activated successfully.",
            "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}

// For the DEACTIVATE button
private void deactivateButtonActionPerformed(java.awt.event.ActionEvent evt) {
    int selectedRow = jTable1.getSelectedRow();
    
    if (selectedRow == -1) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Please select a user from the table first.",
            "No Selection",
            javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Convert view row to model row
    int modelRow = jTable1.convertRowIndexToModel(selectedRow);
    
    String name = tableModel.getValueAt(modelRow, 0).toString();
    String currentStatus = tableModel.getValueAt(modelRow, 4).toString();
    
    if (currentStatus.equals("Inactive")) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "User " + name + " is already inactive.",
            "Already Inactive",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    // Confirm action
    int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
        "Are you sure you want to deactivate user: " + name + "?",
        "Confirm Deactivation",
        javax.swing.JOptionPane.YES_NO_OPTION,
        javax.swing.JOptionPane.WARNING_MESSAGE);
    
    if (confirm == javax.swing.JOptionPane.YES_OPTION) {
        // Update the status in the table
        tableModel.setValueAt("Inactive", modelRow, 4);
        
        // TODO: Update in database
        /*
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "UPDATE users SET status = 'Inactive' WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, tableModel.getValueAt(modelRow, 2).toString()); // ID
            pst.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        
        javax.swing.JOptionPane.showMessageDialog(this,
            "User " + name + " has been deactivated successfully.",
            "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
    private void setupTable() {
        String[] columns = {"Name", "Type", "ID", "Email", "Profile"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable1.setModel(tableModel);
        jTable1.setRowHeight(25);
    }
    
    private void setupTableClickListener() {
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            int row = jTable1.rowAtPoint(e.getPoint());
            int column = jTable1.columnAtPoint(e.getPoint());
            
            if (row >= 0 && column == 0) { // Column 0 is Name column (first column)
                String Name = jTable1.getValueAt(row, 0).toString();
                String Type = jTable1.getValueAt(row, 1).toString(); // ADD THIS
                String ID = jTable1.getValueAt(row, 2).toString();
                String Email = jTable1.getValueAt(row, 3).toString(); // ADD THIS
                
                // Open profile page
                openProfile(Name, Type, ID, Email);
            }
        }
    });
}
    private void loadUsers() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add sample users (replace with your database query)
        tableModel.addRow(new Object[]{"John Doe", "Student", "2021001", "john@email.com", "Active"});
        tableModel.addRow(new Object[]{"Jane Smith", "Student", "2021002", "jane@email.com", "Active"});
        tableModel.addRow(new Object[]{"Dr. Brown", "Counselor", "C001", "brown@email.com", "Active"});
        
        // TODO: Load from database
        /*
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT name, type, id, email, status FROM users";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                String id = rs.getString("id");
                String email = rs.getString("email");
                String status = rs.getString("status");
                
                tableModel.addRow(new Object[]{name, type, id, email, status});
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
    
    private void openProfile(String Name, String Type, String ID, String Email) {
        // Open different profile forms based on user type
      
            // For admin or other types
            javax.swing.JOptionPane.showMessageDialog(this,
                "Profile Details:\n" +
                "Name: " + Name + "\n" +
                "Type: " + Type + "\n" +
                "ID: " + ID + "\n" +
                "Email: " + Email,
                "User Profile",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

            // TODO: Load additional data from database
            /*
            try {
                String query = "SELECT * FROM students WHERE student_id = ?";
                // Load and display data
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
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
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
                .addContainerGap(171, Short.MAX_VALUE))
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

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Type", "ID", "Email", "Profile"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
        jLabel1.setText("ALL USERS");

        Search.setText("Search");

        jButton9.setBackground(new java.awt.Color(255, 195, 51));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("DEACTIVATE");
        jButton9.setBorderPainted(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(255, 195, 51));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("ACTIVATE");
        jButton10.setBorderPainted(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

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
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton9))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jButton1))
                .addGap(32, 32, 32))
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
        // TODO add your handling code here:
        deactivateButtonActionPerformed(evt);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        activateButtonActionPerformed(evt);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
        new admin_dashboard().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               new admin_allusers().setVisible(true);
            }
        });
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton logout;
    // End of variables declaration//GEN-END:variables
}