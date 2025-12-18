package oop_finals;

import java.time.LocalDate;
import java.util.List;

/**
 * Counselor Controller - REFACTORED
 * Handles counselor-specific operations ONLY
 * Login moved to LoginController
 * Registration moved to RegistrationController
 */
public class CounselorController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(CounselorController.class.getName());
    
    private final CounselorDAO counselorDAO;
    
    /**
     * Constructor
     */
    public CounselorController() {
        this.counselorDAO = new CounselorDAO();
    }
    
    // ============================================================================
    // AUTHENTICATION REMOVED - Now handled by LoginController
    // ============================================================================
    // loginCounselor() method removed - use LoginController.loginCounselor() instead
    
    // ============================================================================
    // REGISTRATION REMOVED - Now handled by RegistrationController
    // ============================================================================
    // registerCounselor() method removed - use RegistrationController.registerCounselor() instead
    
    // ============================================================================
    // COUNSELOR-SPECIFIC OPERATIONS
    // ============================================================================
    
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
     * Update counselor profile
     * @param counselor Counselor object with updated data
     * @return OperationResult with success status and message
     */
    public OperationResult updateCounselorProfile(Counselor counselor) {
        if (counselor == null || counselor.getCounselorId() <= 0) {
            return new OperationResult(false, "Invalid counselor data");
        }
        
        if (counselor.getName() == null || counselor.getName().trim().isEmpty()) {
            return new OperationResult(false, "Name is required");
        }
        
        boolean success = counselorDAO.updateCounselor(counselor);
        
        if (success) {
            logger.info("Counselor profile updated: " + counselor.getName());
            return new OperationResult(true, "Profile updated successfully");
        } else {
            logger.severe("Failed to update counselor profile: " + counselor.getName());
            return new OperationResult(false, "Failed to update profile");
        }
    }
    
    /**
     * Delete counselor (soft delete)
     * @param counselorId Counselor ID
     * @return OperationResult with success status and message
     */
    public OperationResult deleteCounselor(int counselorId) {
        if (counselorId <= 0) {
            return new OperationResult(false, "Invalid counselor ID");
        }
        
        boolean success = counselorDAO.deleteCounselor(counselorId);
        
        if (success) {
            logger.info("Counselor deleted: ID " + counselorId);
            return new OperationResult(true, "Counselor deleted successfully");
        } else {
            logger.severe("Failed to delete counselor: ID " + counselorId);
            return new OperationResult(false, "Failed to delete counselor");
        }
    }
    
    // ============================================================================
    // SCHEDULE MANAGEMENT
    // ============================================================================
    
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
     * Block date for counselor
     * @param counselorId Counselor ID
     * @param date Date to block
     * @param reason Reason for blocking
     * @return OperationResult with success status and message
     */
    public OperationResult blockDate(int counselorId, LocalDate date, String reason) {
        if (counselorId <= 0) {
            return new OperationResult(false, "Invalid counselor ID");
        }
        
        if (date == null) {
            return new OperationResult(false, "Date is required");
        }
        
        if (date.isBefore(LocalDate.now())) {
            return new OperationResult(false, "Cannot block dates in the past");
        }
        
        boolean success = counselorDAO.blockDate(counselorId, date, reason);
        
        if (success) {
            logger.info("Date blocked for counselor " + counselorId + ": " + date);
            return new OperationResult(true, "Date blocked successfully");
        } else {
            return new OperationResult(false, "Failed to block date");
        }
    }
    
    /**
     * Unblock date for counselor
     * @param counselorId Counselor ID
     * @param date Date to unblock
     * @return OperationResult with success status and message
     */
    public OperationResult unblockDate(int counselorId, LocalDate date) {
        if (counselorId <= 0) {
            return new OperationResult(false, "Invalid counselor ID");
        }
        
        if (date == null) {
            return new OperationResult(false, "Date is required");
        }
        
        boolean success = counselorDAO.unblockDate(counselorId, date);
        
        if (success) {
            logger.info("Date unblocked for counselor " + counselorId + ": " + date);
            return new OperationResult(true, "Date unblocked successfully");
        } else {
            return new OperationResult(false, "Failed to unblock date");
        }
    }
    
    /**
     * Update blocked date reason
     * @param counselorId Counselor ID
     * @param date Blocked date
     * @param newReason New reason
     * @return OperationResult with success status and message
     */
    public OperationResult updateBlockedDateReason(int counselorId, LocalDate date, String newReason) {
        if (counselorId <= 0 || date == null) {
            return new OperationResult(false, "Invalid parameters");
        }
        
        boolean success = counselorDAO.updateBlockedDateReason(counselorId, date, newReason);
        
        if (success) {
            logger.info("Blocked date reason updated for counselor " + counselorId);
            return new OperationResult(true, "Reason updated successfully");
        } else {
            return new OperationResult(false, "Failed to update reason");
        }
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
    
    // ============================================================================
    // HELPER METHODS (For internal use by other controllers)
    // ============================================================================
    
    /**
     * Check if email exists
     * Used by RegistrationController for validation
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return counselorDAO.emailExists(email.trim());
    }
    
    /**
     * Check if license number exists
     * Used by RegistrationController for validation
     * @param licenseNumber License number to check
     * @return true if exists, false otherwise
     */
    public boolean licenseNumberExists(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return false;
        }
        return counselorDAO.licenseNumberExists(licenseNumber.trim());
    }
    
    /**
     * Create counselor registration request
     * Called by RegistrationController
     * @param counselor Counselor object with registration data
     * @return true if successful, false otherwise
     */
    protected boolean createRegistrationRequest(Counselor counselor) {
        boolean success = counselorDAO.createRegistrationRequest(counselor);
        
        if (success) {
            logger.info("Registration request created for: " + counselor.getName());
        } else {
            logger.severe("Failed to create registration request for: " + counselor.getName());
        }
        
        return success;
    }
    
    /**
     * Authenticate counselor
     * Called by LoginController
     * @param emailOrUsername Email or username
     * @param password Password
     * @return Counselor object if authenticated, null otherwise
     */
    protected Counselor authenticate(String emailOrUsername, String password) {
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        Counselor counselor = counselorDAO.authenticateCounselor(emailOrUsername.trim(), password.trim());
        
        if (counselor != null) {
            logger.info("Counselor authenticated: " + counselor.getName());
        } else {
            logger.warning("Failed authentication attempt for: " + emailOrUsername);
        }
        
        return counselor;
    }
    
    // ============================================================================
    // RESULT CLASSES
    // ============================================================================
    
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
}