package oop_finals;

import java.sql.Timestamp;

/**
 * Counselor Model Class (Entity/POJO)
 * Represents a counselor in the system
 */
public class Counselor {
    private int counselorId;
    private int userId;
    private String name;
    private String email;
    private String password;
    private String specialization;
    private String licenseNumber;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Default constructor
     */
    public Counselor() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Counselor(int counselorId, String name, String specialization) {
        this.counselorId = counselorId;
        this.name = name;
        this.specialization = specialization;
    }
    
    /**
     * Full constructor
     */
    public Counselor(int counselorId, int userId, String name, String email, 
                    String specialization, String licenseNumber, String status) {
        this.counselorId = counselorId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.status = status;
    }
    
    // Getters and Setters
    public int getCounselorId() {
        return counselorId;
    }
    
    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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
    
    /**
     * Get formatted counselor info for display
     */
    public String getFormattedInfo() {
        StringBuilder info = new StringBuilder();
        info.append("═══════════════════════════════════\n");
        info.append("           COUNSELOR DETAILS\n");
        info.append("═══════════════════════════════════\n\n");
        info.append("Name: ").append(name != null ? name : "N/A").append("\n");
        info.append("Specialization: ").append(specialization != null ? specialization : "N/A").append("\n");
        info.append("Email: ").append(email != null ? email : "N/A").append("\n");
        info.append("License Number: ").append(licenseNumber != null ? licenseNumber : "N/A").append("\n");
        info.append("Status: ").append(status != null ? status : "N/A").append("\n\n");
        info.append("═══════════════════════════════════\n");
        
        return info.toString();
    }
    
    @Override
    public String toString() {
        return "Counselor{" +
                "counselorId=" + counselorId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", specialization='" + specialization + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
