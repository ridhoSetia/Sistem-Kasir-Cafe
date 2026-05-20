package com.cafe.model;

public class Kasir extends User {
    public Kasir(int idUser, String username, String password, String namaLengkap) {
        super(idUser, username, password, "Kasir", namaLengkap);
    }
}