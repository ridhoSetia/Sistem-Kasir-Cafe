package com.cafe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.SQLException;

import com.cafe.model.Hewan;
import com.cafe.repository.HewanRepository; // Ini akan mengarah ke file asli kamu

public class HewanController {
    @FXML private TextField inputNama;
    @FXML private TextField inputUmur;
    @FXML private Label labelDisplay;
    
    // Sekarang ini akan menggunakan class dari package com.cafe.repository
    private final HewanRepository repository = new HewanRepository();

    @FXML
    private void onBtnClick() {
        try {
            String nama = inputNama.getText();
            int umur = Integer.parseInt(inputUmur.getText());

            Hewan hewanBaru = new Hewan(nama, umur);

            // Memanggil method save yang asli (yang sudah ada JDBC-nya)
            repository.save(hewanBaru);

            labelDisplay.setText("Tersimpan ke Database: " + hewanBaru.getNama());
            
            inputNama.clear();
            inputUmur.clear();
            
        } catch (NumberFormatException e) {
            labelDisplay.setText("Error: Umur harus angka!");
        } catch (SQLException e) {
            labelDisplay.setText("Error Database: " + e.getMessage());
        }
    }
}
