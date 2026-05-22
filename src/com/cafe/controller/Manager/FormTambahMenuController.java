package com.cafe.controller.Manager;

import com.cafe.model.Menu;
import com.cafe.repository.MenuRepository;
import com.cafe.utils.Alerts;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FormTambahMenuController {
    @FXML private TextField txtNamaMenuForm;
    @FXML private TextField txtHargaMenuForm;
    @FXML private TextField txtStokMenuForm;
    @FXML private ComboBox<String> cmbKategoriMenuForm;

    // Relasi ke halaman utama untuk memicu penyegaran tabel
    private KelolaMenuController mainController;
    private final MenuRepository menuRepository = new MenuRepository();

    // VARIABEL KONTROL STATE Untuk membedakan apakah form sedang menambah atau mengedit
    private boolean isEditMode = false;
    private int idMenuEdit = 0;

    @FXML
    public void initialize() {
        cmbKategoriMenuForm.getItems().addAll("Makanan", "Minuman");
    }

    // Dipanggil saat menekan tombol "Tambah Menu"
    public void setMenuController(KelolaMenuController controller) {
        this.mainController = controller;
        this.isEditMode = false; // Pastikan status edit dimatikan
    }

    // Dipanggil saat menekan tombol "Edit Menu" 
    // Berfungsi menyuntikkan data menu lama ke dalam Text Field
    public void setEditData(Menu menu, KelolaMenuController controller) {
        this.mainController = controller;
        this.isEditMode = true; // Aktifkan status edit
        this.idMenuEdit = menu.getIdMenu(); // Simpan ID untuk query UPDATE nanti

        // Memasukkan data ke komponen visual
        txtNamaMenuForm.setText(menu.getNamaMenu());
        cmbKategoriMenuForm.setValue(menu.getKategori());
        
        // Pengecoran Tipe (Type Casting), Mengubah angka menjadi teks
        txtHargaMenuForm.setText(String.valueOf(menu.getHarga()));
        txtStokMenuForm.setText(String.valueOf(menu.getStok()));
    }

    @FXML
    private void handleSimpanFormMenu() {
        String nama = txtNamaMenuForm.getText().trim();
        String kategori = cmbKategoriMenuForm.getValue();
        String hargaStr = txtHargaMenuForm.getText().trim();
        String stokStr = txtStokMenuForm.getText().trim();

        // Validasi input kosong
        if (nama.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty() || kategori == null) {
            Alerts.tampilkanAlert("Peringatan", "Semua kolom data harus diisi!");
            return;
        }

        try {
            double harga = Double.parseDouble(hargaStr);
            int stok = Integer.parseInt(stokStr);

            // PERCABANGAN LOGIKA BERDASARKAN MODE
            if (isEditMode) {
                // Eksekusi Logika EDIT (Update Data)
                Menu menuUpdate = new Menu(idMenuEdit, nama, harga, kategori, stok);
                
                if (menuRepository.perbarui(menuUpdate)) {
                    Alerts.tampilkanAlert("Sukses", "Data menu berhasil diperbarui!");
                    refreshTabelUtama();
                    tutupForm();
                } else {
                    Alerts.tampilkanAlert("Gagal", "Gagal memperbarui data menu di database.");
                }
                } else {
                // Merakit objek menuBaru di memori RAM
                Menu menuBaru = new Menu(0, nama, harga, kategori, stok);
                
                // MENGIRIM OBJEK menuBaru TERSEBUT
                if (menuRepository.simpan(menuBaru)) {
                    Alerts.tampilkanAlert("Sukses", "Menu baru berhasil ditambahkan!");
                    refreshTabelUtama();
                    tutupForm();
                } else {
                    Alerts.tampilkanAlert("Gagal", "Gagal menyimpan menu ke database.");
                }
            }

        } catch (NumberFormatException e) {
            Alerts.tampilkanAlert("Error Input", "Harga dan Stok harus berisi angka murni!");
        }
    }

    @FXML
    private void handleBatalFormMenu() {
        tutupForm();
    }
    
    private void refreshTabelUtama() {
        if (mainController != null) {
            mainController.loadMenuData(); // Memicu tabel di belakang untuk memuat ulang data
        }
    }

    private void tutupForm() {
        Stage stage = (Stage) txtNamaMenuForm.getScene().getWindow();
        stage.close();
    }
}