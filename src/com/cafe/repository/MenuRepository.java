// Berisi operasi CRUD (Create, Read, Update, Delete) untuk menu cafe.

package com.cafe.repository;
import com.cafe.config.DBConnection;
import com.cafe.model.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    public List<Menu> getMenuTersedia() {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE stok > 0 ORDER BY kategori, nama_menu";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[MenuRepository] getMenuTersedia: " + e.getMessage());
        }
        return list;
    }
    public List<Menu> cariMenuByNama(String keyword) {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE LOWER(nama_menu) LIKE ? AND stok > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[MenuRepository] cariMenuByNama: " + e.getMessage());
        }
        return list;
    }

    // Ambil satu menu berdasarkan ID.
    public Menu getMenuById(int idMenu) {
        String sql = "SELECT * FROM menu WHERE id_menu = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMenu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("[MenuRepository] getMenuById: " + e.getMessage());
        }
        return null;
    }
    private Menu mapRow(ResultSet rs) throws SQLException {
        return new Menu(
                rs.getInt("id_menu"),
                rs.getString("nama_menu"),
                rs.getDouble("harga"),
                rs.getString("kategori"),
                rs.getInt("stok")
        );
    }
}
