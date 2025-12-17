package oop_finals;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Counselor Data Access Object
 * Handles all database operations for Counselor entity
 */
public class CounselorDAO {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(CounselorDAO.class.getName());
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "09@denverrr";
    
    /**
     * Get database connection
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
     * Get all active specializations
     * @return List of unique specializations
     */
    public List<String> getAllSpecializations() {
        List<String> specializations = new ArrayList<>();
        String query = "SELECT DISTINCT specialization FROM counselors " +
                      "WHERE status = 'Active' ORDER BY specialization";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }
            
            logger.info("Retrieved " + specializations.size() + " specializations");
            
        } catch (SQLException e) {
            logger.severe("Error getting specializations: " + e.getMessage());
        }
        
        return specializations;
    }
    
    /**
     * Get counselors by specialization
     * @param specialization Specialization to filter by
     * @return List of counselors with that specialization
     */
    public List<Counselor> getCounselorsBySpecialization(String specialization) {
        List<Counselor> counselors = new ArrayList<>();
        String query = "SELECT counselor_id, name, email, specialization, license_number, status " +
                      "FROM counselors WHERE specialization = ? AND status = 'Active' ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, specialization);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                counselors.add(extractCounselorFromResultSet(rs));
            }
            
            logger.info("Retrieved " + counselors.size() + " counselors for specialization: " + specialization);
            
        } catch (SQLException e) {
            logger.severe("Error getting counselors by specialization: " + e.getMessage());
        }
        
        return counselors;
    }
    
    /**
     * Get counselor by ID
     * @param counselorId Counselor ID
     * @return Counselor object or null if not found
     */
    public Counselor getCounselorById(int counselorId) {
        String query = "SELECT * FROM counselors WHERE counselor_id = ? AND status = 'Active'";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, counselorId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractCounselorFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting counselor by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get counselor by name
     * @param name Counselor name
     * @return Counselor object or null if not found
     */
    public Counselor getCounselorByName(String name) {
        String query = "SELECT * FROM counselors WHERE name = ? AND status = 'Active'";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractCounselorFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting counselor by name: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all active counselors
     * @return List of all active counselors
     */
    public List<Counselor> getAllActiveCounselors() {
        List<Counselor> counselors = new ArrayList<>();
        String query = "SELECT * FROM counselors WHERE status = 'Active' ORDER BY name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                counselors.add(extractCounselorFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting all active counselors: " + e.getMessage());
        }
        
        return counselors;
    }
    
    /**
     * Check if counselor is available on a specific date
     * @param counselorId Counselor ID
     * @param date Date to check
     * @return true if available, false otherwise
     */
    public boolean isCounselorAvailableOnDate(int counselorId, LocalDate date) {
        try (Connection conn = getConnection()) {
            
            // Get day of week
            String dayOfWeek = date.getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault());
            
            // Check if counselor works on this day
            String schedQuery = "SELECT COUNT(*) as count FROM counselor_schedules " +
                               "WHERE counselor_id = ? AND day_of_week = ? AND is_available = TRUE";
            PreparedStatement schedPst = conn.prepareStatement(schedQuery);
            schedPst.setInt(1, counselorId);
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
            
            // Check if date is blocked
            String blockedQuery = "SELECT COUNT(*) as count FROM counselor_blocked_dates " +
                                 "WHERE counselor_id = ? AND blocked_date = ?";
            PreparedStatement blockedPst = conn.prepareStatement(blockedQuery);
            blockedPst.setInt(1, counselorId);
            blockedPst.setDate(2, Date.valueOf(date));
            ResultSet blockedRs = blockedPst.executeQuery();
            
            boolean isBlocked = false;
            if (blockedRs.next()) {
                isBlocked = blockedRs.getInt("count") > 0;
            }
            blockedRs.close();
            blockedPst.close();
            
            return !isBlocked;
            
        } catch (SQLException e) {
            logger.severe("Error checking counselor availability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get counselor's available days of week
     * @param counselorId Counselor ID
     * @return List of available day names
     */
    public List<String> getCounselorAvailableDays(int counselorId) {
        List<String> availableDays = new ArrayList<>();
        String query = "SELECT day_of_week FROM counselor_schedules " +
                      "WHERE counselor_id = ? AND is_available = TRUE " +
                      "ORDER BY FIELD(day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, counselorId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                availableDays.add(rs.getString("day_of_week"));
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting counselor available days: " + e.getMessage());
        }
        
        return availableDays;
    }
    
    /**
     * Get counselor's blocked dates
     * @param counselorId Counselor ID
     * @return List of blocked dates
     */
    public List<LocalDate> getCounselorBlockedDates(int counselorId) {
        List<LocalDate> blockedDates = new ArrayList<>();
        String query = "SELECT blocked_date FROM counselor_blocked_dates " +
                      "WHERE counselor_id = ? ORDER BY blocked_date";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, counselorId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Date sqlDate = rs.getDate("blocked_date");
                if (sqlDate != null) {
                    blockedDates.add(sqlDate.toLocalDate());
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting counselor blocked dates: " + e.getMessage());
        }
        
        return blockedDates;
    }
    
    /**
     * Create new counselor
     * @param counselor Counselor object
     * @return true if successful, false otherwise
     */
    public boolean createCounselor(Counselor counselor) {
        String query = "INSERT INTO counselors (user_id, name, email, password, " +
                      "specialization, license_number, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setInt(1, counselor.getUserId());
            pst.setString(2, counselor.getName());
            pst.setString(3, counselor.getEmail());
            pst.setString(4, counselor.getPassword());
            pst.setString(5, counselor.getSpecialization());
            pst.setString(6, counselor.getLicenseNumber());
            pst.setString(7, counselor.getStatus());
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    counselor.setCounselorId(rs.getInt(1));
                }
                logger.info("Counselor created successfully: " + counselor.getName());
                return true;
            }
            
        } catch (SQLException e) {
            logger.severe("Error creating counselor: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update counselor
     * @param counselor Counselor object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateCounselor(Counselor counselor) {
        String query = "UPDATE counselors SET name = ?, email = ?, " +
                      "specialization = ?, license_number = ? " +
                      "WHERE counselor_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, counselor.getName());
            pst.setString(2, counselor.getEmail());
            pst.setString(3, counselor.getSpecialization());
            pst.setString(4, counselor.getLicenseNumber());
            pst.setInt(5, counselor.getCounselorId());
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating counselor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete counselor (soft delete)
     * @param counselorId Counselor ID
     * @return true if successful, false otherwise
     */
    public boolean deleteCounselor(int counselorId) {
        String query = "UPDATE counselors SET status = 'Inactive' WHERE counselor_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, counselorId);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error deleting counselor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Counselor object from ResultSet
     * @param rs ResultSet from query
     * @return Counselor object
     * @throws SQLException if error reading ResultSet
     */
    private Counselor extractCounselorFromResultSet(ResultSet rs) throws SQLException {
        Counselor counselor = new Counselor();
        
        counselor.setCounselorId(rs.getInt("counselor_id"));
        
        // Check if user_id exists in the result set
        try {
            counselor.setUserId(rs.getInt("user_id"));
        } catch (SQLException e) {
            // user_id might not be in the result set
        }
        
        counselor.setName(rs.getString("name"));
        counselor.setEmail(rs.getString("email"));
        counselor.setSpecialization(rs.getString("specialization"));
        counselor.setLicenseNumber(rs.getString("license_number"));
        counselor.setStatus(rs.getString("status"));
        
        // Check if timestamps exist
        try {
            counselor.setCreatedAt(rs.getTimestamp("created_at"));
            counselor.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException e) {
            // timestamps might not be in the result set
        }
        
        return counselor;
    }
    
    // Add these methods to your CounselorDAO class

/**
 * Check if email exists in counselors or pending requests
 * @param email Email to check
 * @return true if exists, false otherwise
 */
public boolean emailExists(String email) {
    String query = "SELECT COUNT(*) as count FROM counselors WHERE email = ? " +
                  "UNION ALL " +
                  "SELECT COUNT(*) as count FROM user_requests WHERE email = ? AND user_type = 'Counselor'";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, email);
        pst.setString(2, email);
        ResultSet rs = pst.executeQuery();
        
        int totalCount = 0;
        while (rs.next()) {
            totalCount += rs.getInt("count");
        }
        
        return totalCount > 0;
        
    } catch (SQLException e) {
        logger.severe("Error checking email existence: " + e.getMessage());
    }
    
    return false;
}

/**
 * Check if license number exists in counselors or pending requests
 * @param licenseNumber License number to check
 * @return true if exists, false otherwise
 */
public boolean licenseNumberExists(String licenseNumber) {
    String query = "SELECT COUNT(*) as count FROM counselors WHERE license_number = ? " +
                  "UNION ALL " +
                  "SELECT COUNT(*) as count FROM user_requests WHERE license_number = ? AND user_type = 'Counselor'";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, licenseNumber);
        pst.setString(2, licenseNumber);
        ResultSet rs = pst.executeQuery();
        
        int totalCount = 0;
        while (rs.next()) {
            totalCount += rs.getInt("count");
        }
        
        return totalCount > 0;
        
    } catch (SQLException e) {
        logger.severe("Error checking license number existence: " + e.getMessage());
    }
    
    return false;
}

/**
 * Create new counselor registration request
 * @param counselor Counselor object with registration data
 * @return true if successful, false otherwise
 */
public boolean createRegistrationRequest(Counselor counselor) {
    String query = "INSERT INTO user_requests (user_type, name, email, password, " +
                  "specialization, license_number, status) " +
                  "VALUES ('Counselor', ?, ?, ?, ?, ?, 'Pending')";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, counselor.getName());
        pst.setString(2, counselor.getEmail());
        pst.setString(3, counselor.getPassword());
        pst.setString(4, counselor.getSpecialization());
        pst.setString(5, counselor.getLicenseNumber());
        
        return pst.executeUpdate() > 0;
        
    } catch (SQLException e) {
        logger.severe("Error creating counselor registration request: " + e.getMessage());
        return false;
    }
}

/**
 * Block a specific date for a counselor
 * @param counselorId Counselor ID
 * @param date Date to block
 * @param reason Reason for blocking
 * @return true if successful, false otherwise
 */
public boolean blockDate(int counselorId, LocalDate date, String reason) {
    String query = "INSERT INTO counselor_blocked_dates (counselor_id, blocked_date, reason) " +
                  "VALUES (?, ?, ?)";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setInt(1, counselorId);
        pst.setDate(2, Date.valueOf(date));
        pst.setString(3, reason);
        
        return pst.executeUpdate() > 0;
        
    } catch (SQLException e) {
        // Check if it's a duplicate entry error
        if (e.getMessage().contains("Duplicate entry")) {
            logger.warning("Date already blocked: " + date);
        } else {
            logger.severe("Error blocking date: " + e.getMessage());
        }
        return false;
    }
}

/**
 * Unblock a specific date for a counselor
 * @param counselorId Counselor ID
 * @param date Date to unblock
 * @return true if successful, false otherwise
 */
public boolean unblockDate(int counselorId, LocalDate date) {
    String query = "DELETE FROM counselor_blocked_dates " +
                  "WHERE counselor_id = ? AND blocked_date = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setInt(1, counselorId);
        pst.setDate(2, Date.valueOf(date));
        
        return pst.executeUpdate() > 0;
        
    } catch (SQLException e) {
        logger.severe("Error unblocking date: " + e.getMessage());
        return false;
    }
}

/**
 * Update the reason for a blocked date
 * @param counselorId Counselor ID
 * @param date Blocked date
 * @param newReason New reason
 * @return true if successful, false otherwise
 */
public boolean updateBlockedDateReason(int counselorId, LocalDate date, String newReason) {
    String query = "UPDATE counselor_blocked_dates SET reason = ? " +
                  "WHERE counselor_id = ? AND blocked_date = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, newReason);
        pst.setInt(2, counselorId);
        pst.setDate(3, Date.valueOf(date));
        
        return pst.executeUpdate() > 0;
        
    } catch (SQLException e) {
        logger.severe("Error updating blocked date reason: " + e.getMessage());
        return false;
    }
}

public Counselor authenticateCounselor(String emailOrUsername, String password) {
    String query = 
        "SELECT c.counselor_id, c.user_id, c.name, c.email, c.specialization, " +
        "c.license_number, c.status " +
        "FROM counselors c " +
        "JOIN users u ON u.user_id = c.user_id " +
        "WHERE (c.email = ? OR c.name = ?) AND c.password = ? AND u.status = 'Active'";
    
    try (Connection conn = getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, emailOrUsername);
        pst.setString(2, emailOrUsername);
        pst.setString(3, password);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            return extractCounselorFromResultSet(rs);
        }
        
    } catch (SQLException e) {
        logger.severe("Error authenticating counselor: " + e.getMessage());
    }
    
    return null;
}
}