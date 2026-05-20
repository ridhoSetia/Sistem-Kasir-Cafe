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
    @FXML private ScrollPane scrollPane;
    @FXML private VBox       vboxKertasStruk;
    @FXML private Button     btnCetakNota;
    @FXML private Button     btnTutupCetakNota;

    private Transaksi transaksi;

    private final NumberFormat rupiahFmt =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private final SimpleDateFormat dateFmt =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public void setDataTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
        renderNota();
    }
    private void renderNota() {
        vboxKertasStruk.getChildren().clear();
        vboxKertasStruk.getChildren().addAll(
                buatTeks("================================"),
                buatTeks("         CAFE KITA             "),
                buatTeks("================================"),
                buatTeks("Tanggal : " + dateFmt.format(transaksi.getTanggal())),
                buatTeks("--------------------------------")
        );
        for (DetailTransaksi d : transaksi.getItems()) {
            vboxKertasStruk.getChildren().addAll(
                    buatTeks(d.getMenu().getNamaMenu() + "  x" + d.getJumlah()),
                    buatTeks(String.format("  @%s  = %s",
                            rupiahFmt.format(d.getMenu().getHarga()),
                            rupiahFmt.format(d.getSubtotal())))
            );
        }
        vboxKertasStruk.getChildren().addAll(
                buatTeks("--------------------------------"),
                buatTeks("Total     : " + rupiahFmt.format(transaksi.getTotalHarga())),
                buatTeks("================================"),
                buatTeks("      Terima Kasih! :)         "),
                buatTeks("================================")
        );
    }
    private Text buatTeks(String isi) {
        Text t = new Text(isi);
        t.getStyleClass().add("struk-text");
        return t;
    }

    @FXML
    private void handleCetakNota() {
        // PrinterJob: API bawaan JavaFX untuk mencetak Node ke printer
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            tampilkanAlert(Alert.AlertType.ERROR,
                    "Printer Tidak Tersedia",
                    "Tidak ada printer yang terdeteksi. Periksa koneksi printer.");
            return;
        }
        boolean konfirmasi = job.showPrintDialog(btnCetakNota.getScene().getWindow());

        if (konfirmasi) {
            // cetak node vboxKertasStruk (tampilan nota)
            Node nodeCetak = scrollPane.getContent();
            boolean berhasil = job.printPage(nodeCetak);

            if (berhasil) {
                job.endJob();
                tampilkanAlert(Alert.AlertType.INFORMATION,
                        "Berhasil", "Nota sedang dicetak.");
                tutupWindow();
            } else {
                tampilkanAlert(Alert.AlertType.ERROR,
                        "Gagal Cetak", "Proses cetak gagal. Coba lagi.");
            }
        }
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
