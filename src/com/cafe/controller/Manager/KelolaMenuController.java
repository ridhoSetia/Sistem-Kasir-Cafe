package com.cafe.controller.Manager;

import com.cafe.model.Menu;
import com.cafe.repository.MenuRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class KelolaMenuController {

    @FXML private TableView<Menu> tableMenu;
    @FXML private TableColumn<Menu, Integer> colId;
    @FXML private TableColumn<Menu, String> colNama;
    @FXML private TableColumn<Menu, Double> colHarga;
    @FXML private TableColumn<Menu, String> colKategori;
    @FXML private TableColumn<Menu, Integer> colStok;

    private final MenuRepository menuRepository = new MenuRepository();
    private ObservableList<Menu> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMenu"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));

        loadMenuData();
    }

    public void loadMenuData() {
        masterData.clear();
        masterData.addAll(menuRepository.getAllMenu());
        tableMenu.setItems(masterData);
    }

    @FXML
    private void handleTambahMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cafe/resources/FormTambahMenu.fxml"));
            Parent root = loader.load();

            // Menghubungkan langsung sesama controller di folder Manager
            FormTambahMenuController popupController = loader.getController();
            popupController.setMenuController(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Menu Baru");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka Form Tambah Menu: " + e.getMessage());
        }
    }

    @FXML
    private void handleHapusMenu() {
        Menu selectedMenu = tableMenu.getSelectionModel().getSelectedItem();
        if (selectedMenu != null) {
            if (menuRepository.deleteMenu(selectedMenu.getIdMenu())) {
                showAlert("Sukses", "Menu berhasil dihapus!");
                loadMenuData();
            } else {
                showAlert("Gagal", "Gagal menghapus menu.");
            }
        } else {
            showAlert("Peringatan", "Pilih menu di tabel terlebih dahulu!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}