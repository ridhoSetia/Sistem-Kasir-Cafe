package com.cafe.controller.Manager;

import com.cafe.repository.MenuRepository;
import com.cafe.utils.*;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormTambahMenuController {
    @FXML private TextField txtNamaMenuForm;
    @FXML private TextField txtHargaMenuForm;
    @FXML private TextField txtStokMenuForm;
    @FXML private ComboBox<String> cmbKategoriMenuForm;

    // Menghubungkan ke KelolaMenuController karena berada di package yang sama
    protected KelolaMenuController mainController;
    private final MenuRepository menuRepository = new MenuRepository();

    @FXML
    public void initialize() {
        cmbKategoriMenuForm.getItems().addAll("Makanan", "Minuman");
    }

    public void setMenuController(KelolaMenuController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleSimpanFormMenu() {
        String nama = txtNamaMenuForm.getText();
        String kategori = cmbKategoriMenuForm.getValue();

        if (nama.isEmpty() || txtHargaMenuForm.getText().isEmpty() || txtStokMenuForm.getText().isEmpty() || kategori == null) {
            Alerts.tampilkanModal("Peringatan", "Semua kolom data harus diisi!");
            return;
        }

        try {
            double harga = Double.parseDouble(txtHargaMenuForm.getText());
            int stok = Integer.parseInt(txtStokMenuForm.getText());

            if (menuRepository.addMenu(nama, harga, kategori, stok)) {
                Alerts.tampilkanModal("Sukses", "Menu baru berhasil ditambahkan!");

                if (mainController != null) {
                    mainController.loadMenuData(); // Memicu refresh tabel utama
                }

                Stage stage = (Stage) txtNamaMenuForm.getScene().getWindow();
                stage.close();
            } else {
                Alerts.tampilkanModal("Gagal", "Gagal menyimpan menu ke database.");
            }

        } catch (NumberFormatException e) {
            Alerts.tampilkanModal("Error", "Harga dan Stok harus berupa angka valid!");
        }
    }

    @FXML
    private void handleBatalFormMenu() {
        Stage stage = (Stage) txtNamaMenuForm.getScene().getWindow();
        stage.close();
    }
}