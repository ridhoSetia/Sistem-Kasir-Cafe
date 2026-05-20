package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {

    @FXML private Label lblWelcome;
    @FXML private Button btnMenuKasir;
    @FXML private Button btnKelolaKasir;
    @FXML private Button btnLaporanPenjualan;
    @FXML private Button btnLogout;

    private String currentRole;

    // Menerima payload data dari LoginController
    public void setSessionData(String username, String role) {
        this.currentRole = role;
        lblWelcome.setText("User: " + username + " (Role: " + role + ")");

        // Penerapan Aturan Hak Akses
        if (!role.equalsIgnoreCase("Manager")) {
            // Sembunyikan fitur Kelola Kasir
            btnKelolaKasir.setVisible(false);
            btnKelolaKasir.setManaged(false);
            
            // Sembunyikan fitur Laporan Penjualan
            btnLaporanPenjualan.setVisible(false);
            btnLaporanPenjualan.setManaged(false);
        }
    }

    @FXML
    private void handleBukaKasir() {
        pindahHalaman("/resources/Kasir/KelolaPembayaran.fxml", "Menu Kasir - Kelola Pembayaran");
    }

    @FXML
    private void handleBukaRiwayat() {
        pindahHalaman("/resources/Kasir/LihatRiwayat.fxml", "Menu Kasir - Riwayat Penjualan");
    }

    @FXML
    private void handleBukaKelolaKasir() {
        pindahHalaman("/resources/Manager/KelolaAkunKasir.fxml", "Menu Manager - Kelola Akun Kasir");
    }

    @FXML
    private void handleBukaLaporan() {
        pindahHalaman("/resources/Manager/LaporanPenjualan.fxml", "Menu Manager - Laporan Penjualan");
    }

    @FXML
    private void handleLogout() {
        pindahHalaman("/resources/Login.fxml", "Aplikasi Kasir - Login");
    }

    private void pindahHalaman(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Infrastruktur Error: Gagal memuat file FXML di " + fxmlPath);
            e.printStackTrace();
        }
    }
}