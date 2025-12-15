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
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PendingUsers extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PendingUsers.class.getName());

    private int currentAdminId;
    private String currentAdminName;
    
    private TableRowSorter<DefaultTableModel> sorter;
    
    /**
     * Creates new form PendingUsers
     */
    public PendingUsers() {
        initComponents();
        loadPendingUsers();
        setupSearch();
        setupFilterComboBox();
    }
    
    public PendingUsers(int adminId, String adminName) {
        this.currentAdminId = adminId;
        this.currentAdminName = adminName;
        initComponents();
        loadPendingUsers();
        setupSearch();
        setupFilterComboBox();

        // Set the admin name in the UI
        jLabel5.setText(adminName + "!");
    }
    
    // LOAD PENDING USERS
    private void loadPendingUsers() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        String sql = "SELECT request_id, name, user_type, email, requested_at, status " +
                 "FROM user_requests " +
                 "WHERE status = 'Pending' " +
                 "ORDER BY requested_at DESC";

        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("user_type"),
                    rs.getInt("request_id"),
                    rs.getTimestamp("requested_at"),
                    rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading pending users:\n" + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    

    // UPDATE STATUS (APPROVE / REJECT)
    private void approveUserRequest(int requestId, String userType) {
    try (Connection con = DatabaseConnection.getConnection()) {
        con.setAutoCommit(false);
        
        try {
            // Get request details from user_requests table
            String getRequestQuery = "SELECT * FROM user_requests WHERE request_id = ?";
            PreparedStatement getPst = con.prepareStatement(getRequestQuery);
            getPst.setInt(1, requestId);
            ResultSet rs = getPst.executeQuery();
            
            if (rs.next()) {
                // Create user in users table
                String insertUserQuery = "INSERT INTO users (user_type, name, email, password, status) VALUES (?, ?, ?, ?, 'Active')";
                PreparedStatement userPst = con.prepareStatement(insertUserQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                userPst.setString(1, userType);
                userPst.setString(2, rs.getString("name"));
                userPst.setString(3, rs.getString("email"));
                userPst.setString(4, rs.getString("password"));
                userPst.executeUpdate();
                
                ResultSet generatedKeys = userPst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    
                    // Insert into specific table
                    if (userType.equals("Counselor")) {
                        String insertCounselorQuery = "INSERT INTO counselors (user_id, name, email, specialization, license_number, password, status) VALUES (?, ?, ?, ?, ?, ?, 'Active')";
                        PreparedStatement counselorPst = con.prepareStatement(insertCounselorQuery);
                        counselorPst.setInt(1, userId);
                        counselorPst.setString(2, rs.getString("name"));
                        counselorPst.setString(3, rs.getString("email"));
                        counselorPst.setString(4, rs.getString("specialization"));
                        counselorPst.setString(5, rs.getString("license_number"));
                        counselorPst.setString(6, rs.getString("password"));
                        counselorPst.executeUpdate();
                        
                    } else if (userType.equals("Student")) {
                        // FIXED: Now includes year_level
                        String insertStudentQuery = "INSERT INTO students (user_id, name, email, course, year_level, student_number, password, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')";
                        PreparedStatement studentPst = con.prepareStatement(insertStudentQuery);
                        studentPst.setInt(1, userId);
                        studentPst.setString(2, rs.getString("name"));
                        studentPst.setString(3, rs.getString("email"));
                        studentPst.setString(4, rs.getString("course"));
                        
                        // Handle year_level - use default if not present
                        String yearLevel = rs.getString("year_level");
                        if (yearLevel == null || yearLevel.trim().isEmpty()) {
                            yearLevel = "Not Specified"; // Default value
                        }
                        studentPst.setString(5, yearLevel);
                        
                        studentPst.setString(6, rs.getString("student_number"));
                        studentPst.setString(7, rs.getString("password"));
                        studentPst.executeUpdate();
                    }
                }
                
                // Update request status
                String updateRequestQuery = "UPDATE user_requests SET status = 'Approved', processed_at = NOW(), processed_by = ? WHERE request_id = ?";
                PreparedStatement updatePst = con.prepareStatement(updateRequestQuery);
                updatePst.setInt(1, currentAdminId);
                updatePst.setInt(2, requestId);
                updatePst.executeUpdate();
                
                con.commit();
                
                JOptionPane.showMessageDialog(this,
                    "User request approved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadPendingUsers();
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(this,
                    "Request not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            con.rollback();
            throw e;
        }
        
    } catch (Exception e) {
        System.err.println("Error approving request: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error approving request: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    private void rejectUserRequest(int requestId, String reason) {
        try (Connection con = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE user_requests SET status = 'Rejected', rejection_reason = ?, processed_at = NOW(), processed_by = ? WHERE request_id = ?";
            PreparedStatement pst = con.prepareStatement(updateQuery);
            pst.setString(1, reason);
            pst.setInt(2, currentAdminId);
            pst.setInt(3, requestId);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this,
                    "User request rejected",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                loadPendingUsers();
            }

        } catch (Exception e) {
            System.err.println("Error rejecting request: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error rejecting request: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


        // SEARCH FILTER
        private void setupSearch() {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            sorter = new TableRowSorter<>(model);
            jTable1.setRowSorter(sorter);

            // Setup placeholder
            Search.setText("Search");
            Search.setForeground(Color.GRAY);

            Search.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (Search.getText().equals("Search")) {
                        Search.setText("");
                        Search.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (Search.getText().isEmpty()) {
                        Search.setText("Search");
                        Search.setForeground(Color.GRAY);
                        if (sorter != null) {
                            sorter.setRowFilter(null);
                        }
                    }
                }
            });

            Search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            });
        }
        
        private void performSearch() {
            String searchText = Search.getText().trim();

            // Ignore placeholder text
            if (searchText.isEmpty() || searchText.equals("Search")) {
                sorter.setRowFilter(null);
                return;
            }

            String selectedFilter = (String) jComboBox1.getSelectedItem();

            try {
                // Search in specific column
                int columnIndex = getColumnIndex(selectedFilter);
                if (columnIndex != -1) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i).*" + escapeRegex(searchText) + ".*", columnIndex));
                }
            } catch (java.util.regex.PatternSyntaxException ex) {
                // If regex is invalid, clear the filter
                sorter.setRowFilter(null);
            }
        }
        
        private int getColumnIndex(String columnName) {
            switch (columnName) {
                case "Name": return 0;
                case "Type": return 1;
                case "ID": return 2;
                case "Date": return 3;
                case "Status": return 4;
                default: return -1;
            }
        }

        // Add the escapeRegex() helper method:
        private String escapeRegex(String text) {
            return text.replaceAll("([\\\\*+\\[\\](){}$.?^|])", "\\\\$1");
        }
        
    private void setupFilterComboBox() {
    // Clear existing items
        jComboBox1.removeAllItems();

        // Add filter options matching table columns
        jComboBox1.addItem("Name");
        jComboBox1.addItem("Type");
        jComboBox1.addItem("ID");
        jComboBox1.addItem("Date");
        jComboBox1.addItem("Status");

        // Add action listener for when selection changes
        jComboBox1.addActionListener(e -> performSearch());
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
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();

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
                .addContainerGap(170, Short.MAX_VALUE))
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
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Type", "ID", "Date", "Status"
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

        jButton2.setText("Home");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 195, 51));
        jLabel6.setText("Filter Search");

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
                .addGap(18, 18, 18)
                .addComponent(jButton2)
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
                        .addComponent(jLabel3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(logout)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(37, 37, 37)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        loadPendingUsers();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        admin_allusers au = new admin_allusers(currentAdminId, currentAdminName);
        au.setVisible(true);
        this.dispose();
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
        admin_dashboard d = new admin_dashboard(currentAdminId, currentAdminName);
        d.setVisible(true);
        this.dispose();
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

    // Get data from the selected row
    int requestId = (int) jTable1.getValueAt(row, 2);     // Column 2 is request_id
    String userName = jTable1.getValueAt(row, 0).toString(); // Column 0 is Name
    
    // Ask for rejection reason
    String reason = JOptionPane.showInputDialog(this,
            "Enter reason for rejection (optional):",
            "Reject User",
            JOptionPane.QUESTION_MESSAGE);
    
    // User cancelled the dialog
    if (reason == null) {
        return;
    }
    
    // Confirm rejection
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reject " + userName + "?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        rejectUserRequest(requestId, reason);
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

    // Get data from the selected row
    int requestId = (int) jTable1.getValueAt(row, 2);        // Column 2 is request_id
    String userName = jTable1.getValueAt(row, 0).toString();  // Column 0 is Name
    String userType = jTable1.getValueAt(row, 1).toString();  // Column 1 is Type
    
    // Confirm approval
    int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to approve " + userName + "?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        approveUserRequest(requestId, userType);
    }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();
        new admin_dashboard(currentAdminId, currentAdminName).setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        admin_dashboard c = new admin_dashboard(currentAdminId, currentAdminName);
        c.setVisible(true);
        this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new PendingUsers().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Search;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton logout;
    // End of variables declaration//GEN-END:variables
}
