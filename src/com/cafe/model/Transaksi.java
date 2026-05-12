// Untuk menampung data pesanan

package com.cafe.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaksi {
    private int id_transaksi;
    private Date tanggal;
    private double totalHarga;
    private List<DetailTransaksi> items;

    public Transaksi(int id_transaksi) {
        this.id_transaksi = id_transaksi;
        this.tanggal = new Date();
        this.items = new ArrayList<>();
        this.totalHarga = 0.0;
    }

    public void tambahItem(DetailTransaksi detail) {
        this.items.add(detail);
        this.totalHarga += detail.getSubtotal();
    }

    public double getTotalHarga() { return totalHarga; }
    public List<DetailTransaksi> getItems() { return items; }
}