package oop_finals;

import java.util.regex.Pattern;

/**
 * Registration Controller - REFACTORED
 * Centralized registration for ALL user types
 * This is now NECESSARY since registration logic was removed from individual controllers
 */
public class RegistrationController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(RegistrationController.class.getName());
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    private final StudentController studentController;
    private final CounselorController counselorController;
    private final UserDAO userDAO;
    
    /**
     * Constructor
     */
    public RegistrationController() {
        this.studentController = new StudentController();
        this.counselorController = new CounselorController();
        this.userDAO = new UserDAO();
    }
    
    // ============================================================================
    // STUDENT REGISTRATION
    // ============================================================================
    
    /**
     * Register new student
     * @param name Full name
     * @param email Email
     * @param password Password
     * @param confirmPassword Confirm password
     * @param studentNumber Student number
     * @param course Course
     * @param yearLevel Year level
     * @return RegistrationResult with success status and message
     */
    public RegistrationResult registerStudent(String name, String email, String password,
                                              String confirmPassword, String studentNumber,
                                              String course, String yearLevel) {
        
        // Validate all fields
        String validationError = validateStudentRegistration(name, email, password, 
                                                             confirmPassword, studentNumber, 
                                                             course, yearLevel);
        if (validationError != null) {
            return new RegistrationResult(false, "Student", validationError);
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email.trim())) {
            return new RegistrationResult(false, "Student", 
                "Email already registered. Please use a different email or login.");
        }
        
        // Check if student number already exists
        if (studentController.studentNumberExists(studentNumber.trim())) {
            return new RegistrationResult(false, "Student", 
                "Student number already registered.");
        }
        
        // Create student object
        Student student = new Student();
        student.setName(name.trim());
        student.setEmail(email.trim());
        student.setPassword(password.trim());
        student.setStudentNumber(studentNumber.trim());
        student.setCourse(course.trim());
        student.setYearLevel(yearLevel.trim());
        
        // Create registration request using StudentController
        boolean success = studentController.createRegistrationRequest(student);
        
        if (success) {
            logger.info("Student registration request created: " + name);
            return new RegistrationResult(true, "Student", 
                "Registration request submitted successfully! Please wait for admin approval.");
        } else {
            logger.warning("Failed student registration: " + name);
            return new RegistrationResult(false, "Student", 
                "Registration failed. Please try again.");
        }
    }
    
    // ============================================================================
    // COUNSELOR REGISTRATION
    // ============================================================================
    
    /**
     * Register new counselor
     * @param name Full name
     * @param email Email
     * @param specialization Specialization
     * @param licenseNumber License number
     * @param password Password
     * @param confirmPassword Confirm password
     * @return RegistrationResult with success status and message
     */
    public RegistrationResult registerCounselor(String name, String email, String specialization,
                                                String licenseNumber, String password,
                                                String confirmPassword) {
        
        // Validate all fields
        String validationError = validateCounselorRegistration(name, email, specialization,
                                                               licenseNumber, password, 
                                                               confirmPassword);
        if (validationError != null) {
            return new RegistrationResult(false, "Counselor", validationError);
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email.trim())) {
            return new RegistrationResult(false, "Counselor", 
                "Email already registered. Please use a different email or login.");
        }
        
        // Check if license number already exists
        if (counselorController.licenseNumberExists(licenseNumber.trim())) {
            return new RegistrationResult(false, "Counselor", 
                "License number already registered.");
        }
        
        // Create counselor object
        Counselor counselor = new Counselor();
        counselor.setName(name.trim());
        counselor.setEmail(email.trim());
        counselor.setSpecialization(specialization.trim());
        counselor.setLicenseNumber(licenseNumber.trim());
        counselor.setPassword(password.trim());
        
        // Create registration request using CounselorController
        boolean success = counselorController.createRegistrationRequest(counselor);
        
        if (success) {
            logger.info("Counselor registration request created: " + name);
            return new RegistrationResult(true, "Counselor", 
                "Registration request submitted successfully! Please wait for admin approval.");
        } else {
            logger.warning("Failed counselor registration: " + name);
            return new RegistrationResult(false, "Counselor", 
                "Registration failed. Please try again.");
        }
    }
    
    // ============================================================================
    // VALIDATION METHODS
    // ============================================================================
    
    /**
     * Validate student registration data
     * @return Error message or null if valid
     */
    private String validateStudentRegistration(String name, String email, String password,
                                               String confirmPassword, String studentNumber,
                                               String course, String yearLevel) {
        
        // Common validation
        String commonError = validateCommonFields(name, email, password, confirmPassword);
        if (commonError != null) {
            return commonError;
        }
        
        // Student-specific validation
        if (studentNumber == null || studentNumber.trim().isEmpty() || 
            studentNumber.equals("ID Number")) {
            return "Please enter your student number.";
        }
        
        if (course == null || course.trim().isEmpty() || course.equals("Course")) {
            return "Please enter your course.";
        }
        
        if (yearLevel == null || yearLevel.trim().isEmpty() || yearLevel.equals("Year level")) {
            return "Please enter your year level.";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Validate counselor registration data
     * @return Error message or null if valid
     */
    private String validateCounselorRegistration(String name, String email, String specialization,
                                                 String licenseNumber, String password,
                                                 String confirmPassword) {
        
        // Common validation
        String commonError = validateCommonFields(name, email, password, confirmPassword);
        if (commonError != null) {
            return commonError;
        }
        
        // Counselor-specific validation
        if (specialization == null || specialization.trim().isEmpty()) {
            return "Please enter your specialization.";
        }
        
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return "Please enter your license number.";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Validate common fields for all user types
     * @return Error message or null if valid
     */
    private String validateCommonFields(String name, String email, 
                                       String password, String confirmPassword) {
        
        // Name validation
        if (name == null || name.trim().isEmpty() || name.equals("Full name")) {
            return "Please enter your full name.";
        }
        
        if (name.trim().length() < 2) {
            return "Name must be at least 2 characters long.";
        }
        
        // Email validation
        if (email == null || email.trim().isEmpty() || email.equals("Email")) {
            return "Please enter your email address.";
        }
        
        if (!isValidEmail(email.trim())) {
            return "Please enter a valid email address.";
        }
        
        // Password validation
        if (password == null || password.trim().isEmpty() || password.equals("Password")) {
            return "Please enter a password.";
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.";
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return "Please confirm your password.";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match. Please try again.";
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
    
    // ============================================================================
    // UTILITY METHODS
    // ============================================================================
    
    /**
     * Check if email is already registered
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean isEmailRegistered(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return userDAO.emailExists(email.trim());
    }
    
    /**
     * Check if student number is already registered
     * @param studentNumber Student number to check
     * @return true if exists, false otherwise
     */
    public boolean isStudentNumberRegistered(String studentNumber) {
        if (studentNumber == null || studentNumber.trim().isEmpty()) {
            return false;
        }
        
        return studentController.studentNumberExists(studentNumber.trim());
    }
    
    /**
     * Check if license number is already registered
     * @param licenseNumber License number to check
     * @return true if exists, false otherwise
     */
    public boolean isLicenseNumberRegistered(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return false;
        }
        
        return counselorController.licenseNumberExists(licenseNumber.trim());
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return Strength message (Weak, Medium, Strong)
     */
    public String checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Weak";
        }
        
        int strength = 0;
        
        // Check length
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        
        // Check for uppercase
        if (password.matches(".*[A-Z].*")) strength++;
        
        // Check for lowercase
        if (password.matches(".*[a-z].*")) strength++;
        
        // Check for digits
        if (password.matches(".*\\d.*")) strength++;
        
        // Check for special characters
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;
        
        if (strength >= 5) {
            return "Strong";
        } else if (strength >= 3) {
            return "Medium";
        } else {
            return "Weak";
        }
    }
    
    /**
     * Get password requirements
     * @return List of password requirements
     */
    public String getPasswordRequirements() {
        return "Password must:\n" +
               "• Be at least " + MIN_PASSWORD_LENGTH + " characters long\n" +
               "• Contain both uppercase and lowercase letters (recommended)\n" +
               "• Include at least one number (recommended)\n" +
               "• Include special characters for stronger security (recommended)";
    }
    
    // ============================================================================
    // RESULT CLASS
    // ============================================================================
    
    /**
     * Registration result class
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String userType;
        private final String message;
        
        public RegistrationResult(boolean success, String userType, String message) {
            this.success = success;
            this.userType = userType;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getUserType() {
            return userType;
        }
        
        public String getMessage() {
            return message;
        }
        
        public boolean isStudent() {
            return "Student".equals(userType);
        }
        
        public boolean isCounselor() {
            return "Counselor".equals(userType);
        }
    }
}