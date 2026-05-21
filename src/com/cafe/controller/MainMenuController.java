package com.cafe.controller;

import com.cafe.utils.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Button btnMenuKasir;
    @FXML
    private Button btnRiwayatPenjualan;
    @FXML
    private Button btnKelolaKasir;
    @FXML
    private Button btnKelolaMenu;
    @FXML
    private Button btnLogout;

    private String currentRole;

    // Menerima payload data dari LoginController
    public void setSessionData(String username, String role) {
        this.currentRole = role;
        lblWelcome.setText("User: " + username + " (Role: " + role + ")");

        // Mengambil objek user aktif yang sesungguhnya dari memori RAM (bisa berupa Kasir / Manager)
        com.cafe.model.User userAktif = com.cafe.config.UserSession.getInstance().getUserAktif();
        if (userAktif != null) {
            // Jika objeknya adalah Kasir, tombol otomatis sembunyi. Jika Manager, tombol tetap muncul.
            userAktif.konfigurasiHakAkses(btnKelolaKasir, btnKelolaMenu);
        }
    }

    @FXML
    private void handleBukaKasir() {
        PindahHalaman.pindah(btnMenuKasir, "/resources/Kasir/KelolaPembayaran.fxml", "Menu Kasir - Kelola Pembayaran");
    }

    @FXML
    private void handleBukaRiwayat() {
        PindahHalaman.pindah(btnRiwayatPenjualan, "/resources/Kasir/LihatRiwayat.fxml", "Menu Kasir - Riwayat Penjualan");
    }

    @FXML
    private void handleBukaKelolaKasir() {
        PindahHalaman.pindah(btnKelolaKasir, "/resources/Manager/KelolaAkunKasir.fxml", "Menu Manager - Kelola Akun Kasir");
    }

    @FXML
    private void handleBukaKelolaMenu() {
        PindahHalaman.pindah(btnKelolaMenu, "/resources/Manager/KelolaMenu.fxml", "Menu Manager - Kelola Menu Cafe");
    }

    @FXML
    private void handleLogout() {
        PindahHalaman.pindah(btnLogout, "/resources/Login.fxml", "Aplikasi Kasir - Login");
    }
}