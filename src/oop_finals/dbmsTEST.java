/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oop_finals;

/**
 *
 * @author bacas
 */
import java.sql.*;

public class dbmsTEST {
    public static void main(String[] args) {
        String DB_URL = "jdbc:mysql://localhost:3306/guidance_appointment_system";
        String DB_USER = "root";
        String DB_PASSWORD = "";
        
        try {
            System.out.println("Attempting to connect...");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ CONNECTION SUCCESSFUL!");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            
            System.out.println("\nStudents in database:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") + 
                                 " | Name: " + rs.getString("name"));
            }
            
            conn.close();
        } catch (SQLException e) {
            System.out.println("✗ CONNECTION FAILED!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
