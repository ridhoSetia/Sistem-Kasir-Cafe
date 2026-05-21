package com.cafe.model;

public class DetailTransaksi {
    private int idDetail;
    private int idTransaksi; // Menunjuk ke ID Transaksi induk
    private Menu menu;       // Relasi Many-to-One langsung ke objek Menu
    private int jumlah;
    private double subtotal;

    public DetailTransaksi(int idDetail, int idTransaksi, Menu menu, int jumlah) {
        this.idDetail = idDetail;
        this.idTransaksi = idTransaksi;
        this.menu = menu;
        this.jumlah = jumlah;
        this.subtotal = menu.getHarga() * jumlah;
    }
    
    public int    getIdDetail()  { return idDetail; }
    public int    getIdTransaksi()  { return idTransaksi; }
    public double getSubtotal() { return subtotal; }
    public Menu getMenu() { return menu; }
    public int getJumlah() { return jumlah; }
}