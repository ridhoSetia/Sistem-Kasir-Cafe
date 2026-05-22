package com.cafe.controller.Manager;

import com.cafe.model.Menu;
import com.cafe.repository.MenuRepository;
import com.cafe.utils.PindahHalaman;
import com.cafe.utils.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class KelolaMenuController {

    @FXML
    private TableView<Menu> tbKelolaMenu;
    @FXML
    private TableColumn<Menu, Integer> colIdMenu;
    @FXML
    private TableColumn<Menu, String> colNamaMenu;
    @FXML
    private TableColumn<Menu, Double> colHargaMenu;
    @FXML
    private TableColumn<Menu, String> colKategoriMenu;
    @FXML
    private TableColumn<Menu, Integer> colStokMenu;
    @FXML
    private Button btnKembaliMenu;
    @FXML
    private Button btnTambahMenu;
    @FXML
    private TextField txtCariMenu;

    private final MenuRepository menuRepository = new MenuRepository();
    private final ObservableList<Menu> masterData = FXCollections.observableArrayList();

    private final NumberFormat rupiahFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    @FXML
    public void initialize() {
        // Sinkronisasi properti kolom dengan atribut yang ada pada model Menu.java
        colIdMenu.setCellValueFactory(new PropertyValueFactory<>("idMenu"));
        colNamaMenu.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colHargaMenu.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colKategoriMenu.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colStokMenu.setCellValueFactory(new PropertyValueFactory<>("stok"));

        // Mengubah deretan angka harga mentah menjadi format mata uang Rupiah di tabel
        colHargaMenu.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });

        loadMenuData();
    }

    public void loadMenuData() {
        masterData.clear();
        masterData.addAll(menuRepository.getAllMenu());
        tbKelolaMenu.setItems(masterData);
    }

    @FXML
    private void handleTambahMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Manager/FormTambahMenu.fxml"));
            Parent root = loader.load();

            // Hubungkan relasi callback antar sesama kontroler Manager
            FormTambahMenuController popupController = loader.getController();
            popupController.setMenuController(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Menu Baru - Brew Society");
            stage.initModality(Modality.APPLICATION_MODAL); // Mengunci halaman utama di belakangnya
            stage.initOwner(btnTambahMenu.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait(); // Menggunakan showAndWait agar sinkron saat pop-up ditutup

        } catch (IOException e) {
            System.err.println("[KelolaMenuController] Gagal memuat FormTambahMenu FXML: " + e.getMessage());
            e.printStackTrace();
            Alerts.tampilkanModal("Error System", "Gagal memuat komponen form.");
        }
    }

    @FXML
    private void handleHapusMenu() {
        Menu selectedMenu = tbKelolaMenu.getSelectionModel().getSelectedItem();
        if (selectedMenu != null) {
            if (menuRepository.deleteMenu(selectedMenu.getIdMenu())) {
                Alerts.tampilkanModal("Sukses", "Menu berhasil dihapus!");
                loadMenuData(); // Otomatis refresh data tabel setelah menghapus
            } else {
                Alerts.tampilkanModal("Gagal", "Gagal menghapus menu dari database.");
            }
        } else {
            Alerts.tampilkanModal("Peringatan", "Pilih baris menu di tabel terlebih dahulu!");
        }
    }

    @FXML
    private void handleEditMenu() {
        // 1. Ambil data menu yang sedang diklik di tabel
        Menu selectedMenu = tbKelolaMenu.getSelectionModel().getSelectedItem();

        if (selectedMenu == null) {
            Alerts.tampilkanModal("Peringatan", "Pilih baris menu di tabel terlebih dahulu untuk diedit!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Manager/FormTambahMenu.fxml"));
            Parent root = loader.load();

            // 2. Kirim data menu terpilih ke form
            FormTambahMenuController popupController = loader.getController();
            // Asumsi: Anda harus membuat metode setEditData di FormTambahMenuController
            // yang menerima (Menu data, KelolaMenuController parent)
            popupController.setEditDataMenu(selectedMenu, this);

            Stage stage = new Stage();
            stage.setTitle("Ubah Data Menu - Brew Society");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tbKelolaMenu.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("[KelolaMenuController] Gagal memuat form edit: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCariMenu() {
        // Ambil teks dari kolom pencarian dan ubah ke huruf kecil untuk pencocokan yang fleksibel
        String keyword = txtCariMenu.getText().toLowerCase().trim();

        // Jika inputan kosong, kembalikan tabel ke kondisi data utuh
        if (keyword.isEmpty()) {
            tbKelolaMenu.setItems(masterData);
            return;
        }

        // Lakukan penyaringan (filtering) secara dinamis menggunakan Streams Java modern
        ObservableList<Menu> filteredData = FXCollections.observableArrayList();
        for (Menu menu : masterData) {
            // Cocokkan berdasarkan nama menu atau kategorinya
            if (menu.getNamaMenu().toLowerCase().contains(keyword) ||
                    menu.getKategori().toLowerCase().contains(keyword)) {
                filteredData.add(menu);
            }
        }

        // Tampilkan hasil pencarian ke dalam tabel
        tbKelolaMenu.setItems(filteredData);
    }

    @FXML
    private void handleKembaliMenu() {
        PindahHalaman.pindah(btnKembaliMenu, "/resources/MainMenu.fxml", "Cafe System - Main Menu");
    }
}