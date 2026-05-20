// Untuk menampung data pesanan

package com.cafe.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaksi {
    private int idTransaksi;
    private Date tanggal;
    private double totalHarga;
    private List<DetailTransaksi> items;

    public Transaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
        this.tanggal = new Date();
        this.items = new ArrayList<>();
        this.totalHarga = 0.0;
    }

    public void tambahItem(DetailTransaksi detail) {
        this.items.add(detail);
    }

    public double getTotalHarga() {
        double total = 0.0;
        for (DetailTransaksi item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public List<DetailTransaksi> getItems() {
        return items;
    }

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public Date getTanggal() {
        return tanggal;
    }
}