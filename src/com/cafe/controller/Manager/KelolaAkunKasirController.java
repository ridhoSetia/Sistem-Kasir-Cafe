package com.cafe.controller.Manager;

import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class KelolaAkunKasirController implements FormTambahKasirController.ParentRefreshable {

    @FXML
    private TableView<User> tbAkunKasir;
    @FXML
    private TableColumn<User, Integer> tbCIDAkunKasir;
    @FXML
    private TableColumn<User, String> tbCNamaAkunKasir;
    @FXML
    private TableColumn<User, String> tbCUsernameAkunKasir;
    @FXML
    private Button btnTambahAkunKasir;
    @FXML
    private Button btnKembaliMenu;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void initialize() {
        // Pemetaan data model dengan kolom FXML secara presisi
        tbCIDAkunKasir.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        tbCNamaAkunKasir.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        tbCUsernameAkunKasir.setCellValueFactory(new PropertyValueFactory<>("username"));

        tampilkanDataTabel();
    }

    @Override
    public void tampilkanDataTabel() {
        ObservableList<User> dataKasir = userRepository.readDataAkunKasir();
        tbAkunKasir.setItems(dataKasir);
    }

    @FXML
    private void handleTambahAkunKasir(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Manager/FormTambahKasir.fxml"));
            Parent root = loader.load();

            FormTambahKasirController formController = loader.getController();
            formController.setTambahMode(this);

            bukaModalWindow("Tambah Akun Kasir Baru", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditAkunKasir(ActionEvent event) {
        User selectedUser = tbAkunKasir.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            tampilkanAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data kasir yang ingin diedit!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Manager/FormTambahKasir.fxml"));
            Parent root = loader.load();

            FormTambahKasirController formController = loader.getController();
            formController.setEditDataKasir(selectedUser, this);

            bukaModalWindow("Ubah Data Akun Kasir", root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHapusAkunKasir(ActionEvent event) {
        User selectedUser = tbAkunKasir.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            tampilkanAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data kasir yang ingin dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus akun " + selectedUser.getNamaLengkap() + "?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> response = confirm.showAndWait();

        if (response.isPresent() && response.get() == ButtonType.YES) {
            if (userRepository.hapusAkunKasir(selectedUser.getIdUser())) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Akun berhasil dihapus!");
                tampilkanDataTabel();
            }
        }
    }

    private void bukaModalWindow(String title, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(btnTambahAkunKasir.getScene().getWindow());
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.showAndWait();
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCari(ActionEvent event) {
    }

    @FXML
    private void handleKembaliMenu() {
        // Memanggil fungsi utilitas statis tanpa instansiasi objek baru
        com.cafe.utils.KembaliMenu.kembaliKe(btnKembaliMenu, "/resources/MainMenu.fxml", "Cafe System - Main Menu");
    }
}