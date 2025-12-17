package oop_finals;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminDAO - Data Access Object for Admin operations
 * Handles all database interactions for Admin entity
 */
public class AdminDAO {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(AdminDAO.class.getName());
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "09@denverrr";

    // Get database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Authenticate admin login
     * @param emailOrUsername Email or username
     * @param password Password
     * @return Admin object if successful, null otherwise
     */
    public Admin authenticateAdmin(String emailOrUsername, String password) {
        String query = "SELECT u.*, a.* FROM users u " +
                      "JOIN admins a ON u.user_id = a.user_id " +
                      "WHERE a.email = ? AND a.password = ? AND u.status = 'Active'";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, emailOrUsername);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractAdminFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            logger.severe("Error authenticating admin: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get admin by ID
     * @param adminId Admin ID
     * @return Admin object or null
     */
    public Admin getAdminById(int adminId) {
        String query = "SELECT u.*, a.* FROM users u " +
                      "JOIN admins a ON u.user_id = a.user_id " +
                      "WHERE a.admin_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, adminId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractAdminFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            logger.severe("Error getting admin by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get all admins
     * @return List of all admins
     */
    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String query = "SELECT u.*, a.* FROM users u " +
                      "JOIN admins a ON u.user_id = a.user_id " +
                      "ORDER BY a.name";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                admins.add(extractAdminFromResultSet(rs));
            }

        } catch (SQLException e) {
            logger.severe("Error getting all admins: " + e.getMessage());
            e.printStackTrace();
        }

        return admins;
    }

    /**
     * Get dashboard statistics
     * @return DashboardStats object with all counts
     */
    public DashboardStats getDashboardStatistics() {
        DashboardStats stats = new DashboardStats();

        try (Connection conn = getConnection()) {
            
            // Pending Counselors
            stats.pendingCounselors = getCount(conn, 
                "SELECT COUNT(*) FROM user_requests WHERE user_type='Counselor' AND status='Pending'");
            
            // Approved Counselors
            stats.approvedCounselors = getCount(conn,
                "SELECT COUNT(*) FROM counselors WHERE status='Active'");
            
            // Total Counselors
            stats.totalCounselors = getCount(conn,
                "SELECT COUNT(*) FROM counselors");
            
            // Pending Students
            stats.pendingStudents = getCount(conn,
                "SELECT COUNT(*) FROM user_requests WHERE user_type='Student' AND status='Pending'");
            
            // Approved Students
            stats.approvedStudents = getCount(conn,
                "SELECT COUNT(*) FROM students WHERE status='Active'");
            
            // Total Students
            stats.totalStudents = getCount(conn,
                "SELECT COUNT(*) FROM students");
            
            // Pending Appointments
            stats.pendingAppointments = getCount(conn,
                "SELECT COUNT(*) FROM appointments WHERE status='Pending'");
            
            // Total Appointments
            stats.totalAppointments = getCount(conn,
                "SELECT COUNT(*) FROM appointments");

        } catch (SQLException e) {
            logger.severe("Error getting dashboard statistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Helper method to get count from query
     */
    private int getCount(Connection conn, String query) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get pending user requests preview (limited)
     * @param limit Maximum number of requests to return
     * @return List of UserRequest objects
     */
    public List<UserRequest> getPendingRequestsPreview(int limit) {
        List<UserRequest> requests = new ArrayList<>();
        String query = "SELECT request_id, name, user_type, email, requested_at, status " +
                      "FROM user_requests " +
                      "WHERE status='Pending' " +
                      "ORDER BY requested_at DESC " +
                      "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, limit);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    UserRequest request = new UserRequest();
                    request.setRequestId(rs.getInt("request_id"));
                    request.setName(rs.getString("name"));
                    request.setUserType(rs.getString("user_type"));
                    request.setEmail(rs.getString("email"));
                    request.setRequestedAt(rs.getTimestamp("requested_at"));
                    request.setStatus(rs.getString("status"));
                    requests.add(request);
                }
            }

        } catch (SQLException e) {
            logger.severe("Error getting pending requests preview: " + e.getMessage());
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * Get all pending user requests
     * @return List of all pending UserRequest objects
     */
    public List<UserRequest> getAllPendingRequests() {
        List<UserRequest> requests = new ArrayList<>();
        String query = "SELECT request_id, name, user_type, email, requested_at, status " +
                      "FROM user_requests " +
                      "WHERE status='Pending' " +
                      "ORDER BY requested_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                UserRequest request = new UserRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setName(rs.getString("name"));
                request.setUserType(rs.getString("user_type"));
                request.setEmail(rs.getString("email"));
                request.setRequestedAt(rs.getTimestamp("requested_at"));
                request.setStatus(rs.getString("status"));
                requests.add(request);
            }

        } catch (SQLException e) {
            logger.severe("Error getting all pending requests: " + e.getMessage());
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * Get user request details by ID
     * @param requestId Request ID
     * @return UserRequestDetails object or null
     */
    public UserRequestDetails getUserRequestDetails(int requestId) {
        String query = "SELECT * FROM user_requests WHERE request_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, requestId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    UserRequestDetails details = new UserRequestDetails();
                    details.setRequestId(rs.getInt("request_id"));
                    details.setUserType(rs.getString("user_type"));
                    details.setName(rs.getString("name"));
                    details.setEmail(rs.getString("email"));
                    details.setPassword(rs.getString("password"));
                    details.setStatus(rs.getString("status"));
                    
                    // Counselor-specific fields
                    details.setSpecialization(rs.getString("specialization"));
                    details.setLicenseNumber(rs.getString("license_number"));
                    
                    // Student-specific fields
                    details.setCourse(rs.getString("course"));
                    details.setYearLevel(rs.getString("year_level"));
                    details.setStudentNumber(rs.getString("student_number"));
                    
                    details.setRequestedAt(rs.getTimestamp("requested_at"));
                    details.setProcessedAt(rs.getTimestamp("processed_at"));
                    details.setProcessedBy(rs.getInt("processed_by"));
                    details.setRejectionReason(rs.getString("rejection_reason"));
                    
                    return details;
                }
            }

        } catch (SQLException e) {
            logger.severe("Error getting user request details: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Approve user request - creates user and moves to appropriate table
     * @param requestId Request ID
     * @param adminId Admin approving the request
     * @return true if successful, false otherwise
     */
    public boolean approveUserRequest(int requestId, int adminId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Get request details
            UserRequestDetails details = getUserRequestDetails(requestId);
            if (details == null) {
                return false;
            }

            // Create user in users table
            String insertUserQuery = "INSERT INTO users (user_type, name, email, password, status) " +
                                    "VALUES (?, ?, ?, ?, 'Active')";
            int userId;
            
            try (PreparedStatement userPst = conn.prepareStatement(insertUserQuery, 
                    Statement.RETURN_GENERATED_KEYS)) {
                userPst.setString(1, details.getUserType());
                userPst.setString(2, details.getName());
                userPst.setString(3, details.getEmail());
                userPst.setString(4, details.getPassword());
                userPst.executeUpdate();

                ResultSet generatedKeys = userPst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Insert into specific table based on user type
            if ("Counselor".equals(details.getUserType())) {
                String insertCounselorQuery = "INSERT INTO counselors " +
                    "(user_id, name, email, specialization, license_number, password, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'Active')";
                    
                try (PreparedStatement pst = conn.prepareStatement(insertCounselorQuery)) {
                    pst.setInt(1, userId);
                    pst.setString(2, details.getName());
                    pst.setString(3, details.getEmail());
                    pst.setString(4, details.getSpecialization());
                    pst.setString(5, details.getLicenseNumber());
                    pst.setString(6, details.getPassword());
                    pst.executeUpdate();
                }
                
            } else if ("Student".equals(details.getUserType())) {
                String insertStudentQuery = "INSERT INTO students " +
                    "(user_id, name, email, course, year_level, student_number, password, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')";
                    
                try (PreparedStatement pst = conn.prepareStatement(insertStudentQuery)) {
                    pst.setInt(1, userId);
                    pst.setString(2, details.getName());
                    pst.setString(3, details.getEmail());
                    pst.setString(4, details.getCourse());
                    
                    String yearLevel = details.getYearLevel();
                    if (yearLevel == null || yearLevel.trim().isEmpty()) {
                        yearLevel = "Not Specified";
                    }
                    pst.setString(5, yearLevel);
                    
                    pst.setString(6, details.getStudentNumber());
                    pst.setString(7, details.getPassword());
                    pst.executeUpdate();
                }
            }

            // Update request status
            String updateRequestQuery = "UPDATE user_requests " +
                                       "SET status = 'Approved', processed_at = NOW(), processed_by = ? " +
                                       "WHERE request_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(updateRequestQuery)) {
                pst.setInt(1, adminId);
                pst.setInt(2, requestId);
                pst.executeUpdate();
            }

            conn.commit();
            logger.info("User request approved successfully: " + requestId);
            return true;

        } catch (SQLException e) {
            logger.severe("Error approving user request: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.severe("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.severe("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reject user request
     * @param requestId Request ID
     * @param reason Rejection reason
     * @param adminId Admin rejecting the request
     * @return true if successful, false otherwise
     */
    public boolean rejectUserRequest(int requestId, String reason, int adminId) {
        String query = "UPDATE user_requests " +
                      "SET status = 'Rejected', rejection_reason = ?, processed_at = NOW(), processed_by = ? " +
                      "WHERE request_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, reason);
            pst.setInt(2, adminId);
            pst.setInt(3, requestId);

            int rowsAffected = pst.executeUpdate();
            logger.info("User request rejected: " + requestId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.severe("Error rejecting user request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all users (students and counselors)
     * @return List of UserInfo objects
     */
    public List<UserInfo> getAllUsers() {
        List<UserInfo> users = new ArrayList<>();

        try (Connection conn = getConnection()) {
            
            // Get students
            String queryStudents = "SELECT name, 'Student' as user_type, student_number as id, " +
                                  "email, status FROM students";
            try (PreparedStatement pst = conn.prepareStatement(queryStudents);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    UserInfo user = new UserInfo();
                    user.setName(rs.getString("name"));
                    user.setUserType(rs.getString("user_type"));
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setStatus(rs.getString("status"));
                    users.add(user);
                }
            }

            // Get counselors
            String queryCounselors = "SELECT name, 'Counselor' as user_type, license_number as id, " +
                                    "email, status FROM counselors";
            try (PreparedStatement pst = conn.prepareStatement(queryCounselors);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    UserInfo user = new UserInfo();
                    user.setName(rs.getString("name"));
                    user.setUserType(rs.getString("user_type"));
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setStatus(rs.getString("status"));
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            logger.severe("Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Update user status (activate/deactivate)
     * @param userType "Student" or "Counselor"
     * @param id Student number or license number
     * @param newStatus "Active" or "Inactive"
     * @return true if successful, false otherwise
     */
    public boolean updateUserStatus(String userType, String id, String newStatus) {
        String tableName = "Student".equals(userType) ? "students" : "counselors";
        String idColumn = "Student".equals(userType) ? "student_number" : "license_number";

        try (Connection conn = getConnection()) {
            
            // Update user type table
            String query = "UPDATE " + tableName + " SET status = ? WHERE " + idColumn + " = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, newStatus);
                pst.setString(2, id);
                pst.executeUpdate();
            }

            // Update users table
            String queryUsers = "UPDATE users SET status = ? " +
                               "WHERE user_id = (SELECT user_id FROM " + tableName + 
                               " WHERE " + idColumn + " = ?)";
            try (PreparedStatement pst = conn.prepareStatement(queryUsers)) {
                pst.setString(1, newStatus);
                pst.setString(2, id);
                pst.executeUpdate();
            }

            logger.info("User status updated: " + userType + " " + id + " -> " + newStatus);
            return true;

        } catch (SQLException e) {
            logger.severe("Error updating user status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract Admin object from ResultSet
     */
    private Admin extractAdminFromResultSet(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setUserId(rs.getInt("user_id"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));
        admin.setPassword(rs.getString("password"));
        admin.setAdminNumber(rs.getString("admin_number"));
        admin.setStatus(rs.getString("status"));
        admin.setCreatedAt(rs.getTimestamp("created_at"));
        admin.setUpdatedAt(rs.getTimestamp("updated_at"));
        return admin;
    }

    // Inner classes for structured data

    /**
     * Dashboard statistics data class
     */
    public static class DashboardStats {
        public int pendingCounselors;
        public int approvedCounselors;
        public int totalCounselors;
        public int pendingStudents;
        public int approvedStudents;
        public int totalStudents;
        public int pendingAppointments;
        public int totalAppointments;
    }

    /**
     * User request data class
     */
    public static class UserRequest {
        private int requestId;
        private String name;
        private String userType;
        private String email;
        private Timestamp requestedAt;
        private String status;

        // Getters and setters
        public int getRequestId() { return requestId; }
        public void setRequestId(int requestId) { this.requestId = requestId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Timestamp getRequestedAt() { return requestedAt; }
        public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * User request details data class
     */
    public static class UserRequestDetails {
        private int requestId;
        private String userType;
        private String name;
        private String email;
        private String password;
        private String status;
        
        // Counselor fields
        private String specialization;
        private String licenseNumber;
        
        // Student fields
        private String course;
        private String yearLevel;
        private String studentNumber;
        
        private Timestamp requestedAt;
        private Timestamp processedAt;
        private int processedBy;
        private String rejectionReason;

        // Getters and setters
        public int getRequestId() { return requestId; }
        public void setRequestId(int requestId) { this.requestId = requestId; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        public String getCourse() { return course; }
        public void setCourse(String course) { this.course = course; }
        public String getYearLevel() { return yearLevel; }
        public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
        public String getStudentNumber() { return studentNumber; }
        public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
        public Timestamp getRequestedAt() { return requestedAt; }
        public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }
        public Timestamp getProcessedAt() { return processedAt; }
        public void setProcessedAt(Timestamp processedAt) { this.processedAt = processedAt; }
        public int getProcessedBy() { return processedBy; }
        public void setProcessedBy(int processedBy) { this.processedBy = processedBy; }
        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    }

    /**
     * User info data class
     */
    public static class UserInfo {
        private String name;
        private String userType;
        private String id;
        private String email;
        private String status;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}