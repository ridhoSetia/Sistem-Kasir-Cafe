package com.cafe.controller.Manager;

import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import com.cafe.utils.Alerts;
import com.cafe.utils.PindahHalaman;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class KelolaAkunKasirController {

    @FXML private TableView<User> tbAkunKasir;
    @FXML private TableColumn<User, Integer> tbCIDAkunKasir;
    @FXML private TableColumn<User, String> tbCNamaAkunKasir;
    @FXML private TableColumn<User, String> tbCUsernameAkunKasir;
    
    @FXML private TextField txtCariNamaAkunKasir;
    @FXML private Button btnCariAkunKasir;
    @FXML private Button btnTambahAkunKasir;
    @FXML private Button btnEditAkunKasir;
    @FXML private Button btnHapusAkunKasir;
    @FXML private Button btnKembaliMenu;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize() {
        setupTabel();
        tampilkanDataTabel();
    }

    private void setupTabel() {
        tbCIDAkunKasir.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        tbCNamaAkunKasir.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        tbCUsernameAkunKasir.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    public void tampilkanDataTabel() {
        tbAkunKasir.setItems(userRepository.ambilSemua());
    }

    @FXML
    private void handleCariKasir(ActionEvent event) {
        String keyword = txtCariNamaAkunKasir.getText().trim();
        if (keyword.isEmpty()) {
            tampilkanDataTabel();
            return;
        }
        
        ObservableList<User> hasil = FXCollections.observableArrayList(userRepository.cari(keyword));
        tbAkunKasir.setItems(hasil);
        
        if (hasil.isEmpty()) {
            Alerts.tampilkanAlert("Informasi", "Akun kasir tidak ditemukan.");
        }
    }

    @FXML
    private void handleTambahAkunKasir(ActionEvent event) {
        bukaFormModal(null);
    }

    @FXML
    private void handleEditAkunKasir(ActionEvent event) {
        User selectedUser = tbAkunKasir.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alerts.tampilkanAlert("Peringatan", "Pilih akun kasir yang ingin diedit.");
            return;
        }
        bukaFormModal(selectedUser);
    }

    @FXML
    private void handleHapusAkunKasir(ActionEvent event) {
        User selectedUser = tbAkunKasir.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userRepository.hapus(selectedUser.getIdUser())) {
                Alerts.tampilkanAlert("Sukses", "Akun kasir berhasil dihapus!");
                tampilkanDataTabel();
            } else {
                Alerts.tampilkanAlert("Gagal", "Gagal menghapus akun kasir dari sistem.");
            }
        } else {
            Alerts.tampilkanAlert("Peringatan", "Pilih akun kasir yang ingin dihapus.");
        }
    }

    private void bukaFormModal(User userEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Manager/FormTambahKasir.fxml"));
            Parent root = loader.load();
            FormTambahKasirController controller = loader.getController();

            if (userEdit != null) {
                controller.setEditData(userEdit, this);
            } else {
                controller.setParentController(this);
            }

            Stage stage = new Stage();
            stage.setTitle(userEdit == null ? "Tambah Akun Kasir" : "Edit Akun Kasir");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tbAkunKasir.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("[KelolaAkunKasirController] Gagal membuka form: " + e.getMessage());
        }
    }

    @FXML
    private void handleKembaliMenu(ActionEvent event) {
        PindahHalaman.pindah(btnKembaliMenu, "/resources/MainMenu.fxml", "Brew Society - Main Menu");
    }
}