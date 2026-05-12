package com.cafe.model;

public class Manager extends User {
    public Manager(int idUser, String username, String password) {
        super(idUser, username, password, "Manager");
    }
}