package com.cafe.controller.Manager;

import com.cafe.model.Kasir;
import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import com.cafe.utils.Alerts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class FormTambahKasirController {

    @FXML private TextField txtNamaFormTambahKasir;
    @FXML private TextField txtUsernameFormTambahKasir;
    @FXML private PasswordField txtPasswordFormTambahKasir;

    private KelolaAkunKasirController parentController;
    private final UserRepository userRepository = new UserRepository();

    private boolean isEditMode = false;
    private int idUserEdit = 0;

    public void setParentController(KelolaAkunKasirController controller) {
        this.parentController = controller;
        this.isEditMode = false;
    }

    public void setEditData(User user, KelolaAkunKasirController controller) {
        this.parentController = controller;
        this.isEditMode = true;
        this.idUserEdit = user.getIdUser();

        txtNamaFormTambahKasir.setText(user.getNamaLengkap());
        txtUsernameFormTambahKasir.setText(user.getUsername());
        txtPasswordFormTambahKasir.setText(user.getPassword()); 
    }

    @FXML
    private void handleTambahKasir(ActionEvent event) {
        String nama = txtNamaFormTambahKasir.getText().trim();
        String username = txtUsernameFormTambahKasir.getText().trim();
        String password = txtPasswordFormTambahKasir.getText().trim();

        if (nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Alerts.tampilkanAlert("Peringatan", "Semua kredensial wajib diisi!");
            return;
        }

        if (isEditMode) {
            // Membungkus input menjadi entitas Kasir, otomatis terbaca sebagai User di Repository
            Kasir kasirUpdate = new Kasir(idUserEdit, username, password, nama);
            if (userRepository.perbarui(kasirUpdate)) {
                Alerts.tampilkanAlert("Sukses", "Data kasir berhasil diperbarui!");
                tutupDanRefresh();
            } else {
                Alerts.tampilkanAlert("Gagal", "Gagal memperbarui data kasir di database.");
            }
        } else {
            Kasir kasirBaru = new Kasir(0, username, password, nama);
            if (userRepository.simpan(kasirBaru)) {
                Alerts.tampilkanAlert("Sukses", "Akun kasir baru berhasil dibuat!");
                tutupDanRefresh();
            } else {
                Alerts.tampilkanAlert("Gagal", "Gagal membuat akun kasir.");
            }
        }
    }

    @FXML
    private void handleBatalFormTambahKasir(ActionEvent event) {
        Stage stage = (Stage) txtNamaFormTambahKasir.getScene().getWindow();
        stage.close();
    }

    private void tutupDanRefresh() {
        if (parentController != null) {
            parentController.tampilkanDataTabel();
        }
        handleBatalFormTambahKasir(null);
    }
}