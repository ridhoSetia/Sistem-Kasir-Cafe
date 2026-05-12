// Digunakan untuk autentikasi login.

package com.cafe.model;

public class User {
    private int id_user;
    private String username;
    private String password;
    private String role;

    public User(int id_user, String username, String password, String role) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getIdUser() {
        return id_user;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}