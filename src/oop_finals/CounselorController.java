package oop_finals;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Counselor Controller
 * Handles business logic and coordinates between views and DAO
 */
public class CounselorController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(CounselorController.class.getName());
    
    private final CounselorDAO counselorDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Constructor
     */
    public CounselorController() {
        this.counselorDAO = new CounselorDAO();
    }
    
    /**
     * Authenticate counselor login
     * @param emailOrUsername Email or username
     * @param password Password
     * @return LoginResult with success status, counselor object, and message
     */
    public LoginResult loginCounselor(String emailOrUsername, String password) {
        // Validate inputs
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            logger.warning("Login attempt with empty email/username");
            return new LoginResult(false, null, "Please enter your email or username.");
        }
        
        if (password == null || password.trim().isEmpty()) {
            logger.warning("Login attempt with empty password");
            return new LoginResult(false, null, "Please enter your password.");
        }
        
        // Authenticate using DAO
        Counselor counselor = counselorDAO.authenticateCounselor(emailOrUsername.trim(), password.trim());
        
        if (counselor != null) {
            logger.info("Counselor logged in successfully: " + counselor.getName());
            return new LoginResult(true, counselor, "Login successful!");
        } else {
            logger.warning("Failed login attempt for: " + emailOrUsername);
            return new LoginResult(false, null, "Invalid email/username or password. Please check your credentials and try again.");
        }
    }
    
    /**
     * Get all available specializations
     * @return List of specialization strings
     */
    public List<String> getAllSpecializations() {
        return counselorDAO.getAllSpecializations();
    }
    
    /**
     * Get counselors by specialization
     * @param specialization Specialization to filter by
     * @return List of counselors
     */
    public List<Counselor> getCounselorsBySpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            logger.warning("Invalid specialization provided");
            return List.of();
        }
        
        return counselorDAO.getCounselorsBySpecialization(specialization);
    }
    
    /**
     * Get counselor by ID
     * @param counselorId Counselor ID
     * @return Counselor object or null
     */
    public Counselor getCounselorById(int counselorId) {
        if (counselorId <= 0) {
            logger.warning("Invalid counselor ID: " + counselorId);
            return null;
        }
        
        return counselorDAO.getCounselorById(counselorId);
    }
    
    /**
     * Get counselor by name
     * @param name Counselor name
     * @return Counselor object or null
     */
    public Counselor getCounselorByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warning("Invalid counselor name provided");
            return null;
        }
        
        return counselorDAO.getCounselorByName(name);
    }
    
    /**
     * Get all active counselors
     * @return List of all active counselors
     */
    public List<Counselor> getAllActiveCounselors() {
        return counselorDAO.getAllActiveCounselors();
    }
    
    /**
     * Check if counselor is available on a specific date
     * @param counselorId Counselor ID
     * @param date Date to check
     * @return true if available, false otherwise
     */
    public boolean isCounselorAvailableOnDate(int counselorId, LocalDate date) {
        if (counselorId <= 0) {
            logger.warning("Invalid counselor ID: " + counselorId);
            return false;
        }
        
        if (date == null) {
            logger.warning("Date cannot be null");
            return false;
        }
        
        // Cannot book appointments in the past
        if (date.isBefore(LocalDate.now())) {
            return false;
        }
        
        return counselorDAO.isCounselorAvailableOnDate(counselorId, date);
    }
    
    /**
     * Get counselor's available days of week
     * @param counselorId Counselor ID
     * @return List of available day names
     */
    public List<String> getCounselorAvailableDays(int counselorId) {
        if (counselorId <= 0) {
            logger.warning("Invalid counselor ID: " + counselorId);
            return List.of();
        }
        
        return counselorDAO.getCounselorAvailableDays(counselorId);
    }
    
    /**
     * Get counselor's blocked dates
     * @param counselorId Counselor ID
     * @return List of blocked dates
     */
    public List<LocalDate> getCounselorBlockedDates(int counselorId) {
        if (counselorId <= 0) {
            logger.warning("Invalid counselor ID: " + counselorId);
            return List.of();
        }
        
        return counselorDAO.getCounselorBlockedDates(counselorId);
    }
    
    /**
     * Get formatted counselor information for display
     * @param counselorName Counselor name
     * @return Formatted string or error message
     */
    public String getCounselorFormattedInfo(String counselorName) {
        if (counselorName == null || counselorName.trim().isEmpty()) {
            return "No counselor selected.";
        }
        
        Counselor counselor = counselorDAO.getCounselorByName(counselorName);
        
        if (counselor == null) {
            return "Counselor information not found.";
        }
        
        return counselor.getFormattedInfo();
    }
    
    /**
     * Get counselor ID from name
     * @param counselorName Counselor name
     * @return Counselor ID or -1 if not found
     */
    public int getCounselorIdByName(String counselorName) {
        if (counselorName == null || counselorName.trim().isEmpty()) {
            logger.warning("Invalid counselor name provided");
            return -1;
        }
        
        Counselor counselor = counselorDAO.getCounselorByName(counselorName);
        
        if (counselor != null) {
            return counselor.getCounselorId();
        }
        
        return -1;
    }
    
    /**
     * Validate counselor selection for booking
     * @param specialization Selected specialization
     * @param counselorName Selected counselor name
     * @return Error message or null if valid
     */
    public String validateCounselorSelection(String specialization, String counselorName) {
        if (specialization == null || specialization.equals("-- Select Specialization --")) {
            return "Please select a specialization.";
        }
        
        if (counselorName == null || counselorName.equals("-- Select Counselor --")) {
            return "Please select a counselor.";
        }
        
        // Verify counselor exists and is active
        Counselor counselor = counselorDAO.getCounselorByName(counselorName);
        if (counselor == null) {
            return "Selected counselor is not available.";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Validate date selection for counselor
     * @param counselorId Counselor ID
     * @param selectedDate Selected date
     * @return Error message or null if valid
     */
    public String validateDateSelection(int counselorId, LocalDate selectedDate) {
        if (selectedDate == null) {
            return "Please select a date.";
        }
        
        if (selectedDate.isBefore(LocalDate.now())) {
            return "Cannot select a date in the past.";
        }
        
        if (counselorId <= 0) {
            return "Please select a counselor first.";
        }
        
        if (!counselorDAO.isCounselorAvailableOnDate(counselorId, selectedDate)) {
            String dayName = selectedDate.getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault());
            return "The counselor is not available on " + dayName + "s or this date is blocked.";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Create new counselor
     * @param counselor Counselor object
     * @return true if successful, false otherwise
     */
    public boolean createCounselor(Counselor counselor) {
        if (counselor == null) {
            logger.warning("Cannot create null counselor");
            return false;
        }
        
        // Validate required fields
        if (counselor.getName() == null || counselor.getName().trim().isEmpty()) {
            logger.warning("Counselor name is required");
            return false;
        }
        
        if (counselor.getEmail() == null || counselor.getEmail().trim().isEmpty()) {
            logger.warning("Counselor email is required");
            return false;
        }
        
        boolean success = counselorDAO.createCounselor(counselor);
        
        if (success) {
            logger.info("Counselor created successfully: " + counselor.getName());
        } else {
            logger.severe("Failed to create counselor: " + counselor.getName());
        }
        
        return success;
    }
    
    /**
     * Update counselor
     * @param counselor Counselor object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateCounselor(Counselor counselor) {
        if (counselor == null || counselor.getCounselorId() <= 0) {
            logger.warning("Invalid counselor data for update");
            return false;
        }
        
        boolean success = counselorDAO.updateCounselor(counselor);
        
        if (success) {
            logger.info("Counselor updated successfully: " + counselor.getName());
        } else {
            logger.severe("Failed to update counselor: " + counselor.getName());
        }
        
        return success;
    }
    
    /**
     * Delete counselor (soft delete)
     * @param counselorId Counselor ID
     * @return true if successful, false otherwise
     */
    public boolean deleteCounselor(int counselorId) {
        if (counselorId <= 0) {
            logger.warning("Invalid counselor ID for deletion: " + counselorId);
            return false;
        }
        
        boolean success = counselorDAO.deleteCounselor(counselorId);
        
        if (success) {
            logger.info("Counselor deleted successfully: ID " + counselorId);
        } else {
            logger.severe("Failed to delete counselor: ID " + counselorId);
        }
        
        return success;
    }
    
    /**
     * Register new counselor
     */
    public String registerCounselor(String name, String email, String specialization,
                                    String licenseNumber, String password, String confirmPassword) {
        // Validate all fields
        String validationError = validateCounselorRegistrationData(name, email, specialization, 
                                                         licenseNumber, password, confirmPassword);
        if (validationError != null) {
            return validationError;
        }
        
        // Check if email exists
        if (counselorDAO.emailExists(email)) {
            return "Email already registered or pending approval.";
        }
        
        // Check if license exists
        if (counselorDAO.licenseNumberExists(licenseNumber)) {
            return "License number already registered or pending approval.";
        }
        
        // Create registration request
        Counselor counselor = new Counselor();
        counselor.setName(name.trim());
        counselor.setEmail(email.trim());
        counselor.setSpecialization(specialization.trim());
        counselor.setLicenseNumber(licenseNumber.trim());
        counselor.setPassword(password.trim());
        
        boolean success = counselorDAO.createRegistrationRequest(counselor);
        
        return success ? "SUCCESS" : "Registration failed. Please try again.";
    }
    
    /**
     * Block date for counselor
     */
    public boolean blockDate(int counselorId, LocalDate date, String reason) {
        if (counselorId <= 0 || date == null) {
            return false;
        }
        
        if (date.isBefore(LocalDate.now())) {
            return false;
        }
        
        return counselorDAO.blockDate(counselorId, date, reason);
    }
    
    /**
     * Unblock date for counselor
     */
    public boolean unblockDate(int counselorId, LocalDate date) {
        if (counselorId <= 0 || date == null) {
            return false;
        }
        
        return counselorDAO.unblockDate(counselorId, date);
    }
    
    /**
     * Update blocked date reason
     */
    public boolean updateBlockedDateReason(int counselorId, LocalDate date, String newReason) {
        if (counselorId <= 0 || date == null || newReason == null) {
            return false;
        }
        
        return counselorDAO.updateBlockedDateReason(counselorId, date, newReason);
    }
    
    /**
     * Validate counselor registration data
     */
    private String validateCounselorRegistrationData(String name, String email, String specialization,
                                                     String licenseNumber, String password, String confirmPassword) {
        if (name == null || name.trim().isEmpty()) {
            return "Please enter your name.";
        }
        
        if (email == null || email.trim().isEmpty()) {
            return "Please enter your email.";
        }
        
        if (!isValidEmail(email)) {
            return "Invalid email format.";
        }
        
        if (specialization == null || specialization.trim().isEmpty()) {
            return "Please enter your specialization.";
        }
        
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return "Please enter your license number.";
        }
        
        if (password == null || password.trim().isEmpty()) {
            return "Please enter your password.";
        }
        
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        
        return null;
    }
    
    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Inner class to represent login result
     */
    public static class LoginResult {
        private final boolean success;
        private final Counselor counselor;
        private final String message;
        
        public LoginResult(boolean success, Counselor counselor, String message) {
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