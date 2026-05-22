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
import java.util.stream.Collectors;

public class TambahItemKeranjangController {
    @FXML private TextField txtCariMenuTambahItem;
    @FXML private AnchorPane txtHasilPencarianTambahItem; 
    @FXML private AnchorPane txtPreviewMenuTambahItem; 
    @FXML private Label lblNamaMenuTambahItem;
    @FXML private Label lblHargaSatuanTambahItem;
    @FXML private Spinner<Integer> spJumlahTambahItem;
    @FXML private Button btnTambahItemKeKeranjang;

    private final MenuRepository menuRepository = new MenuRepository();
    private KelolaPembayaranController parentController;
    private Menu menuTerpilih = null;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        spJumlahTambahItem.setValueFactory(valueFactory);

        muatMenuTersedia();
    }

    public void setParentController(KelolaPembayaranController parent) {
        this.parentController = parent;
    }

    // Memuat data dan membuang menu yang stoknya habis (0)
    private void muatMenuTersedia() {
        List<Menu> menuTersedia = menuRepository.ambilSemua().stream()
                .filter(m -> m.getStok() > 0)
                .collect(Collectors.toList());
        tampilkanHasilPencarian(menuTersedia);
    }

    @FXML
    private void handleCariMenu() {
        String keyword = txtCariMenuTambahItem.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            muatMenuTersedia();
        } else {
            // SINTAKS KONTRAK & STREAM: Mencari ke database, lalu pastikan stoknya tersedia
            List<Menu> hasilCari = menuRepository.cari(keyword).stream()
                    .filter(m -> m.getStok() > 0)
                    .collect(Collectors.toList());
            tampilkanHasilPencarian(hasilCari);
        }
    }

    private void tampilkanHasilPencarian(List<Menu> hasilMenu) {
        VBox listContainer = new VBox(8);
        listContainer.setLayoutX(10);
        listContainer.setLayoutY(40);

        if (hasilMenu.isEmpty()) {
            Label lblKosong = new Label("Tidak ada menu ditemukan.");
            lblKosong.getStyleClass().add("label-muted");
            listContainer.getChildren().add(lblKosong);
        } else {
            for (Menu menu : hasilMenu) {
                Label lblMenu = new Label(menu.getNamaMenu() + "  (Stok: " + menu.getStok() + ")");
                lblMenu.getStyleClass().add("label-muted");
                lblMenu.setStyle("-fx-cursor: hand;");
                lblMenu.setOnMouseClicked(e -> pilihMenu(menu));
                listContainer.getChildren().add(lblMenu);
            }
        }

        txtHasilPencarianTambahItem.getChildren().clear();
        Label lblJudul = new Label("List Hasil Pencarian");
        lblJudul.getStyleClass().add("label-muted");
        lblJudul.setLayoutX(93);
        lblJudul.setLayoutY(14);
        txtHasilPencarianTambahItem.getChildren().addAll(lblJudul, listContainer);
    }

    private void pilihMenu(Menu menu) {
        this.menuTerpilih = menu;
        lblNamaMenuTambahItem.setText(menu.getNamaMenu());
        lblHargaSatuanTambahItem.setText(String.format("Rp %,.0f", menu.getHarga()));
        spJumlahTambahItem.getValueFactory().setValue(1);
    }

    private void bersihkanKeranjang() {
        lblNamaMenuTambahItem.setText("-");
        lblHargaSatuanTambahItem.setText("-");
        spJumlahTambahItem.getValueFactory().setValue(1);
        txtCariMenuTambahItem.clear();
        muatMenuTersedia();
    }

    @FXML
    private void handleTambahItemKeKeranjang() {
        if (menuTerpilih == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih menu dari daftar terlebih dahulu.");
            alert.showAndWait();
            return;
        }

        int jumlah = spJumlahTambahItem.getValue();

        if (menuTerpilih.getStok() < jumlah) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Stok " + menuTerpilih.getNamaMenu() + " hanya tersisa " + menuTerpilih.getStok() + ".");
            alert.showAndWait();
            return;
        }
        
        DetailTransaksi detail = new DetailTransaksi(0, 0, menuTerpilih, jumlah);
        parentController.tambahItemKeKeranjang(detail);
        bersihkanKeranjang();
    }

    @FXML
    private void handleTutupForm() {
        Stage stage = (Stage) btnTambahItemKeKeranjang.getScene().getWindow();
        stage.close();
    }
}