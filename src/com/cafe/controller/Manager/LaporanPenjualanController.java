package com.cafe.controller.Manager;

import com.cafe.repository.TransaksiRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LaporanPenjualanController {

    // Komponen Label penampil angka di halaman FXML Laporan kalian
    @FXML private Label txtTotalPendapatan;
    @FXML private Label txtTotalTransaksi;

    private final TransaksiRepository transaksiRepository = new TransaksiRepository();

    /**
     * Method ini otomatis berjalan saat halaman Laporan Penjualan dibuka oleh Manager
     */
    @FXML
    public void initialize() {
        loadDataLaporan();
    }

    /**
     * Mengambil kalkulasi angka dari database dan menampilkannya ke UI JavaFX
     */
    public void loadDataLaporan() {
        // 1. Ambil data hitungan dari database melalui repository
        double omset = transaksiRepository.getTotalPendapatan();
        int jumlahTransaksi = transaksiRepository.getTotalTransaksiCount();

        // 2. Format teks angka rupiah dan tampilkan ke komponen Label FXML
        if (txtTotalPendapatan != null) {
            txtTotalPendapatan.setText("Rp " + String.format("%,.2f", omset));
        }

        if (txtTotalTransaksi != null) {
            txtTotalTransaksi.setText(jumlahTransaksi + " Transaksi");
        }
    }
}