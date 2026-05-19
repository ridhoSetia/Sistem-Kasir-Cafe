// Menangani input dari form login 
// dan melakukan validasi melalui UserRepository.

package com.cafe.controller;

import com.cafe.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController {
    @FXML private TextField inputUsername;
    @FXML private PasswordField inputPassword;
    @FXML private Label lblPesan;

    private UserRepository userRepository = new UserRepository();

    // Method Validasi Data
    private boolean validasiData(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            lblPesan.setText("Username/Password tidak boleh kosong!");
            return false;
        }
        if (password.length() < 5) {
            lblPesan.setText("Password minimal 5 karakter!");
            return false;
        }
        return true;
    }

    @FXML
    private void handleLogin() {
        String user = inputUsername.getText();
        String pass = inputPassword.getText();

        // Lakukan Validasi Dulu
        if (validasiData(user, pass)) {
            // 2Jika valid, jalankan proses Login
            String role = userRepository.login(user, pass);
            
            if (role != null) {
                lblPesan.setText("Login Berhasil sebagai " + role);
                // Lanjutkan ke Dashboard sesuai role...
            } else {
                lblPesan.setText("Username atau Password salah!");
            }
        }
    }
}