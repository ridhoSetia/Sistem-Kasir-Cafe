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
        System.out.println("Fitur Edit Menu Terpilih");
    }

    @FXML
    private void handleCariMenu() {
        System.out.println("Fitur Pencarian Dinamis");
    }

    @FXML
    private void handleKembaliMenu() {
        PindahHalaman.pindah(btnKembaliMenu, "/resources/MainMenu.fxml", "Cafe System - Main Menu");
    }
}