// Digunakan untuk autentikasi login.

package com.cafe.model;

public class User {
    private int idUser;
    private String namaLengkap;
    private String username;
    private String password;
    private String role;

    public User(int idUser, String username, String password, String role, String namaLengkap) {
        this.idUser = idUser;
        this.namaLengkap = namaLengkap;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // JavaFX TableView bakal mencari method ini otomatis (Reflection)
    public int getIdUser() { return idUser; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; } // Ini akan mengisi kolom role
}