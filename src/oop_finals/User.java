package oop_finals;

import java.sql.Timestamp;

/**
 * User Model Class (Entity/POJO)
 * Represents the base user in the system
 * All user types (Admin, Student, Counselor) extend from this entity
 */
public class User {
    private int userId;
    private String userType; // "Admin", "Student", "Counselor"
    private String name;
    private String email;
    private String password;
    private String status; // "Active", "Inactive", "Pending"
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Constructor with essential fields
     */
    public User(int userId, String userType, String name, String email, String status) {
        this.userId = userId;
        this.userType = userType;
        this.name = name;
        this.email = email;
        this.status = status;
    }
    
    /**
     * Full constructor
     */
    public User(int userId, String userType, String name, String email, 
                String password, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.userType = userType;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
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
    
    /**
     * Check if user is active
     */
    public boolean isActive() {
        return "Active".equals(status);
    }
    
    /**
     * Check if user is inactive
     */
    public boolean isInactive() {
        return "Inactive".equals(status);
    }
    
    /**
     * Check if user is pending approval
     */
    public boolean isPending() {
        return "Pending".equals(status);
    }
    
    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return "Admin".equals(userType);
    }
    
    /**
     * Check if user is a student
     */
    public boolean isStudent() {
        return "Student".equals(userType);
    }
    
    /**
     * Check if user is a counselor
     */
    public boolean isCounselor() {
        return "Counselor".equals(userType);
    }
    
    /**
     * Get display name with user type
     */
    public String getDisplayName() {
        return name + " (" + userType + ")";
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userType='" + userType + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}