package com.cafe.repository;

import com.cafe.config.DBConnection;
import com.cafe.model.Menu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    // Ambil semua data menu dari database (Milik Manager)
    public List<Menu> getAllMenu() {
        List<Menu> listMenu = new ArrayList<>();
        String sql = "SELECT * FROM menu";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Menu menu = new Menu(
                        rs.getInt("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getDouble("harga"),
                        rs.getString("kategori"),
                        rs.getInt("stok")
                );
                listMenu.add(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listMenu;
    }

    // Tambah data menu baru (Milik Manager)
    public boolean addMenu(String nama, double harga, String kategori, int stok) {
        String sql = "INSERT INTO menu (nama_menu, harga, kategori, stok) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nama);
            pstmt.setDouble(2, harga);
            pstmt.setString(3, kategori);
            pstmt.setInt(4, stok);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hapus data menu (Milik Manager)
    public boolean deleteMenu(int idMenu) {
        String sql = "DELETE FROM menu WHERE id_menu = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idMenu);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mengambil data menu yang stoknya masih di atas 0 (Tersedia)
    public List<Menu> getMenuTersedia() {
        List<Menu> listMenu = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE stok > 0";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Menu menu = new Menu(
                        rs.getInt("id_menu"),
                        rs.getString("nama_menu"),
                        rs.getDouble("harga"),
                        rs.getString("kategori"),
                        rs.getInt("stok")
                );
                listMenu.add(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listMenu;
    }

    // Mencari menu berdasarkan kemiripan nama (Fitur Search Kasir)
    public List<Menu> cariMenuByNama(String namaCari) {
        List<Menu> listMenu = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE nama_menu LIKE ? AND stok > 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + namaCari + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Menu menu = new Menu(
                            rs.getInt("id_menu"),
                            rs.getString("nama_menu"),
                            rs.getDouble("harga"),
                            rs.getString("kategori"),
                            rs.getInt("stok")
                    );
                    listMenu.add(menu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listMenu;
    }
}