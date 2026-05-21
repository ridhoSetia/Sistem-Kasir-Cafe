package com.cafe.model;
import javafx.scene.control.Button;

public class Manager extends User {
    public Manager(int idUser, String username, String password, String namaLengkap) {
        super(idUser, username, password, "Manager", namaLengkap);
    }

    @Override
    public void konfigurasiHakAkses(Button btnKelolaKasir, Button btnKelolaMenu) {
        btnKelolaKasir.setVisible(true);
        btnKelolaKasir.setManaged(true);
        btnKelolaMenu.setVisible(true);
        btnKelolaMenu.setManaged(true);
    }
}