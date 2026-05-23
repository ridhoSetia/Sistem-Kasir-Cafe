package com.cafe.repository;

import com.cafe.config.DBConnection;
import com.cafe.model.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.List;

public class MenuRepository implements IRepository<Menu, Integer> {

    @Override
    public boolean simpan(Menu entitas) {
        String sql = "INSERT INTO menu (nama_menu, harga, kategori, stok) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entitas.getNamaMenu());
            stmt.setDouble(2, entitas.getHarga());
            stmt.setString(3, entitas.getKategori());
            stmt.setInt(4, entitas.getStok());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MenuRepository] Gagal simpan: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Menu> ambilSemua() {
        ObservableList<Menu> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM menu";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                list.add(new Menu(
                        resultSet.getInt("id_menu"),
                        resultSet.getString("nama_menu"),
                        resultSet.getDouble("harga"),
                        resultSet.getString("kategori"),
                        resultSet.getInt("stok")));
            }
        } catch (SQLException e) {
            System.err.println("[MenuRepository] Gagal ambilSemua: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean perbarui(Menu entitas) {
        String sql = "UPDATE menu SET nama_menu = ?, harga = ?, kategori = ?, stok = ? WHERE id_menu = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entitas.getNamaMenu());
            stmt.setDouble(2, entitas.getHarga());
            stmt.setString(3, entitas.getKategori());
            stmt.setInt(4, entitas.getStok());
            stmt.setInt(5, entitas.getIdMenu());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MenuRepository] Gagal perbarui: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hapus(Integer id) {
        String sql = "DELETE FROM menu WHERE id_menu = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MenuRepository] Gagal hapus: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Menu> cari(String keyword) {
        javafx.collections.ObservableList<Menu> listPencarian = javafx.collections.FXCollections.observableArrayList();

        String sql = "SELECT * FROM menu WHERE nama_menu LIKE ? OR kategori LIKE ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String polaPencarian = "%" + keyword + "%";
            stmt.setString(1, polaPencarian);
            stmt.setString(2, polaPencarian);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    listPencarian.add(new Menu(
                            resultSet.getInt("id_menu"),
                            resultSet.getString("nama_menu"),
                            resultSet.getDouble("harga"),
                            resultSet.getString("kategori"),
                            resultSet.getInt("stok")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[MenuRepository] Gagal mencari menu: " + e.getMessage());
        }

        return listPencarian;
    }
}