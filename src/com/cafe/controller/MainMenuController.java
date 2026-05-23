package com.cafe.controller;

import com.cafe.config.UserSession;
import com.cafe.model.User;
import com.cafe.utils.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainMenuController {

    @FXML private Label lblWelcome;
    @FXML private Button btnMenuKasir;
    @FXML private Button btnRiwayatPenjualan;
    @FXML private Button btnKelolaKasir;
    @FXML private Button btnKelolaMenu;
    @FXML private Button btnLogout;

    // initialize() bawaan JavaFX.
    // Metode ini otomatis dieksekusi oleh sistem sesaat setelah MainMenu.fxml berhasil dimuat.
    @FXML
    public void initialize() {
        // Ambil objek user aktif yang sesungguhnya dari memori RAM (Singleton)
        User userAktif = UserSession.getInstance().getUserAktif();

        if (userAktif != null) {
            // Set teks label sapaan secara dinamis
            lblWelcome.setText("User: " + userAktif.getUsername() + " (Role: " + userAktif.getRole() + ")");

            // Tombol otomatis diatur berdasarkan peran yang login
            userAktif.konfigurasiHakAkses(btnKelolaKasir, btnKelolaMenu, btnMenuKasir);
        } else {
            lblWelcome.setText("User: Tidak Terdeteksi");
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
        // Membersihkan sesi RAM saat logout
        UserSession.getInstance().logout(); 
        PindahHalaman.pindah(btnLogout, "/resources/Login.fxml", "Aplikasi Kasir - Login");
    }
}