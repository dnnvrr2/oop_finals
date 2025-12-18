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
    public UniversalLoginResult login(String emailOrUsername, String password) {
        // Validate inputs
        String validationError = validateLoginInputs(emailOrUsername, password);
        if (validationError != null) {
            return new UniversalLoginResult(false, null, null, validationError);
        }
        
        // Get user by email to determine type
        User user = userDAO.getUserByEmail(emailOrUsername.trim());
        
        if (user == null) {
            logger.warning("Failed login attempt - user not found: " + emailOrUsername);
            return new UniversalLoginResult(false, null, null, 
                "Invalid email or password. Please check your credentials.");
        }
        
        // Check if user is active
        if (!user.isActive()) {
            logger.warning("Inactive user login attempt: " + emailOrUsername);
            return new UniversalLoginResult(false, null, null, 
                "Your account has been deactivated. Please contact support.");
        }
        
        // Route to appropriate authentication based on user type
        return authenticateByUserType(user.getUserType(), emailOrUsername, password);
    }
    
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
     * Login with specific user type
     * @param userType User type (Admin, Student, Counselor)
     * @param emailOrUsername Email or username
     * @param password Password
     * @return UniversalLoginResult
     */
    public UniversalLoginResult loginAs(String userType, String emailOrUsername, String password) {
        // Validate inputs
        String validationError = validateLoginInputs(emailOrUsername, password);
        if (validationError != null) {
            return new UniversalLoginResult(false, null, null, validationError);
        }
        
        if (!isValidUserType(userType)) {
            return new UniversalLoginResult(false, null, null, "Invalid user type");
        }
        
        // Authenticate based on user type
        return authenticateByUserType(userType, emailOrUsername, password);
    }
    
    // ============================================================================
    // HELPER METHODS
    // ============================================================================
    
    /**
     * Authenticate by user type (internal method)
     */
    private UniversalLoginResult authenticateByUserType(String userType, 
                                                        String emailOrUsername, 
                                                        String password) {
        switch (userType) {
            case "Admin":
                AdminController.LoginResult adminResult = 
                    adminController.loginAdmin(emailOrUsername, password);
                return new UniversalLoginResult(
                    adminResult.isSuccess(),
                    adminResult.getAdmin(),
                    "Admin",
                    adminResult.getMessage()
                );
                
            case "Student":
                Student student = studentController.authenticate(emailOrUsername, password);
                if (student != null) {
                    return new UniversalLoginResult(true, student, "Student", "Login successful!");
                } else {
                    return new UniversalLoginResult(false, null, "Student", 
                        "Invalid email or password. Please check your credentials.");
                }
                
            case "Counselor":
                Counselor counselor = counselorController.authenticate(emailOrUsername, password);
                if (counselor != null) {
                    return new UniversalLoginResult(true, counselor, "Counselor", "Login successful!");
                } else {
                    return new UniversalLoginResult(false, null, "Counselor", 
                        "Invalid email or password. Please check your credentials.");
                }
                
            default:
                return new UniversalLoginResult(false, null, null, "Invalid user type");
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
     */
    private boolean isValidUserType(String userType) {
        return "Admin".equals(userType) || 
               "Student".equals(userType) || 
               "Counselor".equals(userType);
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    // ============================================================================
    // RESULT CLASSES
    // ============================================================================
    
    /**
     * Universal login result - works for all user types
     */
    public static class UniversalLoginResult {
        private final boolean success;
        private final Object userObject; // Can be Admin, Student, or Counselor
        private final String userType;
        private final String message;
        
        public UniversalLoginResult(boolean success, Object userObject, 
                                   String userType, String message) {
            this.success = success;
            this.userObject = userObject;
            this.userType = userType;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Object getUserObject() {
            return userObject;
        }
        
        public String getUserType() {
            return userType;
        }
        
        public String getMessage() {
            return message;
        }
        
        // Convenience methods for type casting
        public Admin getAsAdmin() {
            return userObject instanceof Admin ? (Admin) userObject : null;
        }
        
        public Student getAsStudent() {
            return userObject instanceof Student ? (Student) userObject : null;
        }
        
        public Counselor getAsCounselor() {
            return userObject instanceof Counselor ? (Counselor) userObject : null;
        }
        
        public boolean isAdmin() {
            return "Admin".equals(userType);
        }
        
        public boolean isStudent() {
            return "Student".equals(userType);
        }
        
        public boolean isCounselor() {
            return "Counselor".equals(userType);
        }
    }
    
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