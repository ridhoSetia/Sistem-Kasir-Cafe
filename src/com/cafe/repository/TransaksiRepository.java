package com.cafe.repository;

import com.cafe.config.DBConnection;
import com.cafe.model.DetailTransaksi;
import com.cafe.model.Transaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiRepository {

public boolean simpanTransaksi(Transaksi transaksi) {
String sqlHeader = "INSERT INTO transaksi (id_user, tanggal, total_harga) VALUES (?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_menu, jumlah, subtotal) VALUES (?, ?, ?, ?)";
        String sqlUpdateStok = "UPDATE menu SET stok = stok - ? WHERE id_menu = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            conn.setAutoCommit(false); // Mengaktifkan Unit Transaksi ACID

            // 1. Eksekusi Penyimpanan Header Transaksi dengan membawa flags Generated Keys
            PreparedStatement stmtH = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            stmtH.setInt(1, transaksi.getIdUser());
            stmtH.setTimestamp(2, new Timestamp(transaksi.getTanggal().getTime()));
            stmtH.setDouble(3, transaksi.getTotalHarga()); // Mengambil kalkulasi dinamis model
            stmtH.executeUpdate();

            // AMBIL ID TERBARU DARI DATABASE MYSQL AUTO INCREMENT
            int idGenerated = -1;
            ResultSet keys = stmtH.getGeneratedKeys();
            if (keys.next()) {
                idGenerated = keys.getInt(1);
                transaksi.setIdTransaksi(idGenerated); // SUNTIKKAN ID ASLI DARI DB KE OBJEK MODEL
            }

            // 2. Eksekusi Batch Simpan Detail Rincian dan Batch Pengurangan Stok
            try (PreparedStatement stmtD = conn.prepareStatement(sqlDetail);
                 PreparedStatement stmtS = conn.prepareStatement(sqlUpdateStok)) {
                
                for (DetailTransaksi d : transaksi.getlistDetail()) {
                    // Ikat data detail ke ID Transaksi induk asli yang baru saja terbit
                    stmtD.setInt(1, idGenerated);
                    stmtD.setInt(2, d.getMenu().getIdMenu());
                    stmtD.setInt(3, d.getJumlah());
                    stmtD.setDouble(4, d.getSubtotal());
                    stmtD.addBatch();

                    // Pengurangan stok menu operasional cafe
                    stmtS.setInt(1, d.getJumlah()); 
                    stmtS.setInt(2, d.getMenu().getIdMenu()); 
                    stmtS.addBatch();
                }
                
                stmtD.executeBatch(); 
                stmtS.executeBatch(); 
            }

            conn.commit(); // Eksekusi berhasil total, simpan permanen ke MySQL disk
            return true;

        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] Rollback system aktif akibat error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // Fungsi getSemuaTransaksi() dan isiDetailTransaksi() tetap di bawah seperti semula...
    public List<Transaksi> getSemuaTransaksi() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Transaksi transaksi = new Transaksi(rs.getInt("id_user"));
                transaksi.setIdTransaksi(rs.getInt("id_transaksi"));
                isiDetailTransaksi(transaksi, conn);
                list.add(transaksi);
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] getSemuaTransaksi Error: " + e.getMessage());
        }
        return list;
    }

    private void isiDetailTransaksi(Transaksi transaksi, Connection conn) throws SQLException {
        String sql = "SELECT dt.id_detail, dt.id_transaksi, dt.jumlah, dt.subtotal, "
                + "m.id_menu, m.nama_menu, m.harga, m.kategori, m.stok "
                + "FROM detail_transaksi dt "
                + "JOIN menu m ON dt.id_menu = m.id_menu "
                + "WHERE dt.id_transaksi = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transaksi.getIdTransaksi());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                com.cafe.model.Menu menu = new com.cafe.model.Menu(
                        rs.getInt("id_menu"),   rs.getString("nama_menu"),
                        rs.getDouble("harga"),  rs.getString("kategori"),
                        rs.getInt("stok")
                );
                DetailTransaksi detail = new DetailTransaksi(rs.getInt("id_detail"), rs.getInt("id_transaksi"), menu, rs.getInt("jumlah"));
                transaksi.tambahItem(detail);            
            }
        }
    }
}