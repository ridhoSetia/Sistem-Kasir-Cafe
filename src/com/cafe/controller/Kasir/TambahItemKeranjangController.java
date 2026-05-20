// Menangani logika "Kelola Pembayaran", "Input Menu", dan "Cetak Nota".

package com.cafe.controller.Kasir;

import com.cafe.model.DetailTransaksi;
import com.cafe.model.Menu;
import com.cafe.repository.MenuRepository;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class TambahItemKeranjangController {
    @FXML
    private TextField txtCariMenuTambahItem;
    @FXML
    private AnchorPane txtHasilPencarianTambahItem; // panel kiri: list hasil
    @FXML
    private AnchorPane txtPreviewMenuTambahItem; // panel kanan: preview
    @FXML
    private Label lblNamaMenuTambahItem;
    @FXML
    private Label lblHargaSatuanTambahItem;
    @FXML
    private Spinner<Integer> spJumlahTambahItem;
    @FXML
    private Button btnTambahItemKeKeranjang;
    @FXML
    private Button btnEditTambahItemKeKeranjang;

    private final MenuRepository menuRepository = new MenuRepository();
    private KelolaPembayaranController parentController;
    private Menu menuTerpilih = null;

    @FXML
    public void initialize() {
        // Setup Spinner: nilai awal 1, minimum 1, maksimum 99
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        spJumlahTambahItem.setValueFactory(valueFactory);

        // Tampilkan semua menu tersedia saat popup dibuka
        tampilkanHasilPencarian(menuRepository.getMenuTersedia());
    }

    public void setParentController(KelolaPembayaranController parent) {
        this.parentController = parent;
    }

    @FXML
    private void handleCariMenu() {
        String keyword = txtCariMenuTambahItem.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            // Kosong → tampilkan semua menu
            tampilkanHasilPencarian(menuRepository.getMenuTersedia());
        } else {
            // Ada keyword → filter dari repository
            tampilkanHasilPencarian(menuRepository.cariMenuByNama(keyword));
        }
    }

    private void tampilkanHasilPencarian(List<Menu> hasilMenu) {
        // Bersihkan panel kiri dulu, lalu isi ulang
        VBox listContainer = new VBox(8);
        listContainer.setLayoutX(10);
        listContainer.setLayoutY(40);

        if (hasilMenu.isEmpty()) {
            Label lblKosong = new Label("Tidak ada menu ditemukan.");
            lblKosong.getStyleClass().add("label-muted");
            listContainer.getChildren().add(lblKosong);
        } else {
            for (Menu menu : hasilMenu) {
                Label lblMenu = new Label(menu.getNamaMenu()
                        + "  (Stok: " + menu.getStok() + ")");
                lblMenu.getStyleClass().add("label-muted");
                lblMenu.setStyle("-fx-cursor: hand;");

                // Klik item → tampilkan preview di panel kanan
                lblMenu.setOnMouseClicked(e -> pilihMenu(menu));

                listContainer.getChildren().add(lblMenu);
            }
        }

        txtHasilPencarianTambahItem.getChildren().clear();

        // Pertahankan label judul "List Hasil Pencarian"
        Label lblJudul = new Label("List Hasil Pencarian");
        lblJudul.getStyleClass().add("label-muted");
        lblJudul.setLayoutX(93);
        lblJudul.setLayoutY(14);
        txtHasilPencarianTambahItem.getChildren().addAll(lblJudul, listContainer);
    }

    private void pilihMenu(Menu menu) {
        this.menuTerpilih = menu;

        // Update label preview
        lblNamaMenuTambahItem.setText(menu.getNamaMenu());
        lblHargaSatuanTambahItem.setText(
                String.format("Rp %,.0f", menu.getHarga()));

        // Reset spinner ke 1
        spJumlahTambahItem.getValueFactory().setValue(1);
    }

    @FXML
    private void handleEditTambahItemKeKeranjang() {
        menuTerpilih = null;
        lblNamaMenuTambahItem.setText("-");
        lblHargaSatuanTambahItem.setText("-");
        spJumlahTambahItem.getValueFactory().setValue(1);
        txtCariMenuTambahItem.clear();
        tampilkanHasilPencarian(menuRepository.getMenuTersedia());
    }

    @FXML
    private void handleTambahItemKeKeranjang() {
        // Validasi: harus ada menu yang dipilih
        if (menuTerpilih == null) {
            tampilkanAlert(Alert.AlertType.WARNING,
                    "Belum Pilih Menu", "Pilih menu dari daftar terlebih dahulu.");
            return;
        }

        int jumlah = spJumlahTambahItem.getValue();

        // Validasi: stok harus cukup
        if (menuTerpilih.getStok() < jumlah) {
            tampilkanAlert(Alert.AlertType.ERROR,
                    "Stok Tidak Cukup",
                    "Stok " + menuTerpilih.getNamaMenu()
                            + " hanya tersisa " + menuTerpilih.getStok() + ".");
            return;
        }
        // Buat DetailTransaksi dan kirim ke parent controller
        DetailTransaksi detail = new DetailTransaksi(0, 0, menuTerpilih, jumlah);
        parentController.tambahItemKeKeranjang(detail);

        // Tutup popup
        Stage stage = (Stage) btnTambahItemKeKeranjang.getScene().getWindow();
        stage.close();
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
