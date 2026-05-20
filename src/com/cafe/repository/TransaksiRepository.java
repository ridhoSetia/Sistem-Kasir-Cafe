// Menangani penyimpanan transaksi baru ke database.

package com.cafe.repository;
import com.cafe.config.DBConnection;
import com.cafe.model.DetailTransaksi;
import com.cafe.model.Menu;
import com.cafe.model.Transaksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiRepository {
    public boolean simpan(Transaksi transaksi, int idKasir) {
        String sqlHeader = "INSERT INTO transaksi (id_kasir, tanggal, total_harga) VALUES (?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_menu, jumlah, subtotal) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // mulai transaction

            // 1. Simpan header
            PreparedStatement stmtH = conn.prepareStatement(
                    sqlHeader, Statement.RETURN_GENERATED_KEYS);
            stmtH.setInt(1, idKasir);
            stmtH.setTimestamp(2, new Timestamp(transaksi.getTanggal().getTime()));
            stmtH.setDouble(3, transaksi.getTotalHarga());
            stmtH.executeUpdate();

            // Ambil ID yang digenerate DB
            int idBaru = -1;
            ResultSet keys = stmtH.getGeneratedKeys();
            if (keys.next()) idBaru = keys.getInt(1);

            // 2. Simpan detail (batch insert)
            PreparedStatement stmtD = conn.prepareStatement(sqlDetail);
            for (DetailTransaksi d : transaksi.getItems()) {
                stmtD.setInt(1, idBaru);
                stmtD.setInt(2, d.getMenu().getIdMenu());
                stmtD.setInt(3, d.getJumlah());
                stmtD.setDouble(4, d.getSubtotal());
                stmtD.addBatch();
            }
            stmtD.executeBatch();

            conn.commit(); // semua sukses
            System.out.println("[TransaksiRepository] Transaksi disimpan. ID: " + idBaru);
            return true;

        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] Gagal simpan: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;

        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /**
     * Mengambil semua transaksi beserta items-nya dari database.
     * Dipakai oleh LihatRiwayatController untuk mengisi tabel riwayat.
     */
    public List<Transaksi> getSemuaTransaksi() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Transaksi t = new Transaksi(rs.getInt("id_transaksi"));
                isiItems(t, conn);
                list.add(t);
            }

        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] getSemuaTransaksi: " + e.getMessage());
        }
        return list;
    }
    private void isiItems(Transaksi transaksi, Connection conn) throws SQLException {
        String sql = "SELECT dt.id_detail, dt.jumlah, "
                + "m.id_menu, m.nama_menu, m.harga, m.kategori, m.stok "
                + "FROM detail_transaksi dt "
                + "JOIN menu m ON dt.id_menu = m.id_menu "
                + "WHERE dt.id_transaksi = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaksi.getIdTransaksi());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Menu menu = new Menu(
                        rs.getInt("id_menu"),   rs.getString("nama_menu"),
                        rs.getDouble("harga"),  rs.getString("kategori"),
                        rs.getInt("stok")
                );
                transaksi.tambahItem(
                        new DetailTransaksi(rs.getInt("id_detail"), menu, rs.getInt("jumlah")));
            }
        }
    }
}