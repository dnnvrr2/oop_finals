/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

/**
 *
 * @author Admin
 */
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.sql.*;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;


public class student_bookappointment extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(student_bookappointment.class.getName());
    
    private Connection conn;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private int selectedCounselorId = -1;
    
    private int currentStudentId;
    private String currentStudentName;

    // Add this method to initialize the calendar in initComponents() or constructor
    public student_bookappointment() {
        initComponents();
        setLocationRelativeTo(null);
        loadSpecializations();
        counselordetails.setEditable(false);
        counselordetails.setText("Select a specialization and counselor to view details.");
        initializeCalendar();
    }


    public student_bookappointment(int studentId, String studentName) {
        initComponents();
        this.currentStudentId = studentId;
        this.currentStudentName = studentName;
        setLocationRelativeTo(null);
        user.setText(studentName + "!");
        loadSpecializations();
        counselordetails.setEditable(false);
        counselordetails.setText("Select a specialization and counselor to view details.");
        initializeCalendar();
    }

    private void initializeCalendar() {
        currentMonth = YearMonth.now();
        selectedDate = null;
        
        // Setup table model with day headers
        String[] columnNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 6) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable1.setModel(model);
        
        // Configure table appearance
        jTable1.setRowHeight(50);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.setGridColor(Color.GRAY);
        jTable1.setShowGrid(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setCellSelectionEnabled(true);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setColumnSelectionAllowed(false);
        
        // Center align column headers
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) jTable1.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(new Color(255, 195, 51));
        jTable1.getTableHeader().setForeground(Color.WHITE);
        
        // Custom cell renderer
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel cell = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setVerticalAlignment(SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                cell.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                if (value == null || value.toString().trim().isEmpty()) {
                    cell.setBackground(new Color(240, 240, 240));
                    cell.setForeground(Color.GRAY);
                    cell.setText("");
                } else {
                    String cellText = value.toString();
                    LocalDate cellDate = getCellDate(row, column);
                    LocalDate today = LocalDate.now();
                    
                    cell.setBackground(Color.WHITE);
                    cell.setForeground(Color.BLACK);
                    
                    if (selectedCounselorId != -1 && cellDate != null) {
                        if (!isDateAvailableForCounselor(cellDate)) {
                            cell.setBackground(new Color(255, 200, 200));
                            cell.setForeground(new Color(150, 150, 150));
                            cell.setText("<html><strike>" + cellText + "</strike></html>");
                        }
                    }
                    
                    if (cellDate != null && cellDate.equals(today)) {
                        cell.setBackground(new Color(173, 216, 230));
                        cell.setFont(cell.getFont().deriveFont(Font.BOLD));
                    }
                    
                    if (selectedDate != null && cellDate != null && cellDate.equals(selectedDate)) {
                        cell.setBackground(new Color(255, 195, 51));
                        cell.setForeground(Color.WHITE);
                        cell.setFont(cell.getFont().deriveFont(Font.BOLD, 16f));
                    }
                    
                    if (cellDate != null && cellDate.isBefore(today)) {
                        cell.setForeground(new Color(180, 180, 180));
                    }
                    
                    if (!cellText.contains("<html>")) {
                        cell.setText(cellText);
                    }
                }
                
                return cell;
            }
        });
        
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = jTable1.getSelectedRow();
                int col = jTable1.getSelectedColumn();
                
                if (row >= 0 && col >= 0) {
                    handleDateSelection(row, col);
                }
            }
        });
        
        updateCalendarDisplay();
        updateMonthLabel(); 
        updateNavigationButtons();
    }

private void updateCalendarDisplay() {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                model.setValueAt("", i, j);
            }
        }
        
        LocalDate firstDay = currentMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentMonth.lengthOfMonth();
        
        int day = 1;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if ((i == 0 && j < startDayOfWeek) || day > daysInMonth) {
                    model.setValueAt("", i, j);
                } else {
                    model.setValueAt(String.valueOf(day), i, j);
                    day++;
                }
            }
            if (day > daysInMonth) break;
        }
        
        jTable1.clearSelection();
    }


private void handleDateSelection(int row, int col) {
    Object value = jTable1.getValueAt(row, col);
        
        if (value == null || value.toString().trim().isEmpty()) {
            return;
        }
        
        LocalDate clickedDate = getCellDate(row, col);
        
        if (clickedDate == null) {
            return;
        }
        
        if (clickedDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                    "Cannot select a date in the past.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            jTable1.clearSelection();
            return;
        }
        
        if (selectedCounselorId != -1) {
            if (!isDateAvailableForCounselor(clickedDate)) {
                String dayName = clickedDate.getDayOfWeek()
                        .getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
                
                JOptionPane.showMessageDialog(this,
                        "The counselor is not available on " + dayName + "s or this date is blocked.\n" +
                        "Please select another date.",
                        "Date Unavailable",
                        JOptionPane.WARNING_MESSAGE);
                jTable1.clearSelection();
                return;
            }
        }
        
        selectedDate = clickedDate;
        jTable1.repaint();
    }


    private LocalDate getCellDate(int row, int col) {
        Object value = jTable1.getValueAt(row, col);
        
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        
        try {
            String cellText = value.toString();
            cellText = cellText.replaceAll("<[^>]*>", "");
            
            int day = Integer.parseInt(cellText.trim());
            return currentMonth.atDay(day);
        } catch (NumberFormatException | java.time.DateTimeException e) {
            return null;
        }
    }

    private boolean isDateAvailableForCounselor(LocalDate date) {
        if (selectedCounselorId == -1) {
            return true;
        }
        
        try {
            Connection connection = getConnection();
            if (connection == null) return false;
            
            String dayOfWeek = date.getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault());
            
            String schedQuery = "SELECT COUNT(*) as count FROM counselor_schedules " +
                               "WHERE counselor_id = ? AND day_of_week = ? AND is_available = TRUE";
            PreparedStatement schedPst = connection.prepareStatement(schedQuery);
            schedPst.setInt(1, selectedCounselorId);
            schedPst.setString(2, dayOfWeek);
            ResultSet schedRs = schedPst.executeQuery();
            
            boolean dayAvailable = false;
            if (schedRs.next()) {
                dayAvailable = schedRs.getInt("count") > 0;
            }
            schedRs.close();
            schedPst.close();
            
            if (!dayAvailable) {
                return false;
            }
            
            String blockedQuery = "SELECT COUNT(*) as count FROM counselor_blocked_dates " +
                                 "WHERE counselor_id = ? AND blocked_date = ?";
            PreparedStatement blockedPst = connection.prepareStatement(blockedQuery);
            blockedPst.setInt(1, selectedCounselorId);
            blockedPst.setDate(2, java.sql.Date.valueOf(date));
            ResultSet blockedRs = blockedPst.executeQuery();
            
            boolean isBlocked = false;
            if (blockedRs.next()) {
                isBlocked = blockedRs.getInt("count") > 0;
            }
            blockedRs.close();
            blockedPst.close();
            
            return !isBlocked;
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error checking date availability", e);
            return false;
        }
    }

public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void navigateToPreviousMonth() {
        YearMonth now = YearMonth.now();
        
            if (currentMonth.isAfter(now)) {
            currentMonth = currentMonth.minusMonths(1);
            selectedDate = null;
            updateCalendarDisplay();
        }
    }

    public void navigateToNextMonth() {
        YearMonth now = YearMonth.now();
        YearMonth maxMonth = now.plusMonths(1);
            if (currentMonth.isBefore(maxMonth)) {
            currentMonth = currentMonth.plusMonths(1);
            selectedDate = null;
            updateCalendarDisplay();
        }
    }

    public void navigateToToday() {
        currentMonth = YearMonth.now();
        selectedDate = null;
        updateCalendarDisplay();
        updateMonthLabel();
        updateNavigationButtons();
    }

// ============================================================================
// DATABASE METHODS
// ============================================================================

    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Database connection error", e);
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void loadSpecializations() {
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            String query = "SELECT DISTINCT specialization FROM counselors WHERE status = 'Active' ORDER BY specialization";
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Select Specialization --");
            
            while (rs.next()) {
                model.addElement(rs.getString("specialization"));
            }
            
            specialization.setModel(model);
            rs.close();
            pst.close();
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading specializations", e);
            JOptionPane.showMessageDialog(this, "Error loading specializations: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCounselorsBySpecialization(String specialization) {
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            String query = "SELECT counselor_id, name FROM counselors WHERE specialization = ? AND status = 'Active' ORDER BY name";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, specialization);
            ResultSet rs = pst.executeQuery();
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Select Counselor --");
            
            while (rs.next()) {
                String counselorDisplay = rs.getString("name");
                model.addElement(counselorDisplay);
            }
            
            counselor.setModel(model);
            counselordetails.setText("");
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading counselors", e);
            JOptionPane.showMessageDialog(this, "Error loading counselors: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCounselorInfo(String counselorName) {
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            String query = "SELECT * FROM counselors WHERE name = ? AND status = 'Active'";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, counselorName);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                StringBuilder info = new StringBuilder();
                info.append("═══════════════════════════════════\n");
                info.append("           COUNSELOR DETAILS\n");
                info.append("═══════════════════════════════════\n\n");
                info.append("Name: ").append(rs.getString("name")).append("\n");
                info.append("Specialization: ").append(rs.getString("specialization")).append("\n");
                info.append("Email: ").append(rs.getString("email")).append("\n");
                info.append("License Number: ").append(rs.getString("license_number")).append("\n");
                info.append("Status: ").append(rs.getString("status")).append("\n\n");
                info.append("═══════════════════════════════════\n");
                
                counselordetails.setText(info.toString());
            } else {
                counselordetails.setText("Counselor information not found.");
            }
            
            rs.close();
            pst.close();
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error loading counselor info", e);
            JOptionPane.showMessageDialog(this, "Error loading counselor information: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getSelectedCounselorId() {
        try {
            String selectedCounselor = (String) counselor.getSelectedItem();
            if (selectedCounselor == null || selectedCounselor.equals("-- Select Counselor --")) {
                return -1;
            }
            
            Connection connection = getConnection();
            if (connection == null) return -1;
            
            String query = "SELECT counselor_id FROM counselors WHERE name = ? AND status = 'Active'";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, selectedCounselor);
            ResultSet rs = pst.executeQuery();
            
            int counselorId = -1;
            if (rs.next()) {
                counselorId = rs.getInt("counselor_id");
            }
            
            rs.close();
            pst.close();
            return counselorId;
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error getting counselor ID", e);
            return -1;
        }
    }

    @Override
    public void dispose() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error closing connection", e);
        }
        super.dispose();
    }
    /**
     * Creates new form student_bookappointment
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner1 = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        bookappointment = new javax.swing.JButton();
        viewprofile = new javax.swing.JButton();
        myappointmets = new javax.swing.JButton();
        profilelogo = new javax.swing.JLabel();
        appointmentlogo = new javax.swing.JLabel();
        booklogo = new javax.swing.JLabel();
        welcome = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        logo_home = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        book = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        nextpage = new javax.swing.JButton();
        specialization = new javax.swing.JComboBox<>();
        counselor = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        counselordetails = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        previousmonth = new javax.swing.JButton();
        currentmonth = new javax.swing.JLabel();
        nextmonth = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        bookappointment.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bookappointment.setText("BOOK APPOINTMENT");
        bookappointment.setBorderPainted(false);
        bookappointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookappointmentActionPerformed(evt);
            }
        });

        viewprofile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewprofile.setText("VIEW PROFILE");
        viewprofile.setBorderPainted(false);
        viewprofile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewprofileActionPerformed(evt);
            }
        });

        myappointmets.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myappointmets.setText("MY APPOINTMENTS");
        myappointmets.setBorderPainted(false);
        myappointmets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myappointmetsActionPerformed(evt);
            }
        });

        profilelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        profilelogo.setText("icon");

        appointmentlogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        appointmentlogo.setText("icon");

        booklogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/book appointment.png"))); // NOI18N
        booklogo.setText("icon");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(booklogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bookappointment)
                .addGap(42, 42, 42)
                .addComponent(appointmentlogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myappointmets)
                .addGap(49, 49, 49)
                .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewprofile)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bookappointment)
                    .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myappointmets)
                    .addComponent(viewprofile)
                    .addComponent(booklogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appointmentlogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        logo_home.setBackground(new java.awt.Color(38, 36, 68));
        logo_home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        logo_home.setBorderPainted(false);
        logo_home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logo_homeActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 195, 51));
        jLabel6.setText("SELECT COUNSELOR");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        book.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        book.setText("BOOK APPOINTMENT");

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
                .addComponent(book)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(book)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nextpage.setBackground(new java.awt.Color(255, 195, 51));
        nextpage.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nextpage.setForeground(new java.awt.Color(255, 255, 255));
        nextpage.setText("NEXT");
        nextpage.setBorderPainted(false);
        nextpage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextpageActionPerformed(evt);
            }
        });

        specialization.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        specialization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specializationActionPerformed(evt);
            }
        });

        counselor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        counselor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                counselorActionPerformed(evt);
            }
        });

        counselordetails.setColumns(20);
        counselordetails.setRows(5);
        jScrollPane2.setViewportView(counselordetails);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 195, 51));
        jLabel3.setText("SPECIALIZATION");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 195, 51));
        jLabel8.setText("COUNSELOR");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        previousmonth.setText("◀");
        previousmonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousmonthActionPerformed(evt);
            }
        });

        currentmonth.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        currentmonth.setForeground(new java.awt.Color(255, 255, 255));
        currentmonth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentmonth.setText("Month Year");

        nextmonth.setText("▶");
        nextmonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextmonthActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(logo_home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(welcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user)
                .addGap(37, 37, 37)
                .addComponent(logout)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(nextpage, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel3)
                                                    .addComponent(specialization, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel8)
                                                    .addComponent(counselor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                            .addComponent(jScrollPane2))
                                        .addGap(18, 18, 18))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(296, 296, 296)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(previousmonth)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(currentmonth)
                                        .addGap(64, 64, 64)
                                        .addComponent(nextmonth))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(40, 40, 40))
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
                            .addComponent(user)))
                    .addComponent(logo_home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previousmonth)
                    .addComponent(currentmonth)
                    .addComponent(nextmonth))
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(specialization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(counselor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(nextpage)
                .addGap(45, 45, 45))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateMonthLabel() {
        // Format: "January 2025"
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy");
        currentmonth.setText(currentMonth.format(formatter));
    }
    
    private void updateNavigationButtons() {
        YearMonth now = YearMonth.now();
        YearMonth maxMonth = now.plusMonths(1); // Only allow up to next month

        // Disable previous button if we're at current month
        previousmonth.setEnabled(!currentMonth.equals(now));

        // Disable next button if we're at max month (next month)
        nextmonth.setEnabled(!currentMonth.equals(maxMonth));
    }
    
    private void bookappointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookappointmentActionPerformed
        // TODO add your handling code here:
        student_bookappointment a = new student_bookappointment(currentStudentId, currentStudentName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bookappointmentActionPerformed

    private void viewprofileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewprofileActionPerformed
        // TODO add your handling code here:
        student_viewprofile b = new student_viewprofile(currentStudentId, currentStudentName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_viewprofileActionPerformed

    private void myappointmetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myappointmetsActionPerformed
        // TODO add your handling code here:
        student_myappointment c = new student_myappointment(currentStudentId, currentStudentName);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_myappointmetsActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
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
    }//GEN-LAST:event_logoutActionPerformed

    private void logo_homeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logo_homeActionPerformed
        // TODO add your handling code here:
        student_dashboard d = new student_dashboard(currentStudentId, currentStudentName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logo_homeActionPerformed

    private void nextpageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextpageActionPerformed
        // TODO add your handling code here:                                                                             
        String selectedSpecialization = (String) specialization.getSelectedItem();
        String selectedCounselor = (String) counselor.getSelectedItem();

        if (selectedSpecialization == null || selectedSpecialization.equals("-- Select Specialization --")) {
            JOptionPane.showMessageDialog(this, "Please select a specialization before proceeding.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCounselor == null || selectedCounselor.equals("-- Select Counselor --")) {
            JOptionPane.showMessageDialog(this, "Please select a counselor before proceeding.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int counselorId = getSelectedCounselorId();

        if (counselorId == -1) {
               JOptionPane.showMessageDialog(this, "Error retrieving counselor information.",
                       "Error", JOptionPane.ERROR_MESSAGE);
               return;
           }

           // Pass null for the date parameter or handle it in the next form
           student_bookappointment2nd d = new student_bookappointment2nd(
               currentStudentId, 
               currentStudentName, 
               counselorId, 
               null  // Pass null instead of selectedDate
           );
           d.setVisible(true);
           this.dispose();
    }//GEN-LAST:event_nextpageActionPerformed

    private void counselorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_counselorActionPerformed
        // TODO add your handling code here:
        String selectedCounselor = (String) counselor.getSelectedItem();

        if (selectedCounselor != null && !selectedCounselor.equals("-- Select Counselor --")) {
            displayCounselorInfo(selectedCounselor);

            // Update selected counselor ID for calendar
            selectedCounselorId = getSelectedCounselorId();

            // Refresh calendar to show availability
            if (selectedCounselorId != -1) {
                selectedDate = null; // Clear selection when changing counselor
                updateCalendarDisplay();
            }
        } else {
            counselordetails.setText("Select a counselor to view details.");
            selectedCounselorId = -1;
            selectedDate = null;
            updateCalendarDisplay();
        }
    }//GEN-LAST:event_counselorActionPerformed

    private void specializationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specializationActionPerformed
        // TODO add your handling code here:
        String selectedSpecialization = (String) specialization.getSelectedItem();

        if (selectedSpecialization != null && !selectedSpecialization.equals("-- Select Specialization --")) {
            loadCounselorsBySpecialization(selectedSpecialization);
        } else {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Select Counselor --");
            counselor.setModel(model);
            counselordetails.setText("Select a specialization and counselor to view details.");
        }
    }//GEN-LAST:event_specializationActionPerformed

    private void previousmonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousmonthActionPerformed
        // TODO add your handling code here:
        navigateToPreviousMonth();
        updateMonthLabel();
        updateCalendarDisplay();
        updateNavigationButtons();
    }//GEN-LAST:event_previousmonthActionPerformed

    private void nextmonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextmonthActionPerformed
        // TODO add your handling code here:
        navigateToNextMonth();
        updateMonthLabel();
        updateCalendarDisplay();
        updateNavigationButtons();
    }//GEN-LAST:event_nextmonthActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new student_bookappointment().setVisible(true));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appointmentlogo;
    private javax.swing.JLabel book;
    private javax.swing.JButton bookappointment;
    private javax.swing.JLabel booklogo;
    private javax.swing.JComboBox<String> counselor;
    private javax.swing.JTextArea counselordetails;
    private javax.swing.JLabel currentmonth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton logo_home;
    private javax.swing.JButton logout;
    private javax.swing.JButton myappointmets;
    private javax.swing.JButton nextmonth;
    private javax.swing.JButton nextpage;
    private javax.swing.JButton previousmonth;
    private javax.swing.JLabel profilelogo;
    private javax.swing.JComboBox<String> specialization;
    private javax.swing.JLabel user;
    private javax.swing.JButton viewprofile;
    private javax.swing.JLabel welcome;
    // End of variables declaration//GEN-END:variables
}
