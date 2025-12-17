package oop_finals;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Appointment entity class
 * Represents an appointment between a student and counselor
 * 
 * @author Admin
 * @version 1.0
 */
public class Appointment {
    
    // Primary fields
    private int appointmentId;
    private int studentId;
    private int counselorId;
    private Date appointmentDate;
    private Time appointmentTime;
    private String reason;
    private String status; // Pending, Upcoming, Completed, Cancelled, Rejected
    
    // Timestamps
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields from joins (optional)
    private String counselorName;
    private String studentName;
    private String specialization;
    private String studentEmail;
    
    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================
    
    /**
     * Default constructor
     */
    public Appointment() {
    }
    
    /**
     * Constructor with required fields
     */
    public Appointment(int studentId, int counselorId, Date appointmentDate, 
                      Time appointmentTime, String reason, String status) {
        this.studentId = studentId;
        this.counselorId = counselorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
    }
    
    // ============================================================================
    // GETTERS AND SETTERS
    // ============================================================================
    
    public int getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public int getCounselorId() {
        return counselorId;
    }
    
    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }
    
    public Date getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public Time getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(Time appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCounselorName() {
        return counselorName;
    }
    
    public void setCounselorName(String counselorName) {
        this.counselorName = counselorName;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getStudentEmail() {
        return studentEmail;
    }
    
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
    
    // ============================================================================
    // UTILITY METHODS
    // ============================================================================
    
    /**
     * Check if appointment is pending
     */
    public boolean isPending() {
        return "Pending".equals(status);
    }
    
    /**
     * Check if appointment is upcoming/approved
     */
    public boolean isUpcoming() {
        return "Upcoming".equals(status);
    }
    
    /**
     * Check if appointment is completed
     */
    public boolean isCompleted() {
        return "Completed".equals(status);
    }
    
    /**
     * Check if appointment is cancelled
     */
    public boolean isCancelled() {
        return "Cancelled".equals(status);
    }
    
    /**
     * Check if appointment is rejected
     */
    public boolean isRejected() {
        return "Rejected".equals(status);
    }
    
    /**
     * Check if appointment can be cancelled
     * Can only cancel if status is Pending or Upcoming
     */
    public boolean canBeCancelled() {
        return isPending() || isUpcoming();
    }
    
    /**
     * Check if appointment is active (not cancelled or completed)
     */
    public boolean isActive() {
        return isPending() || isUpcoming();
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", studentId=" + studentId +
                ", counselorId=" + counselorId +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime=" + appointmentTime +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", counselorName='" + counselorName + '\'' +
                ", studentName='" + studentName + '\'' +
                '}';
    }
}