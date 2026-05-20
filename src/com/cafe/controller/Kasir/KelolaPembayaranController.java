package com.cafe.controller.Kasir;

import com.cafe.config.UserSession;
import com.cafe.model.DetailTransaksi;
import com.cafe.model.Transaksi;
import com.cafe.model.User;
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
    @FXML
    private TableView<DetailTransaksi> tbKelolaPembayaran;
    @FXML
    private TableColumn<DetailTransaksi, String> tbCNamaMenuKelolaPembayaran;
    @FXML
    private TableColumn<DetailTransaksi, Double> tbCHargaSatuanKelolaPembayaran;
    @FXML
    private TableColumn<DetailTransaksi, Integer> tbCJumlahKelolaPembayaran;
    @FXML
    private TableColumn<DetailTransaksi, Double> tbCSubtotalKelolaPembayaran;
    @FXML
    private Label lblTotalKelolaPembayaran;
    @FXML
    private Label lblKembalianKelolaPembayaran;
    @FXML
    private TextField txtNominalBayarKelolaPembayaran;
    @FXML
    private Button btnBayarKelolaPembayaran;
    @FXML
    private Button btnTambahKeranjang;

    private final ObservableList<DetailTransaksi> keranjang = FXCollections.observableArrayList();
    private final TransaksiRepository transaksiRepository = new TransaksiRepository();

    private int idKasirAktif;
    private final NumberFormat rupiahFmt = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @FXML
    public void initialize() {
        // Mengambil data user secara dinamis dari sesi aktif terpusat
        User user = UserSession.getInstance().getUserAktif();

        if (user != null) {
            this.idKasirAktif = user.getIdUser();
            System.out.println("Kasir yang sedang bertugas: " + user.getNamaLengkap() + " (ID: " + idKasirAktif + ")");
        } else {
            System.err.println("Peringatan: Sesi tidak ditemukan! Menggunakan default ID.");
            this.idKasirAktif = 1;
        }

        // Mapping Kolom Menggunakan Lambda Expression (Sangat Bagus & Kebal Bug
        // Refleksi)
        tbCNamaMenuKelolaPembayaran.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMenu().getNamaMenu()));
        tbCHargaSatuanKelolaPembayaran.setCellValueFactory(
                data -> new javafx.beans.property.SimpleObjectProperty<>(
                        data.getValue().getMenu().getHarga()));

        // Menggunakan PropertyValueFactory untuk mencocokkan method getJumlah() dan
        // getSubtotal()
        tbCJumlahKelolaPembayaran.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        tbCSubtotalKelolaPembayaran.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tbKelolaPembayaran.setItems(keranjang);

        // Format Tampilan Sel Tabel Menjadi Nilai Mata Uang Rupiah
        tbCHargaSatuanKelolaPembayaran.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });
        tbCSubtotalKelolaPembayaran.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : rupiahFmt.format(val));
            }
        });

        // Listener Dinamis: Setiap kali kasir mengetik angka baru, nominal kembalian
        // langsung dihitung otomatis
        txtNominalBayarKelolaPembayaran.textProperty().addListener((observable, oldValue, newValue) -> perbaruiLabel());

        perbaruiLabel();
    }

    public void setIdKasirAktif(int idKasir) {
        this.idKasirAktif = idKasir;
    }

    @FXML
    private void handleTambahKeranjang() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Kasir/TambahItemKeranjang.fxml"));
            Parent root = loader.load();

            // SINKRONISASI KONTROLER JENDELA ANAK (POPUP)
            TambahItemKeranjangController popupCtrl = loader.getController();
            popupCtrl.setParentController(this);

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

    // Menambahkan item menu atau mengakumulasi jumlahnya jika produk sejenis sudah
    // tertera di tabel
    public void tambahItemKeKeranjang(DetailTransaksi detail) {
        for (int i = 0; i < keranjang.size(); i++) {
            DetailTransaksi existing = keranjang.get(i);

            // Periksa apakah ID Menu yang dimasukkan sama dengan yang sudah ada di
            // keranjang
            if (existing.getMenu().getIdMenu() == detail.getMenu().getIdMenu()) {
                int jumlahBaru = existing.getJumlah() + detail.getJumlah();

                // SOLUSI UTAMA: Sesuaikan dengan konstruktor asli DetailTransaksi(idDetail,
                // menu, jumlah)
                // Hapus parameter idTransaksi yang kaku dan ilegal tersebut
                keranjang.set(i, new DetailTransaksi(existing.getIdDetail(), existing.getIdTransaksi(),
                        existing.getMenu(), jumlahBaru));

                perbaruiLabel();
                return; // Keluar dari fungsi setelah data berhasil di-update
            }
        }
        // Jika belum ada produk sejenis, tambahkan sebagai baris baru di keranjang
        // belanja
        keranjang.add(detail);
        perbaruiLabel();
    }

     // Menerima parameter objek Transaksi yang valid
    private void bukaPopupCetakNota(Transaksi transaksi) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Kasir/CetakNota.fxml"));
            Parent root = loader.load();
            
            // Mengirim objek transaksi terisi lengkap menuju CetakNotaController
            CetakNotaController notaCtrl = loader.getController();
            notaCtrl.setDataTransaksi(transaksi); 

            Stage stage = new Stage();
            stage.setTitle("Cetak Nota Pembayaran - Brew Society");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnBayarKelolaPembayaran.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Infrastruktur Error: Gagal memuat CetakNota.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBayar() {
        if (keranjang.isEmpty()) {
            tampilkanAlert(Alert.AlertType.WARNING, "Keranjang Kosong", "Tambahkan pesanan terlebih dahulu.");
            return;
        }
        
        String inputNominal = txtNominalBayarKelolaPembayaran.getText().trim();
        if (inputNominal.isEmpty()) {
            tampilkanAlert(Alert.AlertType.WARNING, "Nominal Kosong", "Masukkan nominal bayar terlebih dahulu.");
            return;
        }

        double nominalBayar;
        try {
            nominalBayar = Double.parseDouble(inputNominal);
        } catch (NumberFormatException e) {
            tampilkanAlert(Alert.AlertType.ERROR, "Format Salah", "Nominal bayar harus berupa angka murni.");
            return;
        }

        double total = hitungTotal();

        if (nominalBayar < total) {
            tampilkanAlert(Alert.AlertType.ERROR, "Uang Tidak Cukup", String.format("Kekurangan: %s", rupiahFmt.format(total - nominalBayar)));
            return;
        }

        double kembalian = nominalBayar - total;
        
        // Mempersiapkan cetakan objek Transaksi berdasarkan sesi Kasir Aktif
        Transaksi transaksi = new Transaksi(idKasirAktif);
        for (DetailTransaksi d : keranjang) {
            transaksi.tambahItem(d);
        }

        // Jalankan mutasi repositori ke database server MySQL Docker
        boolean berhasil = transaksiRepository.simpanTransaksi(transaksi);

        if (berhasil) {
            lblKembalianKelolaPembayaran.setText(rupiahFmt.format(kembalian));
            tampilkanAlert(Alert.AlertType.INFORMATION, "Pembayaran Berhasil", "Kembalian: " + rupiahFmt.format(kembalian));
            bukaPopupCetakNota(transaksi);
            resetSesi(); // Clear data belanja kassa setelah proses pencetakan struk selesai
        } else {
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal", "Transaksi gagal disimpan ke database server. Coba lagi.");
        }
    }

    private double hitungTotal() {
        double total = 0;
        for (DetailTransaksi d : keranjang) {
            total += d.getSubtotal();
        }
        return total;
    }

    private void perbaruiLabel() {
        double total = hitungTotal();
        lblTotalKelolaPembayaran.setText(rupiahFmt.format(total));

        String inputNominal = txtNominalBayarKelolaPembayaran.getText().trim();
        if (!inputNominal.isEmpty()) {
            try {
                double nominal = Double.parseDouble(inputNominal);
                double kembalian = nominal - total;
                lblKembalianKelolaPembayaran.setText(kembalian >= 0 ? rupiahFmt.format(kembalian) : "Uang tidak cukup");
            } catch (NumberFormatException ignored) {
            }
        } else {
            lblKembalianKelolaPembayaran.setText("Rp. -");
        }
    }

    private void resetSesi() {
        keranjang.clear();
        txtNominalBayarKelolaPembayaran.clear();
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