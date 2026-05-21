package com.cafe.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class PindahHalaman {
    public static void pindah(Button btnTrigger, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(PindahHalaman.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Mendapatkan stage/window aktif saat ini melalui node button
            Stage stage = (Stage) btnTrigger.getScene().getWindow();
            Scene scene = new Scene(root);

            // Pasang stylesheet global Brew Society
            if (PindahHalaman.class.getResource("/resources/css/brew-society-pos.css") != null) {
                scene.getStylesheets().add(PindahHalaman.class.getResource("/resources/css/brew-society-pos.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
            
        } catch (IOException e) {
            System.err.println("[NavigationUtil] Gagal memuat halaman FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}