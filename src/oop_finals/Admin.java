package oop_finals;

import java.sql.Timestamp;

/**
 * Admin Entity Model
 * Represents an administrator in the system
 */
public class Admin {
    private int adminId;
    private int userId;
    private String name;
    private String email;
    private String password;
    private String adminNumber;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public Admin() {
    }

    public Admin(int adminId, int userId, String name, String email, String adminNumber, String status) {
        this.adminId = adminId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.adminNumber = adminNumber;
        this.status = status;
    }

    // Full constructor
    public Admin(int adminId, int userId, String name, String email, String password, 
                 String adminNumber, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.adminId = adminId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.adminNumber = adminNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public String getAdminNumber() {
        return adminNumber;
    }

    public void setAdminNumber(String adminNumber) {
        this.adminNumber = adminNumber;
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

    // Utility methods
    public boolean isActive() {
        return "Active".equals(status);
    }

    public String getFormattedInfo() {
        return String.format("%s (%s)", name, adminNumber);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", adminNumber='" + adminNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}