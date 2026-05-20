package com.cafe.controller.Kasir;

import com.cafe.model.DetailTransaksi;
import com.cafe.model.Transaksi;
import com.cafe.repository.TransaksiRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LihatRiwayatController {
    @FXML
    private TextField txtCariIDRiwayatTransaksi;
    @FXML
    private DatePicker dtTanggalRiwayatTransaksi;
    @FXML
    private Button btnCariDataRiwayatTransaksi;
    @FXML
    private TableView<Transaksi> tbDetailRiwayatTransaksi;
    @FXML
    private TableColumn<Transaksi, Integer> tbCIDRiwayatTransaksi;
    @FXML
    private TableColumn<Transaksi, String> tbCWaktuRiwayatTransaksi;
    @FXML
    private TableColumn<Transaksi, Double> tbCTotalPembayaranRiwayatTransaksi;
    @FXML
    private Label lblDetailTransaksiRiwayatTransaksi;
    @FXML
    private Label lblInformasiWaktuRiwayatTransaksi;
    @FXML
    private Label lblTotalAkhirRiwayatTransaksi;
    @FXML
    private TableView<DetailTransaksi> tbTransaksiMenuRiwayatTransaksi;
    @FXML
    private TableColumn<DetailTransaksi, String> tbCNamaMenuRiwayatTransaksi;
    @FXML
    private TableColumn<DetailTransaksi, Integer> tbCQTYRiwayatTransaksi;
    @FXML
    private TableColumn<DetailTransaksi, Double> tbCSubTotalRiwayatTransaksi;
    @FXML
    private Button btnTutupRincianRiwayatTransaksi;

    // Variabel penampung VBox utama dari panel rincian bawah
    @FXML
    private VBox panelDetailBox;

    private final TransaksiRepository transaksiRepository = new TransaksiRepository();
    private List<Transaksi> semuaTransaksi;

    private final NumberFormat rupiahFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupKolomTabelAtas();
        setupKolomTabelBawah();
        sembunyikanPanelDetail(); // Sembunyikan secara total saat awal
        muatSemuaRiwayat();

        // Jembatan Event: saat baris di tabel atas diklik → tampilkan detail secara
        // dinamis
        tbDetailRiwayatTransaksi.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        tampilkanDetailTransaksi(newVal);
                    }
                });
    }

    private void setupKolomTabelAtas() {
        tbCIDRiwayatTransaksi.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));

        // Format kolom tanggal dari objek Date menjadi String terbaca
        tbCWaktuRiwayatTransaksi.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        dateFmt.format(data.getValue().getTanggal())));

        tbCTotalPembayaranRiwayatTransaksi.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));

        // Format kolom total: double → Rupiah
        tbCTotalPembayaranRiwayatTransaksi.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });
    }

    private void setupKolomTabelBawah() {
        tbCNamaMenuRiwayatTransaksi.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMenu().getNamaMenu()));

        tbCQTYRiwayatTransaksi.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        tbCSubTotalRiwayatTransaksi.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Format subtotal menjadi Rupiah
        tbCSubTotalRiwayatTransaksi.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });
    }

    private void muatSemuaRiwayat() {
        semuaTransaksi = transaksiRepository.getSemuaTransaksi();
        tbDetailRiwayatTransaksi.setItems(FXCollections.observableArrayList(semuaTransaksi));
    }

    @FXML
    private void handleCariData() {
        String inputID = txtCariIDRiwayatTransaksi.getText().trim();
        LocalDate tanggal = dtTanggalRiwayatTransaksi.getValue();

        List<Transaksi> hasil = semuaTransaksi;

        // Filter dinamis berdasarkan ID Transaksi
        if (!inputID.isEmpty()) {
            try {
                int idCari = Integer.parseInt(inputID);
                hasil = hasil.stream()
                        .filter(t -> t.getIdTransaksi() == idCari)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                tampilkanAlert(Alert.AlertType.WARNING, "Format Salah", "ID Transaksi harus berupa angka murni.");
                return;
            }
        }

        // Filter dinamis berdasarkan kalender DatePicker
        if (tanggal != null) {
            SimpleDateFormat hariSaja = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalStr = tanggal.toString(); // Output format: yyyy-MM-dd
            hasil = hasil.stream()
                    .filter(t -> hariSaja.format(t.getTanggal()).equals(tanggalStr))
                    .collect(Collectors.toList());
        }

        tbDetailRiwayatTransaksi.setItems(FXCollections.observableArrayList(hasil));
        sembunyikanPanelDetail();
    }

    @FXML
    private void handleCariID() {
        handleCariData();
    }

    @FXML
    private void handleTanggalTransaksi() {
        handleCariData();
    }

    private void tampilkanDetailTransaksi(Transaksi transaksi) {
        lblDetailTransaksiRiwayatTransaksi.setText("Detail Transaksi #" + transaksi.getIdTransaksi());
        lblInformasiWaktuRiwayatTransaksi.setText("Waktu: " + dateFmt.format(transaksi.getTanggal()));
        lblTotalAkhirRiwayatTransaksi.setText("Total Akhir : " + rupiahFmt.format(transaksi.getTotalHarga()));

        // PERBAIKAN FATAL: Mengubah dari getlistDetail() menjadi getItems() sesuai
        // model asli Transaksi.java
        ObservableList<DetailTransaksi> detailItems = FXCollections.observableArrayList(transaksi.getlistDetail());
        tbTransaksiMenuRiwayatTransaksi.setItems(detailItems);

        tampilkanPanelDetail(); // Membuka panel detail ke layar
    }

    @FXML
    private void handleTutupRincianRiwayatTransaksi() {
        sembunyikanPanelDetail();
        tbDetailRiwayatTransaksi.getSelectionModel().clearSelection();
    }

    private void tampilkanPanelDetail() {
        // Memaksa kontainer layout VBox bawah muncul dan memakan porsi ruang di scene
        // JavaFX
        panelDetailBox.setVisible(true);
        panelDetailBox.setManaged(true);
    }

    private void sembunyikanPanelDetail() {
        // Melakukan penutupan total struktural layout agar halaman terlihat bersih
        // bersahaja
        panelDetailBox.setVisible(false);
        panelDetailBox.setManaged(false);

        lblDetailTransaksiRiwayatTransaksi.setText("Detail Transaksi #ID...");
        lblInformasiWaktuRiwayatTransaksi.setText("Informasi waktu transaksi dilakukan.");
        lblTotalAkhirRiwayatTransaksi.setText("Total Akhir : Rp 0");
        tbTransaksiMenuRiwayatTransaksi.setItems(FXCollections.observableArrayList());
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}