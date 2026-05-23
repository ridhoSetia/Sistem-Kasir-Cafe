package com.cafe.model;
import javafx.scene.control.Button;

public class Kasir extends User {
    public Kasir(int idUser, String username, String password, String namaLengkap) {
        super(idUser, username, password, "Kasir", namaLengkap);
    }

    @Override
    public void konfigurasiHakAkses(Button btnKelolaKasir, Button btnKelolaMenu, Button btnMenuKasir) {
        btnKelolaKasir.setVisible(false);
        btnKelolaKasir.setManaged(false);
        btnKelolaMenu.setVisible(false);
        btnKelolaMenu.setManaged(false);
        btnMenuKasir.setVisible(true);
        btnMenuKasir.setManaged(true);
    }
}