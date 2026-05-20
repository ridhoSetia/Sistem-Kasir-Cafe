package com.cafe.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaksi {
    private int idTransaksi;
    private int idUser;
    private Date tanggal;
    private List<DetailTransaksi> listDetail; // Bersihkan nama variabel agar seragam

    // Konstruktor utama kassa kasir aktif
    public Transaksi(int idUser) {
        this.idTransaksi = 0;
        this.idUser = idUser;
        this.tanggal = new Date();
        this.listDetail = new ArrayList<>();
    }

    public void tambahItem(DetailTransaksi detail) {
        this.listDetail.add(detail);
    }

    // Menggunakan objek listDetail yang seragam 
    // untuk mencegah kembalian nilai Rp0,00 di RAM
    public double getTotalHarga() {
        double total = 0.0;
        for (DetailTransaksi item : listDetail) {
            total += item.getSubtotal();
        }
        return total;
    }

    public List<DetailTransaksi> getlistDetail() {
        return listDetail;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public int getIdUser() {
        return idUser;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}