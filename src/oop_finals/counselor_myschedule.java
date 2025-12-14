/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package oop_finals;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Admin
 */
public class counselor_myschedule extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(counselor_myschedule.class.getName());

    private int currentCounselorId;
    private String currentCounselorName;
    
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private int selectedCounselorId = -1;
    private Connection conn;
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    /**
     * Creates new form counselor_myschedule
     */
    public counselor_myschedule() {
        initComponents();
        initializeCalendar();
    }
    
     public counselor_myschedule(int counselorId, String counselorName) {
        initComponents();
        this.currentCounselorId = counselorId;
        this.currentCounselorName = counselorName;
        this.selectedCounselorId = counselorId;
        user.setText(currentCounselorName + "!");
        initializeCalendar();
        loadUpcomingAppointments();
    }
    
    private void initializeCalendar() {
        currentMonth = YearMonth.now();
        selectedDate = null;
        
        String[] columnNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 6) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jTable1.setModel(model);
        
        jTable1.setRowHeight(50);
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jTable1.setGridColor(Color.GRAY);
        jTable1.setShowGrid(true);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setCellSelectionEnabled(true);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setColumnSelectionAllowed(false);
        
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) jTable1.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(new Color(255, 195, 51));
        jTable1.getTableHeader().setForeground(Color.WHITE);
        
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

        // Removed the blocking check - counselors can now click blocked dates

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
    
    private boolean isDateBlocked(LocalDate date) {
    if (selectedCounselorId == -1) {
        return false;
    }
    
    try {
        Connection connection = getConnection();
        if (connection == null) return false;
        
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
        
        return isBlocked;
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error checking if date is blocked", e);
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
        YearMonth maxMonth = now.plusYears(1);
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

    // Missing method implementation
    private void updateMonthLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        currentmonth.setText(currentMonth.format(formatter));
    }

    // Missing method implementation
    private void updateNavigationButtons() {
        YearMonth now = YearMonth.now();
        YearMonth maxMonth = now.plusYears(1);
        
        previousmonth.setEnabled(currentMonth.isAfter(now));
        nextmonth.setEnabled(currentMonth.isBefore(maxMonth));
    }
    
    private void loadUpcomingAppointments() {
    try {
        Connection connection = getConnection();
        if (connection == null) return;
        
        // Query to get upcoming appointments for this counselor
        String query = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, " +
                      "s.name as student_name, s.year_level, a.reason " +
                      "FROM appointments a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "WHERE a.counselor_id = ? " +
                      "AND a.appointment_date >= CURDATE() " +
                      "AND a.status = 'Upcoming' " +
                      "ORDER BY a.appointment_date ASC, a.appointment_time ASC " +
                      "LIMIT 10";
        
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setInt(1, currentCounselorId);
        ResultSet rs = pst.executeQuery();
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Date", "Time", "Student Name", "Year Level", "Reason"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Populate table
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        
        while (rs.next()) {
            LocalDate date = rs.getDate("appointment_date").toLocalDate();
            LocalTime time = rs.getTime("appointment_time").toLocalTime();
            
            model.addRow(new Object[]{
                date.format(dateFormatter),
                time.format(timeFormatter),
                rs.getString("student_name"),
                rs.getString("year_level"),
                rs.getString("reason")
            });
        }
        
        
    } catch (SQLException e) {
        logger.log(java.util.logging.Level.SEVERE, "Error loading upcoming appointments", e);
        JOptionPane.showMessageDialog(this,
                "Error loading appointments: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Database connection error", e);
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        requestslogo = new javax.swing.JLabel();
        requests = new javax.swing.JButton();
        schedulelogo = new javax.swing.JLabel();
        viewprofile = new javax.swing.JButton();
        profilelogo = new javax.swing.JLabel();
        myschedule = new javax.swing.JButton();
        welcome = new javax.swing.JLabel();
        user = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        home = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        profilelogo2 = new javax.swing.JLabel();
        myschedule2 = new javax.swing.JLabel();
        edit = new javax.swing.JButton();
        add = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        previousmonth = new javax.swing.JButton();
        currentmonth = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        nextmonth = new javax.swing.JButton();
        appointments = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        requestslogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        requestslogo.setText("icon");
        requestslogo.setToolTipText("");

        requests.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        requests.setText("REQUESTS");
        requests.setBorderPainted(false);
        requests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                requestsActionPerformed(evt);
            }
        });

        schedulelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/myappointments.png"))); // NOI18N
        schedulelogo.setText("icon");

        viewprofile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        viewprofile.setText("VIEW PROFILE");
        viewprofile.setBorderPainted(false);
        viewprofile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewprofileActionPerformed(evt);
            }
        });

        profilelogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        profilelogo.setText("icon");

        myschedule.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myschedule.setText("MY SCHEDULE");
        myschedule.setBorderPainted(false);
        myschedule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myscheduleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(requestslogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(requests)
                .addGap(65, 65, 65)
                .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(myschedule)
                .addGap(64, 64, 64)
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
                    .addComponent(requests)
                    .addComponent(profilelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewprofile)
                    .addComponent(requestslogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(schedulelogo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myschedule))
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

        home.setBackground(new java.awt.Color(38, 36, 68));
        home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/logo.png"))); // NOI18N
        home.setBorderPainted(false);
        home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        profilelogo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/myappointments.png"))); // NOI18N
        profilelogo2.setText("icon");

        myschedule2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        myschedule2.setText("MY SCHEDULE");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(profilelogo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(myschedule2)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(profilelogo2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(myschedule2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        edit.setBackground(new java.awt.Color(255, 195, 51));
        edit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        edit.setForeground(new java.awt.Color(255, 255, 255));
        edit.setText("EDIT");
        edit.setBorderPainted(false);
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });

        add.setBackground(new java.awt.Color(255, 195, 51));
        add.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        add.setForeground(new java.awt.Color(255, 255, 255));
        add.setText("ADD");
        add.setBorderPainted(false);
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        delete.setBackground(new java.awt.Color(255, 195, 51));
        delete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        delete.setForeground(new java.awt.Color(255, 255, 255));
        delete.setText("DELETE");
        delete.setBorderPainted(false);
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

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

        nextmonth.setText("▶");
        nextmonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextmonthActionPerformed(evt);
            }
        });

        appointments.setBackground(new java.awt.Color(255, 195, 51));
        appointments.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        appointments.setForeground(new java.awt.Color(255, 255, 255));
        appointments.setText("APPOINTMENTS");
        appointments.setBorderPainted(false);
        appointments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointmentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(welcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(user)
                .addGap(37, 37, 37)
                .addComponent(logout)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(previousmonth)
                .addGap(91, 91, 91)
                .addComponent(currentmonth)
                .addGap(88, 88, 88)
                .addComponent(nextmonth)
                .addGap(251, 251, 251))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(appointments))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(edit, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(delete, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(84, Short.MAX_VALUE))
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
                    .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(appointments)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previousmonth)
                    .addComponent(currentmonth)
                    .addComponent(nextmonth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit)
                    .addComponent(add)
                    .addComponent(delete))
                .addGap(90, 90, 90))
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

    private void requestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_requestsActionPerformed
        // TODO add your handling code here:
        counselor_requests a = new counselor_requests(currentCounselorId, currentCounselorName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_requestsActionPerformed

    private void viewprofileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewprofileActionPerformed
        // TODO add your handling code here:
        counselor_viewprofile c = new counselor_viewprofile(currentCounselorId, currentCounselorName);
        c.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_viewprofileActionPerformed

    private void myscheduleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myscheduleActionPerformed
        // TODO add your handling code here:
        counselor_myschedule b = new counselor_myschedule(currentCounselorId, currentCounselorName);
        b.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_myscheduleActionPerformed

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

    private void homeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeActionPerformed
        // TODO add your handling code here:
        counselor_dashboard d = new counselor_dashboard(currentCounselorId, currentCounselorName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_homeActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        // TODO add your handling code here:
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a blocked date first.",
                    "No Date Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isDateBlocked(selectedDate)) {
            JOptionPane.showMessageDialog(this,
                    "This date is not blocked. Please select a blocked date to edit.",
                    "Date Not Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            // Get current reason
            String selectQuery = "SELECT reason FROM counselor_blocked_dates " +
                                "WHERE counselor_id = ? AND blocked_date = ?";
            PreparedStatement selectPst = connection.prepareStatement(selectQuery);
            selectPst.setInt(1, currentCounselorId);
            selectPst.setDate(2, java.sql.Date.valueOf(selectedDate));
            ResultSet rs = selectPst.executeQuery();
            
            String currentReason = "";
            if (rs.next()) {
                currentReason = rs.getString("reason");
            }
            rs.close();
            selectPst.close();
            
            // Ask for new reason
            String newReason = (String) JOptionPane.showInputDialog(this,
                    "Edit reason for blocking " + selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + ":",
                    "Edit Blocked Date",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    currentReason);
            
            if (newReason == null || newReason.trim().isEmpty()) {
                return; // User cancelled or entered empty reason
            }
            
            // Update reason
            String updateQuery = "UPDATE counselor_blocked_dates SET reason = ? " +
                                "WHERE counselor_id = ? AND blocked_date = ?";
            PreparedStatement updatePst = connection.prepareStatement(updateQuery);
            updatePst.setString(1, newReason.trim());
            updatePst.setInt(2, currentCounselorId);
            updatePst.setDate(3, java.sql.Date.valueOf(selectedDate));
            
            int result = updatePst.executeUpdate();
            updatePst.close();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Blocked date updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update blocked date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating blocked date", e);
            JOptionPane.showMessageDialog(this,
                    "Error updating blocked date: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_editActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        // TODO add your handling code here:
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a date first.",
                    "No Date Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusYears(1);
        
        if (selectedDate.isBefore(today)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot block a date in the past.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedDate.isAfter(maxDate)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot block a date more than 1 year in advance.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if date is already blocked
        if (isDateBlocked(selectedDate)) {
            JOptionPane.showMessageDialog(this,
                    "This date is already blocked.",
                    "Date Already Blocked",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Ask for reason
        String reason = JOptionPane.showInputDialog(this,
                "Enter reason for blocking " + selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + ":",
                "Block Date",
                JOptionPane.QUESTION_MESSAGE);
        
        if (reason == null || reason.trim().isEmpty()) {
            return; // User cancelled or entered empty reason
        }
        
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            String insertQuery = "INSERT INTO counselor_blocked_dates (counselor_id, blocked_date, reason, created_at) " +
                                "VALUES (?, ?, ?, NOW())";
            PreparedStatement pst = connection.prepareStatement(insertQuery);
            pst.setInt(1, currentCounselorId);
            pst.setDate(2, java.sql.Date.valueOf(selectedDate));
            pst.setString(3, reason.trim());
            
            int result = pst.executeUpdate();
            pst.close();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Date blocked successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                updateCalendarDisplay();
                loadUpcomingAppointments();
                jTable1.repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to block date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error blocking date", e);
            JOptionPane.showMessageDialog(this,
                    "Error blocking date: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        // TODO add your handling code here:
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a blocked date first.",
                    "No Date Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isDateBlocked(selectedDate)) {
            JOptionPane.showMessageDialog(this,
                    "This date is not blocked. Please select a blocked date to delete.",
                    "Date Not Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unblock " + 
                selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            Connection connection = getConnection();
            if (connection == null) return;
            
            String deleteQuery = "DELETE FROM counselor_blocked_dates " +
                                "WHERE counselor_id = ? AND blocked_date = ?";
            PreparedStatement pst = connection.prepareStatement(deleteQuery);
            pst.setInt(1, currentCounselorId);
            pst.setDate(2, java.sql.Date.valueOf(selectedDate));
            
            int result = pst.executeUpdate();
            pst.close();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                        "Date unblocked successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                selectedDate = null;
                updateCalendarDisplay();
                loadUpcomingAppointments();
                jTable1.repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to unblock date.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error deleting blocked date", e);
            JOptionPane.showMessageDialog(this,
                    "Error unblocking date: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deleteActionPerformed

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

    private void appointmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appointmentsActionPerformed
        // TODO add your handling code here:
        counselor_myscheduleappointments e = new counselor_myscheduleappointments(currentCounselorId, currentCounselorName);
        e.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_appointmentsActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new counselor_myschedule().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton appointments;
    private javax.swing.JLabel currentmonth;
    private javax.swing.JButton delete;
    private javax.swing.JButton edit;
    private javax.swing.JButton home;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton logout;
    private javax.swing.JButton myschedule;
    private javax.swing.JLabel myschedule2;
    private javax.swing.JButton nextmonth;
    private javax.swing.JButton previousmonth;
    private javax.swing.JLabel profilelogo;
    private javax.swing.JLabel profilelogo2;
    private javax.swing.JButton requests;
    private javax.swing.JLabel requestslogo;
    private javax.swing.JLabel schedulelogo;
    private javax.swing.JLabel user;
    private javax.swing.JButton viewprofile;
    private javax.swing.JLabel welcome;
    // End of variables declaration//GEN-END:variables
}
