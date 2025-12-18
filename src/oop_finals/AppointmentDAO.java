package oop_finals;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Appointment Data Access Object
 * Handles all database operations for appointments
 * Works with AppointmentController for business logic
 * 
 * @author Admin
 * @version 1.0
 */
public class AppointmentDAO {
    
    private static final Logger logger = Logger.getLogger(AppointmentDAO.class.getName());
    
    // Database configuration
    private static final String DB_URL = System.getenv().getOrDefault(
        "DB_URL", "jdbc:mysql://localhost:3306/guidance_appointment_system");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "09@denverrr");
    
    // ============================================================================
    // SQL QUERIES
    // ============================================================================
    
    private static final String INSERT_APPOINTMENT_SQL = 
        "INSERT INTO appointments (student_id, counselor_id, appointment_date, " +
        "appointment_time, reason, status, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
    
    private static final String CHECK_TIME_SLOT_SQL =
        "SELECT COUNT(*) as count FROM appointments " +
        "WHERE counselor_id = ? " +
        "AND appointment_date = ? " +
        "AND appointment_time = ? " +
        "AND status IN ('Upcoming', 'Pending')";
    
    private static final String GET_STUDENT_APPOINTMENTS_SQL =
        "SELECT a.*, c.name as counselor_name, c.specialization " +
        "FROM appointments a " +
        "JOIN counselors c ON a.counselor_id = c.counselor_id " +
        "WHERE a.student_id = ? %s " +
        "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
    
    private static final String GET_COUNSELOR_APPOINTMENTS_SQL =
        "SELECT a.*, s.name as student_name, s.email as student_email, " +
        "s.year_level as student_year_level, s.course as student_course " +  // ADD THIS
        "FROM appointments a " +
        "JOIN students s ON a.student_id = s.student_id " +
        "WHERE a.counselor_id = ? %s " +
        "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
    
    private static final String GET_APPOINTMENT_BY_ID_SQL =
        "SELECT a.*, c.name as counselor_name, s.name as student_name " +
        "FROM appointments a " +
        "LEFT JOIN counselors c ON a.counselor_id = c.counselor_id " +
        "LEFT JOIN students s ON a.student_id = s.student_id " +
        "WHERE a.appointment_id = ?";
    
    private static final String GET_APPOINTMENT_ID_SQL =
        "SELECT a.appointment_id " +
        "FROM appointments a " +
        "JOIN counselors c ON a.counselor_id = c.counselor_id " +
        "WHERE a.student_id = ? " +
        "AND a.appointment_date = ? " +
        "AND a.appointment_time = ? " +
        "AND c.name = ?";
    
    private static final String UPDATE_STATUS_SQL =
        "UPDATE appointments SET status = ?, updated_at = NOW() WHERE appointment_id = ?";
    
    private static final String CANCEL_APPOINTMENT_SQL =
        "UPDATE appointments SET status = 'Cancelled', updated_at = NOW() WHERE appointment_id = ?";
    
    // ============================================================================
    // CREATE OPERATIONS
    // ============================================================================
    
    /**
     * Create a new appointment
     * 
     * @param appointment Appointment object with all details
     * @return true if appointment created successfully, false otherwise
     */
    public boolean createAppointment(Appointment appointment) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_APPOINTMENT_SQL, 
                                                            Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, appointment.getStudentId());
            pstmt.setInt(2, appointment.getCounselorId());
            pstmt.setDate(3, appointment.getAppointmentDate());
            pstmt.setTime(4, appointment.getAppointmentTime());
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getStatus() != null ? appointment.getStatus() : "Pending");
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        appointment.setAppointmentId(rs.getInt(1));
                    }
                }
                logger.info("Appointment created successfully for student: " + appointment.getStudentId());
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating appointment", e);
            return false;
        }
    }
    
    // ============================================================================
    // READ OPERATIONS
    // ============================================================================
    
    /**
     * Get appointments for a student with optional status filter
     * 
     * @param studentId Student's ID
     * @param statuses Optional status filters (e.g., "Upcoming", "Pending")
     * @return List of appointments
     */
    public List<Appointment> getStudentAppointments(int studentId, String... statuses) {
        List<Appointment> appointments = new ArrayList<>();
        
        // Build status filter
        String statusFilter = buildStatusFilter(statuses);
        String sql = String.format(GET_STUDENT_APPOINTMENTS_SQL, statusFilter);
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            
            // Set status parameters if provided
            if (statuses != null && statuses.length > 0) {
                for (int i = 0; i < statuses.length; i++) {
                    pstmt.setString(i + 2, statuses[i]);
                }
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(extractAppointmentFromResultSet(rs));
                }
            }
            
            logger.info("Retrieved " + appointments.size() + " appointments for student: " + studentId);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting appointments for student", e);
        }
        
        return appointments;
    }
    
    /**
     * Get appointments for a counselor with optional status filter
     * 
     * @param counselorId Counselor's ID
     * @param statuses Optional status filters
     * @return List of appointments
     */
    public List<Appointment> getCounselorAppointments(int counselorId, String... statuses) {
        List<Appointment> appointments = new ArrayList<>();
        
        String statusFilter = buildStatusFilter(statuses);
        String sql = String.format(GET_COUNSELOR_APPOINTMENTS_SQL, statusFilter);
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, counselorId);
            
            if (statuses != null && statuses.length > 0) {
                for (int i = 0; i < statuses.length; i++) {
                    pstmt.setString(i + 2, statuses[i]);
                }
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(extractAppointmentFromResultSet(rs));
                }
            }
            
            logger.info("Retrieved " + appointments.size() + " appointments for counselor: " + counselorId);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting appointments for counselor", e);
        }
        
        return appointments;
    }
    
    /**
     * Get appointment by ID
     * 
     * @param appointmentId Appointment ID
     * @return Appointment object or null if not found
     */
    public Appointment getAppointmentById(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_APPOINTMENT_BY_ID_SQL)) {
            
            pstmt.setInt(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAppointmentFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting appointment by ID", e);
        }
        
        return null;
    }
    
    /**
     * Get appointment ID from table data
     * 
     * @param studentId Student ID
     * @param date Appointment date
     * @param time Appointment time
     * @param counselorName Counselor name
     * @return Appointment ID or -1 if not found
     */
    public int getAppointmentId(int studentId, Date date, Time time, String counselorName) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_APPOINTMENT_ID_SQL)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setDate(2, date);
            pstmt.setTime(3, time);
            pstmt.setString(4, counselorName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("appointment_id");
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting appointment ID", e);
        }
        
        return -1;
    }
    
    /**
     * Check if a time slot is available for a counselor
     * 
     * @param counselorId Counselor's ID
     * @param date Appointment date
     * @param time Appointment time
     * @return true if available, false if taken
     */
    public boolean isTimeSlotAvailable(int counselorId, Date date, Time time) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CHECK_TIME_SLOT_SQL)) {
            
            pstmt.setInt(1, counselorId);
            pstmt.setDate(2, date);
            pstmt.setTime(3, time);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") == 0; // Available if count is 0
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking time slot availability", e);
        }
        
        return false; // Assume not available on error
    }
    
    // ============================================================================
    // UPDATE OPERATIONS
    // ============================================================================
    
    /**
     * Update appointment status
     * 
     * @param appointmentId Appointment ID
     * @param status New status
     * @return true if update successful, false otherwise
     */
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_STATUS_SQL)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Appointment " + appointmentId + " status updated to: " + status);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating appointment status", e);
            return false;
        }
    }
    
    /**
     * Cancel an appointment
     * 
     * @param appointmentId Appointment ID
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelAppointment(int appointmentId) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CANCEL_APPOINTMENT_SQL)) {
            
            pstmt.setInt(1, appointmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Appointment " + appointmentId + " cancelled successfully");
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error cancelling appointment", e);
            return false;
        }
    }
    
    // ============================================================================
    // HELPER METHODS
    // ============================================================================
    
    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            logger.severe("MySQL Driver not found: " + e.getMessage());
            throw new SQLException("Database driver error", e);
        }
    }
    
    /**
     * Build status filter SQL clause
     * 
     * @param statuses Array of status values
     * @return SQL WHERE clause for statuses
     */
    private String buildStatusFilter(String... statuses) {
        if (statuses == null || statuses.length == 0) {
            return "";
        }

        // FIX: Qualify 'status' with table alias 'a' to avoid ambiguity
        StringBuilder filter = new StringBuilder("AND a.status IN (");
        for (int i = 0; i < statuses.length; i++) {
            filter.append("?");
            if (i < statuses.length - 1) {
                filter.append(", ");
            }
        }
        filter.append(")");

        return filter.toString();
    }
    
    /**
     * Extract Appointment object from ResultSet
     * 
     * @param rs ResultSet from query
     * @return Appointment object
     * @throws SQLException if error reading ResultSet
     */
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();

        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setStudentId(rs.getInt("student_id"));
        appointment.setCounselorId(rs.getInt("counselor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date"));
        appointment.setAppointmentTime(rs.getTime("appointment_time"));
        appointment.setReason(rs.getString("reason"));
        appointment.setStatus(rs.getString("status"));
        appointment.setCreatedAt(rs.getTimestamp("created_at"));
        appointment.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Optional fields from joins
        try {
            appointment.setCounselorName(rs.getString("counselor_name"));
        } catch (SQLException e) {
            // Field might not exist in all queries
        }

        try {
            appointment.setStudentName(rs.getString("student_name"));
        } catch (SQLException e) {
            // Field might not exist in all queries
        }

        try {
            appointment.setStudentEmail(rs.getString("student_email"));
        } catch (SQLException e) {
            // Field might not exist in all queries
        }

        // ADD THESE:
        try {
            appointment.setStudentYearLevel(rs.getString("student_year_level"));
        } catch (SQLException e) {
            // Field might not exist in all queries
        }

        try {
            appointment.setStudentCourse(rs.getString("student_course"));
        } catch (SQLException e) {
            // Field might not exist in all queries
        }

        return appointment;
    }

    // Add this method to your AppointmentDAO class

/**
 * Cancel appointment by counselor with reason
 * 
 * @param appointmentId Appointment ID
 * @param reason Cancellation reason
 * @return true if successful, false otherwise
 */
public boolean cancelAppointmentByCounselor(int appointmentId, String reason) {
    // First, add the reason to an appointments_history or notifications table if you have one
    // For now, we'll just update the status and log the reason
    
    String query = "UPDATE appointments SET status = 'Cancelled', " +
                  "updated_at = NOW() WHERE appointment_id = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, appointmentId);
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            // If you have a cancellations table to log the reason:
            logCancellation(conn, appointmentId, reason);
            
            logger.info("Appointment " + appointmentId + " cancelled by counselor. Reason: " + reason);
            return true;
        }
        
        return false;
        
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Error cancelling appointment by counselor", e);
        return false;
    }
}

/**
 * Log cancellation with reason (optional - if you have a cancellations table)
 * If you don't have this table, you can remove this method
 */
private void logCancellation(Connection conn, int appointmentId, String reason) {
    String logQuery = "INSERT INTO appointment_cancellations " +
                     "(appointment_id, cancelled_by, reason, cancelled_at) " +
                     "VALUES (?, 'counselor', ?, NOW())";
    
    try (PreparedStatement pstmt = conn.prepareStatement(logQuery)) {
        pstmt.setInt(1, appointmentId);
        pstmt.setString(2, reason);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        // Ignore if table doesn't exist
        logger.warning("Could not log cancellation reason: " + e.getMessage());
    }
}
}