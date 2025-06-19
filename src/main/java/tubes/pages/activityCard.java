// File: activityCard.java (FINAL - Sudah Diperbaiki)
package tubes.pages;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tubes.backend.Activity;
import tubes.launch.mainApp;

public class activityCard {

    private Activity activity;
    private mainApp app;

    private Rectangle kotakAktivitas;
    private Label judulAktvLbl;
    private Label waktuAktvLbl;
    private Label lokasiStaticLbl;
    private Label namaLokasiAktvLbl;
    private Label deskripsiStaticLbl;
    private Label isiDeskripsiAktvLbl;
    private Button editBtn;
    private Button hapusBtn;

    private VBox judulWaktuVBox;
    private VBox lokasiVBox;
    private VBox deskripsiVBox;
    private VBox isiAKtivitasVBox;
    private VBox editHapusVBox;
    private BorderPane aktivitasCardLayout;
    private StackPane aktivitasPane;

    public activityCard(mainApp app, Activity activity) {
        this.app = app;
        this.activity = activity;

        this.kotakAktivitas = new Rectangle(1300, 400);
        this.kotakAktivitas.setStyle(
                "-fx-fill: #000612;" +
                        "-fx-opacity: 0.45;" +
                        "-fx-arc-Width: 100;" +
                        "-fx-arc-Height: 100;");

        // DIGANTI: .getJudul() -> .getTitle()
        this.judulAktvLbl = new Label(this.activity.getTitle());
        this.judulAktvLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.judulAktvLbl.setTextFill(Color.rgb(193, 214, 200, 1));

        this.waktuAktvLbl = new Label();
        if (this.activity.getTanggalBatas() != null) {
            this.waktuAktvLbl.setText(this.activity.getTanggalBatasFormatted());
        } else {
            this.waktuAktvLbl.setText("N/A");
        }
        this.waktuAktvLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.waktuAktvLbl.setTextFill(Color.rgb(116, 0, 0, 1));

        this.judulWaktuVBox = new VBox(5);
        this.judulWaktuVBox.getChildren().addAll(this.judulAktvLbl, this.waktuAktvLbl);

        this.lokasiStaticLbl = new Label("LOKASI");
        this.lokasiStaticLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.lokasiStaticLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.lokasiStaticLbl.setUnderline(true);

        // DIGANTI: .getLokasi() -> .getLocation()
        this.namaLokasiAktvLbl = new Label(this.activity.getLocation() != null && !this.activity.getLocation().trim().isEmpty() ? this.activity.getLocation() : "-");
        this.namaLokasiAktvLbl.setFont(Font.font("Segoe UI", 30));
        this.namaLokasiAktvLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.namaLokasiAktvLbl.setWrapText(true);

        this.lokasiVBox = new VBox(5);
        this.lokasiVBox.getChildren().addAll(this.lokasiStaticLbl, this.namaLokasiAktvLbl);

        // DIGANTI: .getLokasi() -> .getLocation()
        boolean hasValidLocation = this.activity.getLocation() != null && !this.activity.getLocation().trim().isEmpty() && !this.activity.getLocation().equalsIgnoreCase("Belum Diisi") && !this.activity.getLocation().equals("-");
        this.lokasiVBox.setVisible(hasValidLocation);
        this.lokasiVBox.setManaged(hasValidLocation);

        this.deskripsiStaticLbl = new Label("DESKRIPSI");
        this.deskripsiStaticLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        this.deskripsiStaticLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.deskripsiStaticLbl.setUnderline(true);

        // DIGANTI: .getDeskripsi() -> .getDescription()
        this.isiDeskripsiAktvLbl = new Label(this.activity.getDescription() != null && !this.activity.getDescription().trim().isEmpty() ? this.activity.getDescription() : "-");
        this.isiDeskripsiAktvLbl.setFont(Font.font("Segoe UI", 30));
        this.isiDeskripsiAktvLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.isiDeskripsiAktvLbl.setWrapText(true);
        this.isiDeskripsiAktvLbl.setMaxWidth(850);

        this.deskripsiVBox = new VBox(5);
        this.deskripsiVBox.getChildren().addAll(this.deskripsiStaticLbl, this.isiDeskripsiAktvLbl);

        this.isiAKtivitasVBox = new VBox(15);
        this.isiAKtivitasVBox.getChildren().addAll(this.judulWaktuVBox, this.lokasiVBox, this.deskripsiVBox);
        this.isiAKtivitasVBox.setAlignment(Pos.CENTER_LEFT);

        this.editBtn = new Button("EDIT");
        this.editBtn.setPrefSize(250, 60);
        this.editBtn.setStyle(
                "-fx-background-color: #012F10;" +
                        "-fx-text-fill: #C1D6C8;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: BOLD;" +
                        "-fx-background-radius: 50;" +
                        "-fx-cursor: hand;");
        this.editBtn.setOnAction(e -> {
            this.app.switchSceneEditSchedulePage(this.activity);
        });

        this.hapusBtn = new Button("HAPUS");
        this.hapusBtn.setPrefSize(250, 60);
        this.hapusBtn.setStyle(
                "-fx-background-color: #012F10;" +
                        "-fx-text-fill: #C1D6C8;" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: BOLD;" +
                        "-fx-background-radius: 50;" +
                        "-fx-cursor: hand;");
        this.hapusBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            // DIGANTI: .getJudul() -> .getTitle(), "tugas" -> "aktivitas"
            alert.setHeaderText("Anda yakin ingin menghapus aktivitas: '" + this.activity.getTitle() + "'?");
            alert.setContentText("Tindakan ini tidak dapat dibatalkan.");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // DIGANTI: .hapusActivity() -> .deleteActivity(), .getId() -> .getActivityId()
                boolean sukses = this.app.getPengelolaActivity().deleteActivity(this.activity.getActivityId());
                if (sukses) {
                    // Teks diubah untuk konsistensi
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Aktivitas berhasil dihapus.");
                    this.app.switchSceneSchedulePage();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus aktivitas.");
                }
            }
        });

        this.editHapusVBox = new VBox(50);
        this.editHapusVBox.getChildren().addAll(this.editBtn, this.hapusBtn);
        this.editHapusVBox.setAlignment(Pos.CENTER);

        this.aktivitasCardLayout = new BorderPane();
        this.aktivitasCardLayout.setLeft(this.isiAKtivitasVBox);
        this.aktivitasCardLayout.setRight(this.editHapusVBox);
        this.aktivitasCardLayout.setPadding(new Insets(60, 80, 60, 80));

        this.aktivitasPane = new StackPane();
        this.aktivitasPane.setPrefSize(1400, 400);
        this.aktivitasPane.setMaxWidth(Double.MAX_VALUE);
        this.aktivitasPane.getChildren().addAll(this.kotakAktivitas, this.aktivitasCardLayout);
    }

    public StackPane getView() {
        return this.aktivitasPane;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}











