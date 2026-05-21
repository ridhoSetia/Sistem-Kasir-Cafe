package com.cafe.controller.Manager;

import com.cafe.repository.MenuRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormTambahMenuController {
    @FXML private TextField txtNamaMenu;
    @FXML private TextField txtHarga;
    @FXML private TextField txtStok;
    @FXML private ComboBox<String> cbKategori;

    // Menghubungkan ke KelolaMenuController karena berada di package yang sama
    private KelolaMenuController mainController;
    private final MenuRepository menuRepository = new MenuRepository();

    @FXML
    public void initialize() {
        cbKategori.getItems().addAll("Makanan", "Minuman");
    }

    public void setMenuController(KelolaMenuController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleSimpan() {
        String nama = txtNamaMenu.getText();
        String kategori = cbKategori.getValue();

        if (nama.isEmpty() || txtHarga.getText().isEmpty() || txtStok.getText().isEmpty() || kategori == null) {
            showAlert("Peringatan", "Semua kolom data harus diisi!");
            return;
        }

        try {
            double harga = Double.parseDouble(txtHarga.getText());
            int stok = Integer.parseInt(txtStok.getText());

            if (menuRepository.addMenu(nama, harga, kategori, stok)) {
                showAlert("Sukses", "Menu baru berhasil ditambahkan!");

                if (mainController != null) {
                    mainController.loadMenuData(); // Memicu refresh tabel utama
                }

                Stage stage = (Stage) txtNamaMenu.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Gagal", "Gagal menyimpan menu ke database.");
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Harga dan Stok harus berupa angka valid!");
        }
    }

    @FXML
    private void handleBatal() {
        Stage stage = (Stage) txtNamaMenu.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}