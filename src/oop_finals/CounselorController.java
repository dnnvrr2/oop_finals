package oop_finals;

import java.time.LocalDate;
import java.util.List;

/**
 * Counselor Controller
 * Handles business logic and coordinates between views and DAO
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
}