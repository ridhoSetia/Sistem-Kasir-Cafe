package com.cafe.controller;

import com.cafe.config.UserSession;
import com.cafe.repository.UserRepository;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsernameLogin;
    @FXML private PasswordField txtPasswordLogin;
    @FXML private Label lblStatus;
    @FXML private Button btnLogin;

    private final UserRepository userRepository = new UserRepository();

    // Method Validasi Data (Sesuai include di Use Case Diagram)
    private boolean validasiData(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            lblStatus.setText("Error: Username tidak boleh kosong!");
            return false;
        }
        if (password == null || password.isEmpty()) {
            lblStatus.setText("Error: Password tidak boleh kosong!");
            return false;
        }
        return true;
    }

    // Jalankan proses login ketika tombol diklik
    @FXML
    private void handleLogin() {
        String username = txtUsernameLogin.getText();
        String password = txtPasswordLogin.getText();

        if (!validasiData(username, password)) {
            return;
        }

        var user = userRepository.login(username, password);

        if (user != null) {
            lblStatus.setText("Login Sukses!");

            // SIMPAN SESI SECARA DINAMIS DI SINI SEBELUM PINDAH HALAMAN
            UserSession.getInstance().login(user);

            // Ambil role dari objek user
            String role = user.getRole();
            
            pindahKeMenuUtama(role);
        } else {
            lblStatus.setText("Error: Username atau Password salah!");
        }
    }

    // Memindahkan scene ke Menu Utama dan mengirimkan data hak akses (role)
    private void pindahKeMenuUtama(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/MainMenu.fxml"));
            Parent root = loader.load();

            // Mengirim data role ke controller tujuan
            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setSessionData(txtUsernameLogin.getText(), role);

            // Ganti kontainer Stage utama
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            
            // Hubungkan file CSS jika ada
            if (getClass().getResource("/resources/css/brew-society-pos.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/resources/css/brew-society-pos.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Cafe System - Main Menu");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            lblStatus.setText("Error: Gagal memuat Menu Utama!");
            e.printStackTrace();
        }
    }
}