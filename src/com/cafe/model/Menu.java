// Untuk mengelola data item cafe.

package com.cafe.model;

public class Menu {
    private int idMenu;
    private String namaMenu;
    private double harga;
    private String kategori;
    private int stok;

    public Menu(int idMenu, String namaMenu, double harga, String kategori, int stok) {
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.kategori = kategori;
        this.stok = stok;
    }

    // Method untuk mengecek ketersediaan sebelum transaksi
    public boolean kurangiStok(int jumlah) {
        if (this.stok >= jumlah) {
            this.stok -= jumlah;
            return true;
        }
        return false;
    }

    // Getter & Setter
    public int getIdMenu() { return idMenu; }
    public String getNamaMenu() { return namaMenu; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
}