package com.cafe.controller.Kasir;

import com.cafe.model.DetailTransaksi;
import com.cafe.model.Transaksi;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CetakNotaController {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox vboxKertasStruk;
    @FXML
    private Button btnCetakNota;
    @FXML
    private Button btnTutupCetakNota;

    private Transaksi transaksi;

    // Menggunakan Locale.forLanguageTag untuk standardisasi Java 19+ (Bebas Deprecation Warning)
    private final NumberFormat rupiahFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public void setDataTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
        renderNota();
    }

    private void renderNota() {
        // Validasi defensif untuk mencegah NullPointerException
        if (transaksi == null) {
            System.err.println("Error: Objek Transaksi yang dilempar ke CetakNota bernilai NULL!");
            return;
        }

        vboxKertasStruk.getChildren().clear();
        vboxKertasStruk.getChildren().addAll(
                buatTeks("=================================="),
                buatTeks("        BREW SOCIETY CAFE        "),
                buatTeks("=================================="),
                buatTeks("Nota ID : #TRX-" + transaksi.getIdTransaksi()), // Sekarang menampilkan ID Riil DB
                buatTeks("Tanggal : " + dateFmt.format(transaksi.getTanggal())),
                buatTeks("----------------------------------"));

        // Mengiterasi list detail belanja yang tersimpan aman di memori objek
        for (DetailTransaksi d : transaksi.getlistDetail()) {
            vboxKertasStruk.getChildren().addAll(
                    buatTeks(d.getMenu().getNamaMenu() + "  x" + d.getJumlah()),
                    buatTeks(String.format("  @%s  = %s",
                            rupiahFmt.format(d.getMenu().getHarga()),
                            rupiahFmt.format(d.getSubtotal()))));
        }

        vboxKertasStruk.getChildren().addAll(
                buatTeks("----------------------------------"),
                buatTeks("Total Belanja : " + rupiahFmt.format(transaksi.getTotalHarga())),
                buatTeks("=================================="),
                buatTeks("     Terima Kasih atas Kunjungan Anda! :)    "),
                buatTeks("=================================="));
    }

    private Text buatTeks(String isi) {
        Text t = new Text(isi);
        t.getStyleClass().add("struk-text");
        return t;
    }

    @FXML
    private void handleCetakNota() {
        tampilkanAlert(Alert.AlertType.INFORMATION,
                "Berhasil", "Nota sedang dicetak.");
        tutupWindow();
    }

    @FXML
    private void handleTutupCetakNota() {
        tutupWindow();
    }

    private void tutupWindow() {
        Stage stage = (Stage) btnTutupCetakNota.getScene().getWindow();
        stage.close();
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}