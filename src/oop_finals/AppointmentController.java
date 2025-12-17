package oop_finals;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Appointment Controller
 * Handles business logic and coordinates between views and DAO
 */
public class AppointmentController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(AppointmentController.class.getName());
    
    private final AppointmentDAO appointmentDAO;
    
    // Cancellation policy: must be at least 30 minutes before appointment
    private static final int MIN_CANCELLATION_MINUTES = 30;
    
    /**
     * Constructor
     */
    public AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
    }
    
    /**
     * Get all appointments for a student
     * @param studentId Student ID
     * @param statuses Statuses to filter (e.g., "Upcoming", "Pending")
     * @return List of appointments
     */
    public List<Appointment> getStudentAppointments(int studentId, String... statuses) {
        if (studentId <= 0) {
            logger.warning("Invalid student ID: " + studentId);
            return List.of();
        }
        
        return appointmentDAO.getStudentAppointments(studentId, statuses);
    }
    
    /**
     * Get appointment by ID
     * @param appointmentId Appointment ID
     * @return Appointment object or null
     */
    public Appointment getAppointmentById(int appointmentId) {
        if (appointmentId <= 0) {
            logger.warning("Invalid appointment ID: " + appointmentId);
            return null;
        }
        
        return appointmentDAO.getAppointmentById(appointmentId);
    }
    
    /**
     * Get appointment ID from table data
     * @param studentId Student ID
     * @param date Appointment date
     * @param time Appointment time
     * @param counselorName Counselor name
     * @return Appointment ID or -1 if not found
     */
    public int getAppointmentId(int studentId, Date date, Time time, String counselorName) {
        if (studentId <= 0 || date == null || time == null || counselorName == null) {
            logger.warning("Invalid parameters for getting appointment ID");
            return -1;
        }
        
        return appointmentDAO.getAppointmentId(studentId, date, time, counselorName);
    }
    
    /**
     * Create a new appointment
     * @param appointment Appointment object
     * @return Success message or error message
     */
    public String createAppointment(Appointment appointment) {
        // Validate appointment data
        String validationError = validateAppointmentData(appointment);
        if (validationError != null) {
            return validationError;
        }
        
        // Check if time slot is available
        if (!appointmentDAO.isTimeSlotAvailable(
                appointment.getCounselorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime())) {
            return "This time slot is already booked. Please select another time.";
        }
        
        // Create appointment
        boolean success = appointmentDAO.createAppointment(appointment);
        
        if (success) {
            logger.info("Appointment created successfully");
            return "SUCCESS";
        } else {
            logger.severe("Failed to create appointment");
            return "Failed to create appointment. Please try again.";
        }
    }
    
    /**
     * Cancel an appointment with validation
     * @param appointmentId Appointment ID
     * @return Result object containing success status and message
     */
    public CancellationResult cancelAppointment(int appointmentId) {
        // Get appointment details
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            return new CancellationResult(false, "Appointment not found.");
        }
        
        // Check if appointment can be cancelled
        if (!appointment.canBeCancelled()) {
            return new CancellationResult(false, 
                "Appointment cannot be cancelled. It may have already been processed.");
        }
        
        // Validate cancellation time (30 minutes before)
        String timeValidationError = validateCancellationTime(
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime()
        );
        
        if (timeValidationError != null) {
            return new CancellationResult(false, timeValidationError);
        }
        
        // Cancel appointment
        boolean success = appointmentDAO.cancelAppointment(appointmentId);
        
        if (success) {
            logger.info("Appointment cancelled successfully: ID " + appointmentId);
            return new CancellationResult(true, "Appointment cancelled successfully!");
        } else {
            logger.severe("Failed to cancel appointment: ID " + appointmentId);
            return new CancellationResult(false, 
                "Failed to cancel appointment. It may have already been processed.");
        }
    }
    
    /**
     * Cancel appointment with date and time (alternative method)
     * @param appointmentId Appointment ID
     * @param appointmentDate Appointment date
     * @param appointmentTime Appointment time
     * @return Result object containing success status and message
     */
    public CancellationResult cancelAppointment(int appointmentId, Date appointmentDate, Time appointmentTime) {
        // Validate cancellation time
        String timeValidationError = validateCancellationTime(appointmentDate, appointmentTime);
        
        if (timeValidationError != null) {
            return new CancellationResult(false, timeValidationError);
        }
        
        // Cancel appointment
        boolean success = appointmentDAO.cancelAppointment(appointmentId);
        
        if (success) {
            logger.info("Appointment cancelled successfully: ID " + appointmentId);
            return new CancellationResult(true, "Appointment cancelled successfully!");
        } else {
            logger.severe("Failed to cancel appointment: ID " + appointmentId);
            return new CancellationResult(false, 
                "Failed to cancel appointment. It may have already been processed.");
        }
    }
    
    /**
     * Update appointment status
     * @param appointmentId Appointment ID
     * @param newStatus New status
     * @return true if successful, false otherwise
     */
    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        if (appointmentId <= 0 || newStatus == null || newStatus.trim().isEmpty()) {
            logger.warning("Invalid parameters for updating appointment status");
            return false;
        }
        
        boolean success = appointmentDAO.updateAppointmentStatus(appointmentId, newStatus);
        
        if (success) {
            logger.info("Appointment status updated: ID " + appointmentId + " -> " + newStatus);
        }
        
        return success;
    }
    
    /**
     * Get all appointments for a counselor
     * @param counselorId Counselor ID
     * @param statuses Statuses to filter
     * @return List of appointments
     */
    public List<Appointment> getCounselorAppointments(int counselorId, String... statuses) {
        if (counselorId <= 0) {
            logger.warning("Invalid counselor ID: " + counselorId);
            return List.of();
        }
        
        return appointmentDAO.getCounselorAppointments(counselorId, statuses);
    }
    
    /**
     * Check if a time slot is available
     * @param counselorId Counselor ID
     * @param date Appointment date
     * @param time Appointment time
     * @return true if available, false otherwise
     */
    public boolean isTimeSlotAvailable(int counselorId, Date date, Time time) {
        return appointmentDAO.isTimeSlotAvailable(counselorId, date, time);
    }
    
    /**
     * Validate appointment data
     * @param appointment Appointment object
     * @return Error message or null if valid
     */
    private String validateAppointmentData(Appointment appointment) {
        if (appointment == null) {
            return "Invalid appointment data.";
        }
        
        if (appointment.getStudentId() <= 0) {
            return "Invalid student ID.";
        }
        
        if (appointment.getCounselorId() <= 0) {
            return "Invalid counselor ID.";
        }
        
        if (appointment.getAppointmentDate() == null) {
            return "Please select an appointment date.";
        }
        
        if (appointment.getAppointmentTime() == null) {
            return "Please select an appointment time.";
        }
        
        // Check if appointment is in the future
        LocalDateTime appointmentDateTime = LocalDateTime.of(
            appointment.getAppointmentDate().toLocalDate(),
            appointment.getAppointmentTime().toLocalTime()
        );
        
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            return "Cannot book appointments in the past.";
        }
        
        return null; // All validations passed
    }
    
    /**
     * Validate cancellation time (must be at least 30 minutes before appointment)
     * @param appointmentDate Appointment date
     * @param appointmentTime Appointment time
     * @return Error message or null if valid
     */
    private String validateCancellationTime(Date appointmentDate, Time appointmentTime) {
        if (appointmentDate == null || appointmentTime == null) {
            return "Invalid appointment date or time.";
        }
        
        try {
            // Convert to LocalDateTime
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointmentDate.toLocalDate(),
                appointmentTime.toLocalTime()
            );
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime minCancellationTime = appointmentDateTime.minusMinutes(MIN_CANCELLATION_MINUTES);
            
            if (now.isAfter(minCancellationTime)) {
                return "Cannot cancel appointment. Cancellations must be made at least " +
                       MIN_CANCELLATION_MINUTES + " minutes before the appointment time.";
            }
            
        } catch (Exception e) {
            logger.severe("Error validating cancellation time: " + e.getMessage());
            return "Error validating appointment time. Please try again.";
        }
        
        return null; // Validation passed
    }
    
    /**
     * Inner class to represent cancellation result
     */
    public static class CancellationResult {
        private final boolean success;
        private final String message;
        
        public CancellationResult(boolean success, String message) {
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
    
    public CancellationResult cancelAppointmentByCounselor(int appointmentId, String reason) {
    Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
    
    if (appointment == null) {
        return new CancellationResult(false, "Appointment not found.");
    }
    
    // Validate 30 minutes before rule
    String timeError = validateCancellationTime(
        appointment.getAppointmentDate(),
        appointment.getAppointmentTime()
    );
    
    if (timeError != null) {
        return new CancellationResult(false, timeError);
    }
    
    boolean success = appointmentDAO.cancelAppointmentByCounselor(appointmentId, reason);
    
    if (success) {
        return new CancellationResult(true, "Appointment cancelled successfully!");
    }
    
    return new CancellationResult(false, "Failed to cancel appointment.");
}
}