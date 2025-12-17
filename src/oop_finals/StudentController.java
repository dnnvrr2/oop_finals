/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop_finals;

import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Student Controller
 * Handles business logic and coordinates between views and DAO
 */
public class StudentController {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(StudentController.class.getName());
    
    private final StudentDAO studentDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Constructor
     */
    public StudentController() {
        this.studentDAO = new StudentDAO();
    }
    
    /**
     * Authenticate student login
     * @param email Student email
     * @param password Student password
     * @return Student object if authenticated, null otherwise
     */
    public Student loginStudent(String email, String password) {
        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            logger.warning("Login attempt with empty email");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            logger.warning("Login attempt with empty password");
            return null;
        }
        
        // Authenticate
        Student student = studentDAO.authenticateStudent(email.trim(), password.trim());
        
        if (student != null) {
            logger.info("Student logged in successfully: " + student.getName());
        } else {
            logger.warning("Failed login attempt for email: " + email);
        }
        
        return student;
    }
    
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
     * Register new student
     * @param name Full name
     * @param email Email
     * @param password Password
     * @param confirmPassword Confirm password
     * @param studentNumber Student number
     * @param course Course
     * @param yearLevel Year level
     * @return Success message or error message
     */
    public String registerStudent(String name, String email, String password, 
                                  String confirmPassword, String studentNumber, 
                                  String course, String yearLevel) {
        
        // Validate all fields
        String validationError = validateRegistrationData(name, email, password, 
                                                         confirmPassword, studentNumber, 
                                                         course, yearLevel);
        if (validationError != null) {
            return validationError;
        }
        
        // Check if email already exists
        if (studentDAO.emailExists(email)) {
            return "Email already registered.";
        }
        
        // Check if student number already exists
        if (studentDAO.studentNumberExists(studentNumber)) {
            return "Student number already registered.";
        }
        
        // Create student object
        Student student = new Student();
        student.setName(name.trim());
        student.setEmail(email.trim());
        student.setPassword(password.trim());
        student.setStudentNumber(studentNumber.trim());
        student.setCourse(course.trim());
        student.setYearLevel(yearLevel.trim());
        
        // Create registration request
        boolean success = studentDAO.createRegistrationRequest(student);
        
        if (success) {
            logger.info("Registration request created for: " + name);
            return "SUCCESS";
        } else {
            logger.severe("Failed to create registration request for: " + name);
            return "Registration failed. Please try again.";
        }
    }
    
    /**
     * Update student profile
     * @param studentId Student ID
     * @param name Updated name
     * @param email Updated email
     * @param course Updated course
     * @param yearLevel Updated year level
     * @return true if successful, false otherwise
     */
    public boolean updateStudentProfile(int studentId, String name, String email, 
                                       String course, String yearLevel) {
        
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            logger.warning("Update attempt with empty name");
            return false;
        }
        
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            logger.warning("Update attempt with invalid email");
            return false;
        }
        
        // Create student object with updated data
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name.trim());
        student.setEmail(email.trim());
        student.setCourse(course.trim());
        student.setYearLevel(yearLevel.trim());
        
        // Update in database
        boolean success = studentDAO.updateStudent(student);
        
        if (success) {
            logger.info("Student profile updated: " + name);
        } else {
            logger.severe("Failed to update student profile: " + name);
        }
        
        return success;
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
     * @return true if successful, false otherwise
     */
    public boolean deleteStudent(int studentId) {
        boolean success = studentDAO.deleteStudent(studentId);
        
        if (success) {
            logger.info("Student deleted: ID " + studentId);
        } else {
            logger.severe("Failed to delete student: ID " + studentId);
        }
        
        return success;
    }
    
    /**
     * Validate registration data
     * @return Error message or null if valid
     */
    private String validateRegistrationData(String name, String email, String password,
                                           String confirmPassword, String studentNumber,
                                           String course, String yearLevel) {
        
        // Check for empty fields
        if (name == null || name.trim().isEmpty() || name.equals("Full name")) {
            return "Please enter your full name.";
        }
        
        if (email == null || email.trim().isEmpty() || email.equals("Email")) {
            return "Please enter your email.";
        }
        
        if (password == null || password.trim().isEmpty() || password.equals("Password")) {
            return "Please enter your password.";
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return "Please confirm your password.";
        }
        
        if (studentNumber == null || studentNumber.trim().isEmpty() || studentNumber.equals("ID Number")) {
            return "Please enter your student number.";
        }
        
        if (course == null || course.trim().isEmpty() || course.equals("Course")) {
            return "Please enter your course.";
        }
        
        if (yearLevel == null || yearLevel.trim().isEmpty() || yearLevel.equals("Year level")) {
            return "Please enter your year level.";
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            return "Invalid email format.";
        }
        
        // Validate password length
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        
        return null; // All validations passed
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Check if student exists by email
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean checkEmailExists(String email) {
        return studentDAO.emailExists(email);
    }
    
    /**
     * Check if student number exists
     * @param studentNumber Student number to check
     * @return true if exists, false otherwise
     */
    public boolean checkStudentNumberExists(String studentNumber) {
        return studentDAO.studentNumberExists(studentNumber);
    }
}
