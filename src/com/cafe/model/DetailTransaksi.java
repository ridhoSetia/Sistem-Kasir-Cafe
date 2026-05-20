package com.cafe.model;

public class DetailTransaksi {
    private int idDetail;
    private Menu menu;
    private int jumlah;
    private double subtotal;

    public DetailTransaksi(int idDetail, Menu menu, int jumlah) {
        this.idDetail = idDetail;
        this.menu = menu;
        this.jumlah = jumlah;
        this.subtotal = menu.getHarga() * jumlah;
    }
    public int    getIdDetail()  { return idDetail; }
    public double getSubtotal() { return subtotal; }
    public Menu getMenu() { return menu; }
    public int getJumlah() { return jumlah; }
}