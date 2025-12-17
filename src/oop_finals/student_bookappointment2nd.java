package oop_finals;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Second step of appointment booking - Date, Time, and Reason selection
 * 
 * Refactored version using AppointmentController (proper 3-layer architecture)
 * 
 * @author Admin
 * @version 3.0
 */
public class student_bookappointment2nd extends javax.swing.JFrame {
    
    // ============================================================================
    // CONSTANTS
    // ============================================================================
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(student_bookappointment2nd.class.getName());
    
    // Time slot constants
    private static final int WORK_START_HOUR = 8;
    private static final int WORK_END_HOUR = 16;
    private static final int LUNCH_START_HOUR = 12;
    private static final int SLOT_INTERVAL_MINUTES = 30;
    private static final int LAST_SLOT_MINUTE = 30; // 4:30 PM
    
    // Time format
    private static final String TIME_12H_FORMAT = "hh:mm a";
    
    // Validation messages
    private static final String MSG_SELECT_DATE = "Please select a date";
    private static final String MSG_SELECT_TIME = "Please select a time";
    
    // ============================================================================
    // INSTANCE VARIABLES
    // ============================================================================
    
    private final int currentStudentId;
    private final String currentStudentName;
    private final int selectedCounselorId;
    private final String selectedSpecialization;
    private final String selectedCounselorName;
    
    // Controller layer - handles business logic
    private final AppointmentController appointmentController;
    
    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================
    
    /**
     * Default constructor (for testing only)
     */
    public student_bookappointment2nd() {
        this(0, "Guest", 0, null, null, null);
    }
    
    /**
     * Full constructor with all booking context
     * 
     * @param studentId Student's ID
     * @param studentName Student's name
     * @param counselorId Selected counselor's ID
     * @param appointmentDate Pre-selected date (nullable)
     * @param specialization Selected specialization
     * @param counselorName Selected counselor's name
     */
    public student_bookappointment2nd(int studentId, String studentName, int counselorId, 
                                      java.util.Date appointmentDate, String specialization, 
                                      String counselorName) {
        // Validate inputs
        validateConstructorInputs(studentId, studentName, counselorId);
        
        // Initialize instance variables
        this.currentStudentId = studentId;
        this.currentStudentName = studentName;
        this.selectedCounselorId = counselorId;
        this.selectedSpecialization = specialization;
        this.selectedCounselorName = counselorName;
        
        // Initialize controller
        this.appointmentController = new AppointmentController();
        
        // Initialize UI components
        initComponents();
        setupUI(appointmentDate);
    }
    
    /**
     * Validate constructor inputs
     */
    private void validateConstructorInputs(int studentId, String studentName, int counselorId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be positive");
        }
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        if (counselorId <= 0) {
            throw new IllegalArgumentException("Counselor ID must be positive");
        }
    }
    
    // ============================================================================
    // UI SETUP
    // ============================================================================
    
    /**
     * Setup UI components after initialization
     */
    private void setupUI(java.util.Date preSelectedDate) {
        setLocationRelativeTo(null);
        jLabel5.setText(currentStudentName + "!");
        
        setupDateChooser(preSelectedDate);
        populateTimeSlots();
    }
    
    /**
     * Setup date chooser component
     */
    private void setupDateChooser(java.util.Date preSelectedDate) {
        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setMinSelectableDate(new java.util.Date());
        
        if (preSelectedDate != null) {
            jDateChooser1.setDate(preSelectedDate);
        }
    }
    
    // ============================================================================
    // TIME SLOT GENERATION
    // ============================================================================
    
    /**
     * Populate time slot combo box with available times
     */
    private void populateTimeSlots() {
        List<String> timeSlots = generateTimeSlots();
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(
            timeSlots.toArray(new String[0])
        ));
    }
    
    /**
     * Generate list of time slots
     * 
     * @return List of formatted time strings in 12-hour format
     */
    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        
        for (int hour = WORK_START_HOUR; hour <= WORK_END_HOUR; hour++) {
            // Skip lunch hour
            if (hour == LUNCH_START_HOUR) {
                continue;
            }
            
            // Generate 30-minute intervals
            for (int minute = 0; minute < 60; minute += SLOT_INTERVAL_MINUTES) {
                // Stop at 4:30 PM
                if (hour == WORK_END_HOUR && minute > LAST_SLOT_MINUTE) {
                    break;
                }
                
                String timeSlot = formatTimeSlot(hour, minute);
                timeSlots.add(timeSlot);
            }
        }
        
        return timeSlots;
    }
    
    /**
     * Format time slot to 12-hour format
     * 
     * @param hour Hour in 24-hour format (0-23)
     * @param minute Minute (0-59)
     * @return Formatted time string (e.g., "08:00 AM")
     */
    private String formatTimeSlot(int hour, int minute) {
        LocalTime time = LocalTime.of(hour, minute);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_12H_FORMAT);
        return time.format(formatter);
    }
    
    // ============================================================================
    // APPOINTMENT BOOKING LOGIC
    // ============================================================================
    
    /**
     * Main appointment booking method
     * Uses AppointmentController which handles all validation
     */
    private void bookAppointment() {
        try {
            // Get UI inputs
            java.util.Date selectedDateUtil = jDateChooser1.getDate();
            String selectedTimeStr = (String) jComboBox1.getSelectedItem();
            String reason = jTextArea1.getText().trim();
            
            // Basic UI validation
            if (selectedDateUtil == null) {
                showWarning(MSG_SELECT_DATE);
                return;
            }
            
            if (selectedTimeStr == null || selectedTimeStr.isEmpty()) {
                showWarning(MSG_SELECT_TIME);
                return;
            }
            
            // Convert to SQL types
            Date sqlDate = new Date(selectedDateUtil.getTime());
            Time sqlTime = parseTimeString(selectedTimeStr);
            
            // Create Appointment object
            Appointment appointment = new Appointment();
            appointment.setStudentId(currentStudentId);
            appointment.setCounselorId(selectedCounselorId);
            appointment.setAppointmentDate(sqlDate);
            appointment.setAppointmentTime(sqlTime);
            appointment.setReason(reason.isEmpty() ? null : reason);
            appointment.setStatus("Pending");
            
            // Use Controller to create (it validates everything!)
            String result = appointmentController.createAppointment(appointment);
            
            if ("SUCCESS".equals(result)) {
                handleBookingSuccess(sqlDate, sqlTime);
            } else {
                // Controller returned validation error message
                showError(result, "Booking Failed");
            }
            
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error booking appointment", e);
            showError("An unexpected error occurred: " + e.getMessage(), "Error");
        }
    }
    
    /**
     * Handle successful booking
     */
    private void handleBookingSuccess(Date date, Time time) {
        String message = String.format(
            "Appointment request submitted successfully!\n\n" +
            "Date: %s\n" +
            "Time: %s\n" +
            "Status: Pending approval\n\n" +
            "You will be notified once the counselor approves your appointment.",
            date.toString(),
            formatTime12Hour(time)
        );
        
        showSuccess(message);
        navigateToDashboard();
    }
    
    // ============================================================================
    // TIME CONVERSION
    // ============================================================================
    
    /**
     * Parse 12-hour time string to SQL Time
     * 
     * @param time12Hour Time in format "hh:mm AM/PM"
     * @return SQL Time object
     */
    private Time parseTimeString(String time12Hour) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_12H_FORMAT);
            LocalTime localTime = LocalTime.parse(time12Hour, formatter);
            return Time.valueOf(localTime);
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error parsing time: " + time12Hour, e);
            return Time.valueOf(LocalTime.of(8, 0)); // Default to 8:00 AM
        }
    }
    
    /**
     * Format SQL Time to 12-hour format
     * 
     * @param time SQL Time object
     * @return Formatted time string
     */
    private String formatTime12Hour(Time time) {
        LocalTime localTime = time.toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_12H_FORMAT);
        return localTime.format(formatter);
    }
    
    // ============================================================================
    // NAVIGATION
    // ============================================================================
    
    /**
     * Navigate to dashboard
     */
    private void navigateToDashboard() {
        student_dashboard dashboard = new student_dashboard(currentStudentId, currentStudentName);
        dashboard.setVisible(true);
        this.dispose();
    }
    
    /**
     * Navigate to booking page (step 1)
     */
    private void navigateToBookingPage() {
        student_bookappointment bookingPage = 
            new student_bookappointment(currentStudentId, currentStudentName);
        
        // Restore previous selections
        if (selectedSpecialization != null) {
            bookingPage.setSelectedSpecialization(selectedSpecialization);
        }
        if (selectedCounselorName != null) {
            bookingPage.setSelectedCounselor(selectedCounselorName);
        }
        
        bookingPage.setVisible(true);
        this.dispose();
    }
    
    /**
     * Navigate to my appointments
     */
    private void navigateToMyAppointments() {
        student_myappointment myAppointments = 
            new student_myappointment(currentStudentId, currentStudentName);
        myAppointments.setVisible(true);
        this.dispose();
    }
    
    /**
     * Navigate to profile
     */
    private void navigateToProfile() {
        student_viewprofile profile = 
            new student_viewprofile(currentStudentId, currentStudentName);
        profile.setVisible(true);
        this.dispose();
    }
    
    /**
     * Handle logout
     */
    private void handleLogout() {
        int confirmation = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                this.dispose();
                new login_page().setVisible(true);
            } catch (Exception e) {
                logger.log(java.util.logging.Level.SEVERE, "Error during logout", e);
                showError("Error during logout", "Error");
            }
        }
    }
    
    // ============================================================================
    // UI HELPER METHODS
    // ============================================================================
    
    /**
     * Show warning message
     */
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Validation Error", 
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Show error message
     */
    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            title, 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show success message
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this, 
            message, 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // ============================================================================
    // EVENT HANDLERS (Called by GUI components)
    // ============================================================================
    
    /**
     * Handle submit button click
     */
    private void handleSubmitClick() {
        bookAppointment();
    }
    
    /**
     * Handle back button click
     */
    private void handleBackClick() {
        navigateToBookingPage();
    }
    
    /**
     * Handle home button click
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(38, 36, 68));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("BOOK APPOINTMENT");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton5.setText("VIEW PROFILE");
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton6.setText("MY APPOINTMENTS");
        jButton6.setBorderPainted(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/profile.png"))); // NOI18N
        jLabel10.setText("icon");

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/appointments.png"))); // NOI18N
        jLabel11.setText("icon");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oop_finals/images/book appointment.png"))); // NOI18N
        jLabel2.setText("icon");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(42, 42, 42)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addGap(49, 49, 49)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(jButton5)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("WELCOME,");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("USER!");

        jButton7.setBackground(new java.awt.Color(204, 0, 0));
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("LOGOUT");
        jButton7.setBorderPainted(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
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

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 195, 51));
        jLabel6.setText("CHOOSE DATE & TIME");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("BOOK APPOINTMENT");

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
                .addComponent(jLabel7)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setBackground(new java.awt.Color(255, 195, 51));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("SUBMIT REQUEST");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 195, 51));
        jLabel9.setText("REASON (OPTIONAL)");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton2.setText("Home");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(37, 37, 37)
                .addComponent(jButton7)
                .addGap(38, 38, 38))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addGap(482, 482, 482)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(52, 52, 52))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jButton2)))
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton1))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        student_bookappointment a = new student_bookappointment(currentStudentId, currentStudentName);
        a.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        navigateToMyAppointments();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        handleLogout();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        student_dashboard d = new student_dashboard(currentStudentId, currentStudentName);
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        handleSubmitClick();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        navigateToProfile();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        navigateToDashboard();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        handleBackClick();
    }//GEN-LAST:event_jButton3ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new student_bookappointment2nd().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
