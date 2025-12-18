package oop_finals;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object
 * Handles all database operations for User entity
 */
public class UserDAO {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(UserDAO.class.getName());
    
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
     * Get user by ID
     * @param userId User ID
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, userId);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting user by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User object or null if not found
     */
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, email);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting user by email: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all users of a specific type
     * @param userType User type (Admin, Student, Counselor)
     * @return List of users
     */
    public List<User> getUsersByType(String userType) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE user_type = ? ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, userType);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
            
            logger.info("Retrieved " + users.size() + " users of type: " + userType);
            
        } catch (SQLException e) {
            logger.severe("Error getting users by type: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY user_type, name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
            logger.info("Retrieved " + users.size() + " users");
            
        } catch (SQLException e) {
            logger.severe("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) as count FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, email);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Error checking email existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Create new user
     * @param user User object
     * @return Generated user ID or -1 if failed
     */
    public int createUser(User user) {
        String query = "INSERT INTO users (user_type, name, email, password, status) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, user.getUserType());
            pst.setString(2, user.getName());
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getPassword());
            pst.setString(5, user.getStatus() != null ? user.getStatus() : "Active");
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        logger.info("User created successfully with ID: " + userId);
                        return userId;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Error creating user: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Update user
     * @param user User object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        String query = "UPDATE users SET name = ?, email = ?, status = ?, " +
                      "updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, user.getName());
            pst.setString(2, user.getEmail());
            pst.setString(3, user.getStatus());
            pst.setInt(4, user.getUserId());
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("User updated successfully: ID " + user.getUserId());
                return true;
            }
            
        } catch (SQLException e) {
            logger.severe("Error updating user: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update user status
     * @param userId User ID
     * @param status New status
     * @return true if successful, false otherwise
     */
    public boolean updateUserStatus(int userId, String status) {
        String query = "UPDATE users SET status = ?, updated_at = NOW() WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, status);
            pst.setInt(2, userId);
            
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("User status updated: ID " + userId + " -> " + status);
                return true;
            }
            
        } catch (SQLException e) {
            logger.severe("Error updating user status: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete user (soft delete - set status to Inactive)
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        return updateUserStatus(userId, "Inactive");
    }
    
    /**
     * Get user statistics by type
     * @return UserStats object with counts
     */
    public UserStats getUserStatistics() {
        UserStats stats = new UserStats();
        
        try (Connection conn = getConnection()) {
            
            // Total users
            stats.totalUsers = getCount(conn, "SELECT COUNT(*) FROM users");
            
            // Active users
            stats.activeUsers = getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE status = 'Active'");
            
            // Inactive users
            stats.inactiveUsers = getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE status = 'Inactive'");
            
            // Admins
            stats.totalAdmins = getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE user_type = 'Admin'");
            
            // Students
            stats.totalStudents = getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE user_type = 'Student'");
            
            // Counselors
            stats.totalCounselors = getCount(conn, 
                "SELECT COUNT(*) FROM users WHERE user_type = 'Counselor'");
            
        } catch (SQLException e) {
            logger.severe("Error getting user statistics: " + e.getMessage());
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
     * Extract User object from ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUserType(rs.getString("user_type"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
    
    /**
     * Inner class for user statistics
     */
    public static class UserStats {
        public int totalUsers;
        public int activeUsers;
        public int inactiveUsers;
        public int totalAdmins;
        public int totalStudents;
        public int totalCounselors;
        
        @Override
        public String toString() {
            return "UserStats{" +
                    "totalUsers=" + totalUsers +
                    ", activeUsers=" + activeUsers +
                    ", inactiveUsers=" + inactiveUsers +
                    ", totalAdmins=" + totalAdmins +
                    ", totalStudents=" + totalStudents +
                    ", totalCounselors=" + totalCounselors +
                    '}';
        }
    }
}