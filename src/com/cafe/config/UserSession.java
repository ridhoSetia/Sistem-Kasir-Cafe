package com.cafe.config;

import com.cafe.model.User;

public class UserSession {
    private static UserSession instance;
    private User userAktif;

    private UserSession() {} // Mencegah instansiasi langsung dari luar

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(User user) {
        this.userAktif = user;
    }

    public void logout() {
        this.userAktif = null;
    }

    public User getUserAktif() {
        return userAktif;
    }
}