package oop_finals;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Student Data Access Object
 * Handles all database operations for Student entity
 */
public class StudentDAO {
    
    private static final java.util.logging.Logger logger = 
        java.util.logging.Logger.getLogger(StudentDAO.class.getName());
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "09@denverrr";
    
    /**
     * Get database connection
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            logger.severe("MySQL Driver not found: " + e.getMessage());
            throw new SQLException("Database driver error", e);
        }
    }
    
    /**
     * Authenticate student login
     * @param email Student email
     * @param password Student password
     * @return Student object if authenticated, null otherwise
     */
    public Student authenticateStudent(String email, String password) {
        String query =
            "SELECT " +
            "s.student_id, s.user_id, s.name, s.email, s.student_number, " +
            "s.course, s.year_level, s.status " +
            "FROM students s " +
            "JOIN users u ON u.user_id = s.user_id " +
            "WHERE s.email = ? AND s.password = ? AND u.status = 'Active'";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error authenticating student: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get student by ID
     * @param studentId Student ID
     * @return Student object or null if not found
     */
    public Student getStudentById(int studentId) {
        String query = "SELECT * FROM students WHERE student_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting student by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get student by email
     * @param email Student email
     * @return Student object or null if not found
     */
    public Student getStudentByEmail(String email) {
        String query = "SELECT * FROM students WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting student by email: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM students WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            logger.severe("Error checking email existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if student number exists
     * @param studentNumber Student number to check
     * @return true if exists, false otherwise
     */
    public boolean studentNumberExists(String studentNumber) {
        String query = "SELECT COUNT(*) FROM students WHERE student_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, studentNumber);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            logger.severe("Error checking student number existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Create new student registration request
     * @param student Student object with registration data
     * @return true if successful, false otherwise
     */
    public boolean createRegistrationRequest(Student student) {
        String query = "INSERT INTO user_requests (user_type, name, email, password, " +
                      "course, student_number, year_level, status) " +
                      "VALUES ('Student', ?, ?, ?, ?, ?, ?, 'Pending')";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, student.getName());
            pst.setString(2, student.getEmail());
            pst.setString(3, student.getPassword());
            pst.setString(4, student.getCourse());
            pst.setString(5, student.getStudentNumber());
            pst.setString(6, student.getYearLevel());
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error creating registration request: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update student profile
     * @param student Student object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET name = ?, email = ?, course = ?, " +
                      "year_level = ? WHERE student_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, student.getName());
            pst.setString(2, student.getEmail());
            pst.setString(3, student.getCourse());
            pst.setString(4, student.getYearLevel());
            pst.setInt(5, student.getStudentId());
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error updating student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all students
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            logger.severe("Error getting all students: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Delete student (soft delete - set status to inactive)
     * @param studentId Student ID
     * @return true if successful, false otherwise
     */
    public boolean deleteStudent(int studentId) {
        String query = "UPDATE students SET status = 'Inactive' WHERE student_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setInt(1, studentId);
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.severe("Error deleting student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract Student object from ResultSet
     * @param rs ResultSet from query
     * @return Student object
     * @throws SQLException if error reading ResultSet
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        
        // Check if user_id exists in the result set
        try {
            student.setUserId(rs.getInt("user_id"));
        } catch (SQLException e) {
            // user_id might not be in the result set
        }
        
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setCourse(rs.getString("course"));
        student.setYearLevel(rs.getString("year_level"));
        
        // Check if status exists
        try {
            student.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            student.setStatus("Active"); // Default
        }
        
        return student;
    }
}