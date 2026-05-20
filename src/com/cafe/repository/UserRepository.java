// Berisi method login(String username, String password) 
// yang mengembalikan nilai boolean atau objek User.

package com.cafe.repository;

import com.cafe.config.DBConnection;
import com.cafe.model.Kasir;
import com.cafe.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public ObservableList<User> readDataAkunKasir() {
        ObservableList<User> listKasir = FXCollections.observableArrayList();
        String query = "SELECT * FROM users WHERE role = 'Kasir'";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User kasir = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("nama_lengkap") // Ambil dari database
                );
                listKasir.add(kasir);
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Read): " + e.getMessage());
        }
        return listKasir;
    }

    public boolean tambahAkunKasir(String username, String password, String namaLengkap) {
        String query = "INSERT INTO users (username, password, role, nama_lengkap) VALUES (?, ?, 'Kasir', ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, namaLengkap);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database Error (Insert): " + " Username mungkin sudah digunakan.");
            return false;
        }
    }

    // Memperbarui data kasir berdasarkan id_user
    public boolean updateAkunKasir(int idUser, String username, String password, String namaLengkap) {
        String query = "UPDATE users SET username = ?, password = ?, nama_lengkap = ? WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, namaLengkap);
            stmt.setInt(4, idUser);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database Error (Update): " + e.getMessage());
            return false;
        }
    }

    // Menghapus data kasir berdasarkan id_user
    public boolean hapusAkunKasir(int idUser) {
        String query = "DELETE FROM users WHERE id_user = ? AND role = 'Kasir'";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database Error (Delete): " + e.getMessage());
            return false;
        }
    }
}