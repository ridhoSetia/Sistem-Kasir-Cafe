// Berisi method login(String username, String password) 
// yang mengembalikan nilai boolean atau objek User.

package com.cafe.repository;

import com.cafe.config.DBConnection;
import java.sql.*;

public class UserRepository {
    
    public String login(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // Mengembalikan 'Kasir' atau 'Manager'
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login gagal
    }
}