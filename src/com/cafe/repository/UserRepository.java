package com.cafe.repository;

import com.cafe.config.DBConnection;
import com.cafe.model.User;
import com.cafe.model.Kasir;
import com.cafe.model.Manager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.List;

// SINKRONISASI KONTRAK Mengikat UserRepository ke IRepository dengan tipe User
public class UserRepository implements IRepository<User, Integer> {

    // Autentikasi Login
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    if (role.equalsIgnoreCase("Kasir")) {
                        return new Kasir(resultSet.getInt("id_user"), resultSet.getString("username"), resultSet.getString("password"),
                                resultSet.getString("nama_lengkap"));
                    } else if (role.equalsIgnoreCase("Manager")) {
                        return new Manager(resultSet.getInt("id_user"), resultSet.getString("username"), resultSet.getString("password"),
                                resultSet.getString("nama_lengkap"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserRepository] Error login: " + e.getMessage());
        }
        return null;
    }

    // IMPLEMENTASI KONTRAK DASAR CRUD

    @Override
    public boolean simpan(User entitas) {
        String sql = "INSERT INTO users (nama_lengkap, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entitas.getNamaLengkap());
            stmt.setString(2, entitas.getUsername());
            stmt.setString(3, entitas.getPassword()); // Meyimpan sandi yang diinput dari form
            stmt.setString(4, entitas.getRole());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserRepository] Gagal simpan: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ObservableList<User> ambilSemua() {
        ObservableList<User> listKasir = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users WHERE role = 'Kasir'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                listKasir.add(new Kasir(
                        resultSet.getInt("id_user"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("nama_lengkap")));
            }
        } catch (SQLException e) {
            System.err.println("[UserRepository] Gagal ambilSemua: " + e.getMessage());
        }
        return listKasir;
    }

    @Override
    public boolean perbarui(User entitas) {
        String sql = "UPDATE users SET nama_lengkap = ?, username = ?, password = ? WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entitas.getNamaLengkap());
            stmt.setString(2, entitas.getUsername());
            stmt.setString(3, entitas.getPassword());
            stmt.setInt(4, entitas.getIdUser());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserRepository] Gagal perbarui: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hapus(Integer id) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserRepository] Gagal hapus: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> cari(String keyword) {
        javafx.collections.ObservableList<User> listKasir = javafx.collections.FXCollections.observableArrayList();

        // Pastikan hanya mencari akun yang memiliki role 'Kasir'
        String sql = "SELECT * FROM users WHERE (nama_lengkap LIKE ? OR username LIKE ?) AND role = 'Kasir'";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String polaPencarian = "%" + keyword + "%";
            stmt.setString(1, polaPencarian);
            stmt.setString(2, polaPencarian);

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    listKasir.add(new Kasir(
                            resultSet.getInt("id_user"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("nama_lengkap")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserRepository] Gagal mencari user: " + e.getMessage());
        }

        return listKasir;
    }
}