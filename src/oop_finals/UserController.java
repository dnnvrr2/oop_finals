package oop_finals;

import java.util.List;
import java.util.regex.Pattern;

/**
 * User Controller
 * Handles business logic for general user operations
 * Coordinates between views and UserDAO
 */
public class UserController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(UserController.class.getName());
    
    private final UserDAO userDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Constructor
     */
    public UserController() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User object or null
     */
    public User getUserById(int userId) {
        if (userId <= 0) {
            logger.warning("Invalid user ID: " + userId);
            return null;
        }
        
        return userDAO.getUserById(userId);
    }
    
    /**
     * Get user by email
     * @param email User email
     * @return User object or null
     */
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warning("Invalid email provided");
            return null;
        }
        
        return userDAO.getUserByEmail(email.trim());
    }
    
    /**
     * Get all users of a specific type
     * @param userType User type (Admin, Student, Counselor)
     * @return List of users
     */
    public List<User> getUsersByType(String userType) {
        if (!isValidUserType(userType)) {
            logger.warning("Invalid user type: " + userType);
            return List.of();
        }
        
        return userDAO.getUsersByType(userType);
    }
    
    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return userDAO.emailExists(email.trim());
    }
    
    /**
     * Create new user
     * @param user User object
     * @return Result with success status and message
     */
    public CreateUserResult createUser(User user) {
        // Validate user data
        String validationError = validateUserData(user);
        if (validationError != null) {
            return new CreateUserResult(false, -1, validationError);
        }
        
        // Check if email already exists
        if (userDAO.emailExists(user.getEmail())) {
            return new CreateUserResult(false, -1, "Email already registered");
        }
        
        // Create user
        int userId = userDAO.createUser(user);
        
        if (userId > 0) {
            logger.info("User created successfully: " + user.getName());
            return new CreateUserResult(true, userId, "User created successfully");
        } else {
            return new CreateUserResult(false, -1, "Failed to create user");
        }
    }
    
    /**
     * Update user
     * @param user User object with updated data
     * @return Result with success status and message
     */
    public OperationResult updateUser(User user) {
        if (user == null || user.getUserId() <= 0) {
            return new OperationResult(false, "Invalid user data");
        }
        
        // Validate user data
        String validationError = validateUserData(user);
        if (validationError != null) {
            return new OperationResult(false, validationError);
        }
        
        boolean success = userDAO.updateUser(user);
        
        if (success) {
            logger.info("User updated successfully: " + user.getName());
            return new OperationResult(true, "User updated successfully");
        } else {
            return new OperationResult(false, "Failed to update user");
        }
    }
    
    /**
     * Activate user
     * @param userId User ID
     * @return Result with success status and message
     */
    public OperationResult activateUser(int userId) {
        return updateUserStatus(userId, "Active");
    }
    
    /**
     * Deactivate user
     * @param userId User ID
     * @return Result with success status and message
     */
    public OperationResult deactivateUser(int userId) {
        return updateUserStatus(userId, "Inactive");
    }
    
    /**
     * Update user status
     * @param userId User ID
     * @param status New status
     * @return Result with success status and message
     */
    public OperationResult updateUserStatus(int userId, String status) {
        if (userId <= 0) {
            return new OperationResult(false, "Invalid user ID");
        }
        
        if (!isValidStatus(status)) {
            return new OperationResult(false, "Invalid status");
        }
        
        boolean success = userDAO.updateUserStatus(userId, status);
        
        if (success) {
            logger.info("User status updated: ID " + userId + " -> " + status);
            return new OperationResult(true, "User status updated successfully");
        } else {
            return new OperationResult(false, "Failed to update user status");
        }
    }
    
    /**
     * Delete user (soft delete)
     * @param userId User ID
     * @return Result with success status and message
     */
    public OperationResult deleteUser(int userId) {
        if (userId <= 0) {
            return new OperationResult(false, "Invalid user ID");
        }
        
        boolean success = userDAO.deleteUser(userId);
        
        if (success) {
            logger.info("User deleted: ID " + userId);
            return new OperationResult(true, "User deleted successfully");
        } else {
            return new OperationResult(false, "Failed to delete user");
        }
    }
    
    /**
     * Get user statistics
     * @return UserStats object
     */
    public UserDAO.UserStats getUserStatistics() {
        return userDAO.getUserStatistics();
    }
    
    /**
     * Validate user data
     * @param user User object to validate
     * @return Error message or null if valid
     */
    private String validateUserData(User user) {
        if (user == null) {
            return "User data is required";
        }
        
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return "Name is required";
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "Email is required";
        }
        
        if (!isValidEmail(user.getEmail())) {
            return "Invalid email format";
        }
        
        if (user.getUserType() == null || !isValidUserType(user.getUserType())) {
            return "Invalid user type";
        }
        
        if (user.getPassword() != null && user.getPassword().length() < 6) {
            return "Password must be at least 6 characters";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Check if user type is valid
     * @param userType User type to check
     * @return true if valid, false otherwise
     */
    private boolean isValidUserType(String userType) {
        return "Admin".equals(userType) || 
               "Student".equals(userType) || 
               "Counselor".equals(userType);
    }
    
    /**
     * Check if status is valid
     * @param status Status to check
     * @return true if valid, false otherwise
     */
    private boolean isValidStatus(String status) {
        return "Active".equals(status) || 
               "Inactive".equals(status) || 
               "Pending".equals(status);
    }
    
    // Result classes
    
    /**
     * Operation result class
     */
    public static class OperationResult {
        private final boolean success;
        private final String message;
        
        public OperationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Create user result class
     */
    public static class CreateUserResult {
        private final boolean success;
        private final int userId;
        private final String message;
        
        public CreateUserResult(boolean success, int userId, String message) {
            this.success = success;
            this.userId = userId;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public int getUserId() {
            return userId;
        }
        
        public String getMessage() {
            return message;
        }
    }
}