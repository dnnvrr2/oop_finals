package oop_finals;

import java.util.regex.Pattern;

/**
 * Login Controller - REFACTORED
 * Centralized authentication for ALL user types
 * This is now NECESSARY since login logic was removed from individual controllers
 */
public class LoginController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(LoginController.class.getName());
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private final AdminController adminController;
    private final StudentController studentController;
    private final CounselorController counselorController;
    private final UserDAO userDAO;
    
    /**
     * Constructor
     */
    public LoginController() {
        this.adminController = new AdminController();
        this.studentController = new StudentController();
        this.counselorController = new CounselorController();
        this.userDAO = new UserDAO();
    }
    
    // ============================================================================
    // UNIVERSAL LOGIN (Auto-detect user type)
    // ============================================================================
    
    /**
     * Universal login - automatically detects user type from email
     * @param emailOrUsername Email or username
     * @param password Password
     * @return UniversalLoginResult with user information
     */
    // ============================================================================
    // SPECIFIC USER TYPE LOGIN
    // ============================================================================
    
    /**
     * Login as Admin
     * @param emailOrUsername Email or username
     * @param password Password
     * @return AdminLoginResult
     */
    public AdminLoginResult loginAdmin(String emailOrUsername, String password) {
        // Validate input
        String validationError = validateLoginInputs(emailOrUsername, password);
        if (validationError != null) {
            return new AdminLoginResult(false, null, validationError);
        }
        
        // Use AdminController's authenticate method
        AdminController.LoginResult result = adminController.loginAdmin(emailOrUsername, password);
        
        return new AdminLoginResult(
            result.isSuccess(),
            result.getAdmin(),
            result.getMessage()
        );
    }
    
    /**
     * Login as Student
     * @param email Student email
     * @param password Password
     * @return StudentLoginResult
     */
    public StudentLoginResult loginStudent(String email, String password) {
        // Validate input
        String validationError = validateLoginInputs(email, password);
        if (validationError != null) {
            return new StudentLoginResult(false, null, validationError);
        }
        
        // Use StudentController's protected authenticate method
        Student student = studentController.authenticate(email, password);
        
        if (student != null) {
            return new StudentLoginResult(true, student, "Login successful!");
        } else {
            return new StudentLoginResult(false, null, 
                "Invalid email or password. Please check your credentials.");
        }
    }
    
    /**
     * Login as Counselor
     * @param emailOrUsername Email or username
     * @param password Password
     * @return CounselorLoginResult
     */
    public CounselorLoginResult loginCounselor(String emailOrUsername, String password) {
        // Validate input
        String validationError = validateLoginInputs(emailOrUsername, password);
        if (validationError != null) {
            return new CounselorLoginResult(false, null, validationError);
        }
        
        // Use CounselorController's protected authenticate method
        Counselor counselor = counselorController.authenticate(emailOrUsername, password);
        
        if (counselor != null) {
            return new CounselorLoginResult(true, counselor, "Login successful!");
        } else {
            return new CounselorLoginResult(false, null, 
                "Invalid email or password. Please check your credentials.");
        }
    }
    

    
    /**
     * Validate login inputs
     * @return Error message or null if valid
     */
    private String validateLoginInputs(String emailOrUsername, String password) {
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            return "Please enter your email or username.";
        }
        
        if (password == null || password.trim().isEmpty()) {
            return "Please enter your password.";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Check if user type is valid

    // ============================================================================
    // RESULT CLASSES
    // ============================================================================
    
    /**
     * Universal login result - works for all user types
     */

    /**
     * Admin login result
     */
    public static class AdminLoginResult {
        private final boolean success;
        private final Admin admin;
        private final String message;
        
        public AdminLoginResult(boolean success, Admin admin, String message) {
            this.success = success;
            this.admin = admin;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Admin getAdmin() {
            return admin;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Student login result
     */
    public static class StudentLoginResult {
        private final boolean success;
        private final Student student;
        private final String message;
        
        public StudentLoginResult(boolean success, Student student, String message) {
            this.success = success;
            this.student = student;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Student getStudent() {
            return student;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Counselor login result
     */
    public static class CounselorLoginResult {
        private final boolean success;
        private final Counselor counselor;
        private final String message;
        
        public CounselorLoginResult(boolean success, Counselor counselor, String message) {
            this.success = success;
            this.counselor = counselor;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Counselor getCounselor() {
            return counselor;
        }
        
        public String getMessage() {
            return message;
        }
    }
}