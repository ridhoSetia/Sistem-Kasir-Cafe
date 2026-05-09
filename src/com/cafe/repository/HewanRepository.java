package com.cafe.repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cafe.config.DBConnection;
import com.cafe.model.Hewan;

public class HewanRepository {
    public void save(Hewan hewan) throws SQLException {
        String sql = "INSERT INTO hewan (nama, umur) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hewan.getNama());
            pstmt.setInt(2, hewan.getUmur());
            pstmt.executeUpdate();
        }
    }
}