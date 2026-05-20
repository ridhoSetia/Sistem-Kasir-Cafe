package com.cafe.controller.Kasir;
import com.cafe.model.DetailTransaksi;
import com.cafe.model.Transaksi;
import com.cafe.repository.TransaksiRepository;
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

public class KelolaPembayaranController {
    @FXML private TableView<DetailTransaksi>tbKelolaPembayaran;
    @FXML private TableColumn<DetailTransaksi, String> tbCNamaMenuKelolaPembayaran;
    @FXML private TableColumn<DetailTransaksi, Double> tbCHargaSatuanKelolaPembayaran;
    @FXML private TableColumn<DetailTransaksi, Integer>tbCJumlahKelolaPembayaran;
    @FXML private TableColumn<DetailTransaksi, Double> tbCSubtotalKelolaPembayaran;
    @FXML private Label lblTotalKelolaPembayaran;
    @FXML private Label lblKembalianKelolaPembayaran;
    @FXML private TextField txtNominalBayarKelolaPembayaran;
    @FXML private CheckBox cbCetakNota;
    @FXML private Button btnBayarKelolaPembayaran;
    @FXML private Button btnTambahKeranjang;

    private final ObservableList<DetailTransaksi> keranjang =
            FXCollections.observableArrayList();
    private final TransaksiRepository transaksiRepository = new TransaksiRepository();
    // ID kasir yang sedang login, diisi oleh MainMenuController
    private int idKasirAktif = -1;
    // Formatter rupiah
    private final NumberFormat rupiahFmt =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    @FXML
    public void initialize() {
        tbCNamaMenuKelolaPembayaran.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMenu().getNamaMenu()));
        tbCHargaSatuanKelolaPembayaran.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getMenu().getHarga()));
        tbCJumlahKelolaPembayaran.setCellValueFactory(
                new PropertyValueFactory<>("jumlah"));
        tbCSubtotalKelolaPembayaran.setCellValueFactory(
                new PropertyValueFactory<>("subtotal"));
        tbKelolaPembayaran.setItems(keranjang);

        // Format kolom harga jadi Rupiah
        tbCHargaSatuanKelolaPembayaran.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });
        tbCSubtotalKelolaPembayaran.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });
        perbaruiLabel();
    }
    public void setIdKasirAktif(int idKasir) {
        this.idKasirAktif = idKasir;
    }

    @FXML
    private void handleTambahKeranjang() {
        System.out.println("tambah keranjang");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/resources/Kasir/TambahItemKeranjang.fxml"));
            Parent root = loader.load();

            // popup bisa memanggil tambahItemKeKeranjang()
            TambahItemKeranjangController popupCtrl = loader.getController();
            popupCtrl.setParentController(this);
            // Buka sebagai modal window (harus ditutup dulu sebelum lanjut)
            Stage stage = new Stage();
            stage.setTitle("Tambah Item ke Keranjang");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnTambahKeranjang.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Gagal membuka TambahItemKeranjang: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void tambahItemKeKeranjang(DetailTransaksi detail) {
        // Cek apakah menu yang sama sudah ada di keranjang
        for (int i = 0; i < keranjang.size(); i++) {
            DetailTransaksi existing = keranjang.get(i);
            if (existing.getMenu().getIdMenu() == detail.getMenu().getIdMenu()) {
                int jumlahBaru = existing.getJumlah() + detail.getJumlah();
                keranjang.set(i, new DetailTransaksi(
                        existing.getIdDetail(), existing.getMenu(), jumlahBaru));
                perbaruiLabel();
                return;
            }
        }
        keranjang.add(detail);
        perbaruiLabel();
    }

    @FXML
    private void handleCetakNota() {
        System.out.println("cetak nota");
        if (cbCetakNota.isSelected()) {
            // harus ada pesanan sebelum cetak nota
            if (keranjang.isEmpty()) {
                tampilkanAlert(Alert.AlertType.WARNING,
                        "Keranjang Kosong",
                        "Tambahkan pesanan terlebih dahulu sebelum mencetak nota.");
                cbCetakNota.setSelected(false); // kembalikan ke unchecked
                return;
            }
            bukaPopupCetakNota();
        }
    }
    private void bukaPopupCetakNota() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/resources/Kasir/CetakNota.fxml"));
            Parent root = loader.load();
            // Buat Transaksi sementara dari keranjang untuk ditampilkan di nota
            Transaksi transaksiPreview = new Transaksi(0);
            for (DetailTransaksi d : keranjang) {
                transaksiPreview.tambahItem(d);
            }
            CetakNotaController notaCtrl = loader.getController();
            notaCtrl.setDataTransaksi(transaksiPreview);

            Stage stage = new Stage();
            stage.setTitle("Cetak Nota");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnBayarKelolaPembayaran.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            // setelah popup ditutup, reset checkbox
            cbCetakNota.setSelected(false);

        } catch (IOException e) {
            System.err.println("Gagal membuka CetakNota: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBayar() {
        System.out.println("bayar");
        // validasi keranjang tidak boleh kosong
        if (keranjang.isEmpty()) {
            tampilkanAlert(Alert.AlertType.WARNING,
                    "Keranjang Kosong", "Tambahkan pesanan terlebih dahulu.");
            return;
        }
        // validasi nominal bayar tidak boleh kosong
        String inputNominal = txtNominalBayarKelolaPembayaran.getText().trim();
        if (inputNominal.isEmpty()) {
            tampilkanAlert(Alert.AlertType.WARNING,
                    "Nominal Kosong", "Masukkan nominal bayar terlebih dahulu.");
            return;
        }

        // validasi nominal harus berupa angka
        double nominalBayar;
        try {
            nominalBayar = Double.parseDouble(inputNominal);
        } catch (NumberFormatException e) {
            tampilkanAlert(Alert.AlertType.ERROR,
                    "Format Salah", "Nominal bayar harus berupa angka.");
            return;
        }

        // Hitung total dari keranjang
        double total = hitungTotal();

        // validasi uang tidak cukup
        if (nominalBayar < total) {
            tampilkanAlert(Alert.AlertType.ERROR,
                    "Uang Tidak Cukup",
                    String.format("Kekurangan: %s", rupiahFmt.format(total - nominalBayar)));
            return;
        }

        // semua valid → proses pembayaran
        double kembalian = nominalBayar - total;
        Transaksi transaksi = new Transaksi(0); // id = 0, DB yang generate
        for (DetailTransaksi d : keranjang) {
            transaksi.tambahItem(d);
        }

        boolean berhasil = transaksiRepository.simpan(transaksi, idKasirAktif);

        if (berhasil) {
            lblKembalianKelolaPembayaran.setText(rupiahFmt.format(kembalian));

            if (cbCetakNota.isSelected()) {
                bukaPopupCetakNota();
            }
            tampilkanAlert(Alert.AlertType.INFORMATION,
                    "Pembayaran Berhasil",
                    "Kembalian: " + rupiahFmt.format(kembalian));
            resetSesi();
        } else {
            tampilkanAlert(Alert.AlertType.ERROR,
                    "Gagal", "Transaksi gagal disimpan. Coba lagi.");
        }
    }
    private double hitungTotal() {
        double total = 0;
        for (DetailTransaksi d : keranjang) {
            total += d.getSubtotal();
        }
        return total;
    }

    //perbarui label Total dan Kembalian setiap kali keranjang berubah
    private void perbaruiLabel() {
        double total = hitungTotal();
        lblTotalKelolaPembayaran.setText(rupiahFmt.format(total));

        String inputNominal = txtNominalBayarKelolaPembayaran.getText().trim();
        if (!inputNominal.isEmpty()) {
            try {
                double nominal = Double.parseDouble(inputNominal);
                double kembalian = nominal - total;
                lblKembalianKelolaPembayaran.setText(
                        kembalian >= 0 ? rupiahFmt.format(kembalian) : "Uang tidak cukup");
            } catch (NumberFormatException ignored) {}
        } else {
            lblKembalianKelolaPembayaran.setText("Rp. -");
        }
    }

    //reset semua state setelah transaksi selesai
    private void resetSesi() {
        keranjang.clear();
        txtNominalBayarKelolaPembayaran.clear();
        cbCetakNota.setSelected(false);
        perbaruiLabel();
    }
    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
