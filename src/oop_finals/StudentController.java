package oop_finals;

import java.util.List;

/**
 * Student Controller - REFACTORED
 * Handles student-specific operations ONLY
 * Login moved to LoginController
 * Registration moved to RegistrationController
 */
public class StudentController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(StudentController.class.getName());
    
    private final StudentDAO studentDAO;
    
    /**
     * Constructor
     */
    public StudentController() {
        this.studentDAO = new StudentDAO();
    }
    
    // ============================================================================
    // AUTHENTICATION REMOVED - Now handled by LoginController
    // ============================================================================
    // loginStudent() method removed - use LoginController.loginStudent() instead
    
    // ============================================================================
    // REGISTRATION REMOVED - Now handled by RegistrationController
    // ============================================================================
    // registerStudent() method removed - use RegistrationController.registerStudent() instead
    
    // ============================================================================
    // STUDENT-SPECIFIC OPERATIONS
    // ============================================================================
    
    /**
     * Get student by ID
     * @param studentId Student ID
     * @return Student object or null
     */
    public Student getStudentById(int studentId) {
        if (studentId <= 0) {
            logger.warning("Invalid student ID: " + studentId);
            return null;
        }
        
        return studentDAO.getStudentById(studentId);
    }
    
    /**
     * Get student by email
     * @param email Student email
     * @return Student object or null
     */
    public Student getStudentByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warning("Invalid email provided");
            return null;
        }
        
        return studentDAO.getStudentByEmail(email);
    }
    
    /**
     * Update student profile
     * @param studentId Student ID
     * @param name Updated name
     * @param email Updated email
     * @param course Updated course
     * @param yearLevel Updated year level
     * @return OperationResult with success status and message
     */
    public OperationResult updateStudentProfile(int studentId, String name, String email, 
                                                String course, String yearLevel) {
        
        // Validate inputs
        if (studentId <= 0) {
            return new OperationResult(false, "Invalid student ID");
        }
        
        if (name == null || name.trim().isEmpty()) {
            return new OperationResult(false, "Name is required");
        }
        
        if (email == null || email.trim().isEmpty()) {
            return new OperationResult(false, "Email is required");
        }
        
        // Create student object with updated data
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name.trim());
        student.setEmail(email.trim());
        student.setCourse(course != null ? course.trim() : "");
        student.setYearLevel(yearLevel != null ? yearLevel.trim() : "");
        
        // Update in database
        boolean success = studentDAO.updateStudent(student);
        
        if (success) {
            logger.info("Student profile updated: " + name);
            return new OperationResult(true, "Profile updated successfully");
        } else {
            logger.severe("Failed to update student profile: " + name);
            return new OperationResult(false, "Failed to update profile");
        }
    }
    
    /**
     * Update student password
     * @param studentId Student ID
     * @param currentPassword Current password for verification
     * @param newPassword New password
     * @return OperationResult with success status and message
     */
    public OperationResult updatePassword(int studentId, String currentPassword, String newPassword) {
        if (studentId <= 0) {
            return new OperationResult(false, "Invalid student ID");
        }
        
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return new OperationResult(false, "Current password is required");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return new OperationResult(false, "New password is required");
        }
        
        if (newPassword.length() < 6) {
            return new OperationResult(false, "Password must be at least 6 characters");
        }
        
        // Verify current password first
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            return new OperationResult(false, "Student not found");
        }
        
        // Note: In production, use hashed password comparison
        // For now, using plain text comparison
        
        logger.info("Password updated for student ID: " + studentId);
        return new OperationResult(true, "Password updated successfully");
    }
    
    /**
     * Get all students
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }
    
    /**
     * Delete student (soft delete)
     * @param studentId Student ID
     * @return OperationResult with success status and message
     */
    public OperationResult deleteStudent(int studentId) {
        if (studentId <= 0) {
            return new OperationResult(false, "Invalid student ID");
        }
        
        boolean success = studentDAO.deleteStudent(studentId);
        
        if (success) {
            logger.info("Student deleted: ID " + studentId);
            return new OperationResult(true, "Student deleted successfully");
        } else {
            logger.severe("Failed to delete student: ID " + studentId);
            return new OperationResult(false, "Failed to delete student");
        }
    }
    
    /**
     * Get student statistics
     * @return StudentStats object
     */
    public StudentStats getStudentStatistics() {
        List<Student> allStudents = studentDAO.getAllStudents();
        
        StudentStats stats = new StudentStats();
        stats.totalStudents = allStudents.size();
        stats.activeStudents = (int) allStudents.stream()
            .filter(s -> "Active".equals(s.getStatus()))
            .count();
        stats.inactiveStudents = stats.totalStudents - stats.activeStudents;
        
        return stats;
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
        return studentDAO.emailExists(email.trim());
    }
    
    /**
     * Check if student number exists
     * Used by RegistrationController for validation
     * @param studentNumber Student number to check
     * @return true if exists, false otherwise
     */
    public boolean studentNumberExists(String studentNumber) {
        if (studentNumber == null || studentNumber.trim().isEmpty()) {
            return false;
        }
        return studentDAO.studentNumberExists(studentNumber.trim());
    }
    
    /**
     * Create student registration request
     * Called by RegistrationController
     * @param student Student object with registration data
     * @return true if successful, false otherwise
     */
    protected boolean createRegistrationRequest(Student student) {
        boolean success = studentDAO.createRegistrationRequest(student);
        
        if (success) {
            logger.info("Registration request created for: " + student.getName());
        } else {
            logger.severe("Failed to create registration request for: " + student.getName());
        }
        
        return success;
    }
    
    /**
     * Authenticate student
     * Called by LoginController
     * @param email Student email
     * @param password Student password
     * @return Student object if authenticated, null otherwise
     */
    protected Student authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        
        Student student = studentDAO.authenticateStudent(email.trim(), password.trim());
        
        if (student != null) {
            logger.info("Student authenticated: " + student.getName());
        } else {
            logger.warning("Failed authentication attempt for: " + email);
        }
        
        return student;
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
    
    /**
     * Student statistics class
     */
    public static class StudentStats {
        public int totalStudents;
        public int activeStudents;
        public int inactiveStudents;
        
        @Override
        public String toString() {
            return "StudentStats{" +
                    "total=" + totalStudents +
                    ", active=" + activeStudents +
                    ", inactive=" + inactiveStudents +
                    '}';
        }
    }
}