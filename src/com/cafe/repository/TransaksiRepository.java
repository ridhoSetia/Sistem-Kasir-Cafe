package com.cafe.repository;

import com.cafe.config.DBConnection;
import com.cafe.model.DetailTransaksi;
import com.cafe.model.Menu;
import com.cafe.model.Transaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// sinkronisasi mengikat TransaksiRepository ke IRepository dengan tipe Transaksi
public class TransaksiRepository implements IRepository<Transaksi, Integer> {

    @Override
    public boolean simpan(Transaksi transaksi) {
        String sqlHeader = "INSERT INTO transaksi (id_user, tanggal, total_harga) VALUES (?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_menu, jumlah, subtotal) VALUES (?, ?, ?, ?)";
        String sqlUpdateStok = "UPDATE menu SET stok = stok - ? WHERE id_menu = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            PreparedStatement stmtH = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            stmtH.setInt(1, transaksi.getIdUser());
            stmtH.setTimestamp(2, new java.sql.Timestamp(transaksi.getTanggal().getTime()));
            stmtH.setDouble(3, transaksi.getTotalHarga());
            stmtH.executeUpdate();

            int idGenerated = -1;
            ResultSet keys = stmtH.getGeneratedKeys();
            if (keys.next()) {
                idGenerated = keys.getInt(1);
                transaksi.setIdTransaksi(idGenerated);
            }

            try (PreparedStatement stmtD = conn.prepareStatement(sqlDetail);
                    PreparedStatement stmtS = conn.prepareStatement(sqlUpdateStok)) {

                for (DetailTransaksi d : transaksi.getlistDetail()) {
                    stmtD.setInt(1, idGenerated);
                    stmtD.setInt(2, d.getMenu().getIdMenu());
                    stmtD.setInt(3, d.getJumlah());
                    stmtD.setDouble(4, d.getSubtotal());
                    stmtD.addBatch();

                    stmtS.setInt(1, d.getJumlah());
                    stmtS.setInt(2, d.getMenu().getIdMenu());
                    stmtS.addBatch();
                }
                stmtD.executeBatch();
                stmtS.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] Rollback sistem aktif: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Transaksi> ambilSemua() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.id_transaksi, t.id_user, t.tanggal, t.total_harga, u.nama_lengkap "
                + "FROM transaksi t "
                + "INNER JOIN users u ON t.id_user = u.id_user "
                + "ORDER BY t.tanggal DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int idTransaksi = resultSet.getInt("id_transaksi");
                int idUser = resultSet.getInt("id_user");
                String namaLengkapKasir = resultSet.getString("nama_lengkap");

                Transaksi transaksi = new Transaksi(idUser);
                transaksi.setIdTransaksi(idTransaksi);
                transaksi.setNamaKasir(namaLengkapKasir);

                java.sql.Timestamp databaseTimestamp = resultSet.getTimestamp("tanggal");
                if (databaseTimestamp != null) {
                    transaksi.setTanggal(new java.util.Date(databaseTimestamp.getTime()));
                }

                isiDetailTransaksi(transaksi, conn);
                list.add(transaksi);
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] Error ambilSemua: " + e.getMessage());
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
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Menu menu = new Menu(
                            resultSet.getInt("id_menu"), resultSet.getString("nama_menu"),
                            resultSet.getDouble("harga"), resultSet.getString("kategori"),
                            resultSet.getInt("stok"));
                    DetailTransaksi detail = new DetailTransaksi(resultSet.getInt("id_detail"), resultSet.getInt("id_transaksi"),
                            menu, resultSet.getInt("jumlah"));
                    transaksi.tambahItem(detail);
                }
            }
        }
    }

    @Override
    public boolean perbarui(Transaksi entitas) {
        throw new UnsupportedOperationException("Keamanan Bisnis: Data transaksi historis dilarang diedit!");
    }

    @Override
    public boolean hapus(Integer id) {
        throw new UnsupportedOperationException("Keamanan Bisnis: Data transaksi historis dilarang dihapus!");
    }

    @Override
    public List<Transaksi> cari(String keyword) {
        List<Transaksi> list = new ArrayList<>();

        // Kita mencari berdasarkan ID Transaksi atau Nama Kasir yang menangani
        String sql = "SELECT t.id_transaksi, t.id_user, t.tanggal, t.total_harga, u.nama_lengkap "
                + "FROM transaksi t "
                + "INNER JOIN users u ON t.id_user = u.id_user "
                + "WHERE t.id_transaksi = ? OR u.nama_lengkap LIKE ? "
                + "ORDER BY t.tanggal DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Uji apakah keyword berupa angka (untuk pencarian ID Transaksi)
            try {
                int idCari = Integer.parseInt(keyword);
                stmt.setInt(1, idCari);
            } catch (NumberFormatException e) {
                stmt.setInt(1, -1); // Jika bukan angka, beri nilai -1 agar tidak ada ID yang cocok
            }

            // Parameter kedua untuk pencarian nama kasir
            stmt.setString(2, "%" + keyword + "%");

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Transaksi transaksi = new Transaksi(resultSet.getInt("id_user"));
                    transaksi.setIdTransaksi(resultSet.getInt("id_transaksi"));
                    transaksi.setNamaKasir(resultSet.getString("nama_lengkap"));

                    java.sql.Timestamp databaseTimestamp = resultSet.getTimestamp("tanggal");
                    if (databaseTimestamp != null) {
                        transaksi.setTanggal(new java.util.Date(databaseTimestamp.getTime()));
                    }

                    isiDetailTransaksi(transaksi, conn);
                    list.add(transaksi);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiRepository] Error cari: " + e.getMessage());
        }
        return list;
    }
}