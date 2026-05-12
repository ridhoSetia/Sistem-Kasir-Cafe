// Untuk mengelola data item cafe.

package com.cafe.model;

public class Menu {
    private int id_menu;
    private String nama_menu;
    private double harga;
    private String kategori;
    private int stok;

    public Menu(int id_menu, String nama_menu, double harga, String kategori, int stok) {
        this.id_menu = id_menu;
        this.nama_menu = nama_menu;
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
    public int getIdMenu() { return id_menu; }
    public String getNama_menu() { return nama_menu; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
}