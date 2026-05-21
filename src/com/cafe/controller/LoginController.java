package com.cafe.controller;

import com.cafe.auth.Auth;
import com.cafe.auth.DatabaseAuth;
import com.cafe.config.UserSession;
import com.cafe.model.User;
import com.cafe.utils.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField txtUsernameLogin;
    @FXML
    private PasswordField txtPasswordLogin;
    @FXML
    private Button btnLogin;
    @FXML

    // Deklarasi tipe induk Auth, diisi bentuk konkret DatabaseAuth
    private final Auth authSystem = new DatabaseAuth();

    @FXML
    private void handleLogin() {
        String username = txtUsernameLogin.getText().trim();
        String password = txtPasswordLogin.getText().trim();

        // filter UI
        if (username.isEmpty() || password.isEmpty()) {
            Modal.tampilkanModal("Input Kosong", "Username dan Password wajib diisi!");
            return;
        }

        // Memanggil metod parent, otomatis menjalankan logika child (DatabaseAuth)
        User userLogOn = authSystem.login(username, password);

        if (userLogOn != null) {
            UserSession.getInstance().login(userLogOn);

            // Pengkondisian Navigasi Dinamis Berdasarkan Cetakan Role
            if (userLogOn.getRole().equalsIgnoreCase("Manager")) {
                Modal.tampilkanModal("Login Sukses", "Selamat datang Manager: " + userLogOn.getNamaLengkap());
            } else {
                Modal.tampilkanModal("Login Sukses", "Selamat datang Kasir: " + userLogOn.getNamaLengkap());
            }
            PindahHalaman.pindah(btnLogin, "/resources/MainMenu.fxml", "Brew Society - Menu Manager");

        } else {
            Modal.tampilkanModal("Gagal Autentikasi", "Kredensial salah atau tidak terdaftar di server.");
        }
    }
}