package oop_finals;

import java.util.List;

/**
 * AdminController - Business Logic Layer for Admin operations
 * Handles validation and workflow for admin-related operations
 */
public class AdminController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(AdminController.class.getName());
    
    private final AdminDAO adminDAO;

    public AdminController() {
        this.adminDAO = new AdminDAO();
    }

    /**
     * Login admin
     * @param emailOrUsername Email or username
     * @param password Password
     * @return LoginResult with success status and admin data
     */
    public LoginResult loginAdmin(String emailOrUsername, String password) {
        // Validate input
        if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
            return new LoginResult(false, null, "Email/username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, null, "Password is required");
        }

        // Authenticate
        Admin admin = adminDAO.authenticateAdmin(emailOrUsername.trim(), password);

        if (admin == null) {
            logger.warning("Failed login attempt for: " + emailOrUsername);
            return new LoginResult(false, null, "Invalid email or password");
        }

        if (!admin.isActive()) {
            logger.warning("Inactive admin login attempt: " + emailOrUsername);
            return new LoginResult(false, null, "Your account has been deactivated");
        }

        logger.info("Successful admin login: " + admin.getName());
        return new LoginResult(true, admin, "Login successful");
    }

    /**
     * Get admin by ID
     * @param adminId Admin ID
     * @return Admin object or null
     */
    public Admin getAdminById(int adminId) {
        if (adminId <= 0) {
            logger.warning("Invalid admin ID: " + adminId);
            return null;
        }
        return adminDAO.getAdminById(adminId);
    }

    /**
     * Get dashboard statistics
     * @return DashboardStats object
     */
    public AdminDAO.DashboardStats getDashboardStatistics() {
        return adminDAO.getDashboardStatistics();
    }

    /**
     * Get pending requests preview for dashboard
     * @param limit Maximum number of requests
     * @return List of pending requests
     */
    public List<AdminDAO.UserRequest> getPendingRequestsPreview(int limit) {
        if (limit <= 0) {
            limit = 5; // Default limit
        }
        return adminDAO.getPendingRequestsPreview(limit);
    }

    /**
     * Get all pending user requests
     * @return List of all pending requests
     */
    public List<AdminDAO.UserRequest> getAllPendingRequests() {
        return adminDAO.getAllPendingRequests();
    }

    /**
     * Approve user request
     * @param requestId Request ID
     * @param adminId Admin approving the request
     * @return ApprovalResult with success status and message
     */
    public ApprovalResult approveUserRequest(int requestId, int adminId) {
        // Validate input
        if (requestId <= 0) {
            return new ApprovalResult(false, "Invalid request ID");
        }

        if (adminId <= 0) {
            return new ApprovalResult(false, "Invalid admin ID");
        }

        // Get request details first
        AdminDAO.UserRequestDetails details = adminDAO.getUserRequestDetails(requestId);
        if (details == null) {
            return new ApprovalResult(false, "Request not found");
        }

        if (!"Pending".equals(details.getStatus())) {
            return new ApprovalResult(false, "Request has already been processed");
        }

        // Approve request
        boolean success = adminDAO.approveUserRequest(requestId, adminId);

        if (success) {
            logger.info("Request approved by admin " + adminId + ": " + requestId);
            return new ApprovalResult(true, "User request approved successfully");
        } else {
            return new ApprovalResult(false, "Failed to approve request");
        }
    }

    /**
     * Reject user request
     * @param requestId Request ID
     * @param reason Rejection reason
     * @param adminId Admin rejecting the request
     * @return ApprovalResult with success status and message
     */
    public ApprovalResult rejectUserRequest(int requestId, String reason, int adminId) {
        // Validate input
        if (requestId <= 0) {
            return new ApprovalResult(false, "Invalid request ID");
        }

        if (adminId <= 0) {
            return new ApprovalResult(false, "Invalid admin ID");
        }

        // Reason is optional but should be trimmed if provided
        String cleanReason = (reason != null) ? reason.trim() : "";

        // Get request details first
        AdminDAO.UserRequestDetails details = adminDAO.getUserRequestDetails(requestId);
        if (details == null) {
            return new ApprovalResult(false, "Request not found");
        }

        if (!"Pending".equals(details.getStatus())) {
            return new ApprovalResult(false, "Request has already been processed");
        }

        // Reject request
        boolean success = adminDAO.rejectUserRequest(requestId, cleanReason, adminId);

        if (success) {
            logger.info("Request rejected by admin " + adminId + ": " + requestId);
            return new ApprovalResult(true, "User request rejected");
        } else {
            return new ApprovalResult(false, "Failed to reject request");
        }
    }

    /**
     * Get all users (students and counselors)
     * @return List of all users
     */
    public List<AdminDAO.UserInfo> getAllUsers() {
        return adminDAO.getAllUsers();
    }

    /**
     * Activate user
     * @param userType "Student" or "Counselor"
     * @param id Student number or license number
     * @return StatusUpdateResult with success status and message
     */
    public StatusUpdateResult activateUser(String userType, String id) {
        return updateUserStatus(userType, id, "Active");
    }

    /**
     * Deactivate user
     * @param userType "Student" or "Counselor"
     * @param id Student number or license number
     * @return StatusUpdateResult with success status and message
     */
    public StatusUpdateResult deactivateUser(String userType, String id) {
        return updateUserStatus(userType, id, "Inactive");
    }

    /**
     * Update user status
     * @param userType "Student" or "Counselor"
     * @param id Student number or license number
     * @param newStatus "Active" or "Inactive"
     * @return StatusUpdateResult with success status and message
     */
    private StatusUpdateResult updateUserStatus(String userType, String id, String newStatus) {
        // Validate input
        if (userType == null || (!userType.equals("Student") && !userType.equals("Counselor"))) {
            return new StatusUpdateResult(false, "Invalid user type");
        }

        if (id == null || id.trim().isEmpty()) {
            return new StatusUpdateResult(false, "Invalid user ID");
        }

        if (newStatus == null || (!newStatus.equals("Active") && !newStatus.equals("Inactive"))) {
            return new StatusUpdateResult(false, "Invalid status");
        }

        // Update status
        boolean success = adminDAO.updateUserStatus(userType, id, newStatus);

        if (success) {
            logger.info("User status updated: " + userType + " " + id + " -> " + newStatus);
            return new StatusUpdateResult(true, 
                "User " + ("Active".equals(newStatus) ? "activated" : "deactivated") + " successfully");
        } else {
            return new StatusUpdateResult(false, "Failed to update user status");
        }
    }

    // Result classes for structured responses

    /**
     * Login result class
     */
    public static class LoginResult {
        private final boolean success;
        private final Admin admin;
        private final String message;

        public LoginResult(boolean success, Admin admin, String message) {
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
     * Approval result class
     */
    public static class ApprovalResult {
        private final boolean success;
        private final String message;

        public ApprovalResult(boolean success, String message) {
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
     * Status update result class
     */
    public static class StatusUpdateResult {
        private final boolean success;
        private final String message;

        public StatusUpdateResult(boolean success, String message) {
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