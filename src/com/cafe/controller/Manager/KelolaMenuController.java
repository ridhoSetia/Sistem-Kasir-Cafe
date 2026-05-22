package com.cafe.controller.Manager;

import com.cafe.model.Menu;
import com.cafe.repository.MenuRepository;
import com.cafe.utils.Alerts;
import com.cafe.utils.PindahHalaman;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private TextField txtCariMenu;
    @FXML
    private Button btnCariMenu;
    @FXML
    private Button btnTambahMenu;
    @FXML
    private Button btnEditMenu;
    @FXML
    private Button btnHapusMenu;
    @FXML
    private Button btnKembaliMenu;

    private final MenuRepository menuRepository = new MenuRepository();
    private final ObservableList<Menu> masterData = FXCollections.observableArrayList();
    private final NumberFormat rupiahFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    @FXML
    public void initialize() {
        setupTabel();
        loadMenuData();
    }

    private void setupTabel() {
        colIdMenu.setCellValueFactory(new PropertyValueFactory<>("idMenu"));
        colNamaMenu.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colKategoriMenu.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colStokMenu.setCellValueFactory(new PropertyValueFactory<>("stok"));

        colHargaMenu.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colHargaMenu.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double harga, boolean empty) {
                super.updateItem(harga, empty);
                if (empty || harga == null) {
                    setText(null);
                } else {
                    setText(rupiahFmt.format(harga));
                }
            }
        });
    }

    // IMPLEMENTASI KONTRAK IRepository READ
    public void loadMenuData() {
        masterData.clear();
        masterData.addAll(menuRepository.ambilSemua());
        tbKelolaMenu.setItems(masterData);
    }

    // IMPLEMENTASI KONTRAK IRepository SEARCH
    @FXML
    private void handleCariMenu() {
        String keyword = txtCariMenu.getText().trim();
        if (keyword.isEmpty()) {
            loadMenuData();
            return;
        }

        // Pengecoran tipe secara dinamis
        ObservableList<Menu> hasilPencarian = FXCollections.observableArrayList(menuRepository.cari(keyword));
        tbKelolaMenu.setItems(hasilPencarian);

        if (hasilPencarian.isEmpty()) {
            Alerts.tampilkanAlert("Informasi", "Menu tidak ditemukan.");
        }
    }

    @FXML
    private void handleTambahMenu() {
        bukaFormModal(null);
    }

    @FXML
    private void handleEditMenu() {
        Menu selectedMenu = tbKelolaMenu.getSelectionModel().getSelectedItem();
        if (selectedMenu == null) {
            Alerts.tampilkanAlert("Peringatan", "Pilih baris menu di tabel terlebih dahulu!");
            return;
        }
        bukaFormModal(selectedMenu);
    }

    // IMPLEMENTASI KONTRAK IRepository DELETE
    @FXML
    private void handleHapusMenu() {
        Menu selectedMenu = tbKelolaMenu.getSelectionModel().getSelectedItem();
        if (selectedMenu != null) {

            if (menuRepository.hapus(selectedMenu.getIdMenu())) {
                Alerts.tampilkanAlert("Sukses", "Menu berhasil dihapus dari database!");
                loadMenuData();
            } else {
                // Beri tahu manajer mengapa database menolak penghapusan
                Alerts.tampilkanAlert("Penolakan Sistem",
                        "Menu ini tidak bisa dihapus karena sudah tercatat di dalam Riwayat Transaksi pelanggan.\n\n" +
                                "Solusi: Jika menu ini sudah tidak dijual, gunakan fitur 'Edit' dan ubah Stok menjadi 0.");
            }

        } else {
            Alerts.tampilkanAlert("Peringatan", "Pilih baris menu di tabel terlebih dahulu!");
        }
    }

    private void bukaFormModal(Menu menuEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Manager/FormTambahMenu.fxml"));
            Parent root = loader.load();
            FormTambahMenuController controller = loader.getController();

            if (menuEdit != null) {
                controller.setEditData(menuEdit, this);
            } else {
                controller.setMenuController(this);
            }

            Stage stage = new Stage();
            stage.setTitle(menuEdit == null ? "Tambah Menu - Brew Society" : "Edit Menu - Brew Society");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tbKelolaMenu.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("[KelolaMenuController] Gagal memuat form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKembaliMenu() {
        PindahHalaman.pindah(btnKembaliMenu, "/resources/MainMenu.fxml", "Brew Society - Main Menu");
    }
}