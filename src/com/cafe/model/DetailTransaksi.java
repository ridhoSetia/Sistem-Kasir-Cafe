package com.cafe.model;

public class DetailTransaksi {
    private int id_detail;
    private Menu menu;
    private int jumlah;
    private double subtotal;

    public DetailTransaksi(int id_detail, Menu menu, int jumlah) {
        this.id_detail = id_detail;
        this.menu = menu;
        this.jumlah = jumlah;
        this.subtotal = menu.getHarga() * jumlah;
    }

    public double getSubtotal() { return subtotal; }
    public Menu getMenu() { return menu; }
    public int getJumlah() { return jumlah; }
}