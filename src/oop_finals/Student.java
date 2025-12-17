/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop_finals;

/**
 * Student Model Class (Entity/POJO)
 * Represents a student in the system
 */
public class Student {
    private int studentId;
    private int userId;
    private String name;
    private String email;
    private String password;
    private String studentNumber;
    private String course;
    private String yearLevel;
    private String status;
    
    // Default constructor
    public Student() {
    }
    
    // Constructor with essential fields
    public Student(int studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }
    
    // Full constructor
    public Student(int studentId, int userId, String name, String email, 
                   String studentNumber, String course, String yearLevel, String status) {
        this.studentId = studentId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.studentNumber = studentNumber;
        this.course = course;
        this.yearLevel = yearLevel;
        this.status = status;
    }
    
    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
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
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public String getYearLevel() {
        return yearLevel;
    }
    
    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", course='" + course + '\'' +
                ", yearLevel='" + yearLevel + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
