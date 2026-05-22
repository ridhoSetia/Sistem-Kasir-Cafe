package com.cafe.controller.Manager;

import com.cafe.model.User;
import com.cafe.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormTambahKasirController {
    @FXML private TextField txtNamaFormTambahKasir;
    @FXML private TextField txtUsernameFormTambahKasir;
    @FXML private PasswordField txtPasswordFormTambahKasir;
    @FXML private PasswordField txtKonfirPasswordFormTambahKasir;
    @FXML private Button btnBatalFormTambahKasir;
    @FXML private Button btnSimpanFormTambahKasir;

    private final UserRepository userRepository = new UserRepository();
    
    private int selectedIdUser = -1; 
    private boolean isEditMode = false;
    private FormTambahKasirController.ParentRefreshable parentController;

    // Interface lokal untuk menjamin fleksibilitas refresh data antar-controller
    public interface ParentRefreshable {
        void tampilkanDataTabel();
    }

    public void setEditDataKasir(User user, FormTambahKasirController.ParentRefreshable parent) {
        this.parentController = parent;
        this.isEditMode = true;
        this.selectedIdUser = user.getIdUser();
        
        // Isi form dengan data lama yang di-select dari TableView
        txtNamaFormTambahKasir.setText(user.getNamaLengkap());
        txtUsernameFormTambahKasir.setText(user.getUsername());
        
        txtPasswordFormTambahKasir.setText("");
        txtKonfirPasswordFormTambahKasir.setText("");
        
        btnSimpanFormTambahKasir.setText("Perbarui");
    }

    public void setTambahMode(FormTambahKasirController.ParentRefreshable parent) {
        this.parentController = parent;
        this.isEditMode = false;
        this.selectedIdUser = -1;
        
        txtNamaFormTambahKasir.clear();
        txtUsernameFormTambahKasir.clear();
        txtPasswordFormTambahKasir.clear();
        txtKonfirPasswordFormTambahKasir.clear();
        
        btnSimpanFormTambahKasir.setText("Simpan");
    }

    @FXML
    private void handleSimpanFormTambahKasir(ActionEvent event) {
        String namaLengkap = txtNamaFormTambahKasir.getText().trim();
        String username = txtUsernameFormTambahKasir.getText().trim();
        String password = txtPasswordFormTambahKasir.getText();
        String konfirPassword = txtKonfirPasswordFormTambahKasir.getText();

        // Validasi Integritas Data (Defensive Logic)
        if (namaLengkap.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.err.println("Peringatan: Semua bidang input wajib diisi!");
            return;
        }

        if (!password.equals(konfirPassword)) {
            System.err.println("Peringatan: Konfirmasi password tidak cocok!");
            return;
        }

        boolean hasil;
        if (isEditMode) {
            hasil = userRepository.updateAkunKasir(selectedIdUser, username, password, namaLengkap);
        } else {
            hasil = userRepository.tambahAkunKasir(username, password, namaLengkap);
        }

        if (hasil) {
            if (parentController != null) {
                parentController.tampilkanDataTabel(); // Segarkan TableView utama
            }
            tutupJendela();
        } else {
            System.err.println("Error: Gagal memproses data ke database server.");
        }
    }

    @FXML
    private void handleBatalFormTambahKasir(ActionEvent event) {
        tutupJendela();
    }

    private void tutupJendela() {
        Stage stage = (Stage) btnBatalFormTambahKasir.getScene().getWindow();
        stage.close();
    }
}