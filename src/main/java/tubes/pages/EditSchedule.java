// File: EditSchedule.java (FINAL - Sudah Diperbaiki)
package tubes.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import tubes.backend.Activity;
import tubes.backend.ActivityManager;
import tubes.backend.Schedule;
import tubes.backend.User;
import tubes.launch.mainApp;
import javafx.geometry.HPos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;

public class EditSchedule extends StackPane {

    private mainApp app;
    private Activity activityToEdit; // DIGANTI

    private Label topLbl;
    private TextField namaKegiatanField;
    private ComboBox<String> kategoriCB;
    private DatePicker tanggalDP;
    private TextField waktuField;
    private TextField lokasiField;
    private TextField deskripsiField;
    private CheckBox ingatkanSayaCheckBox;
    private ComboBox<Integer> jamSebelumComboBox;

    // Konstruktor diubah untuk menerima Activity
    public EditSchedule(mainApp app, Activity activity) {
        this.app = app;
        this.activityToEdit = activity;

        String backgroundString = getClass().getResource("/Background.png").toString();
        ImageView backgroundImage = new ImageView(new Image(backgroundString));
        backgroundImage.fitWidthProperty().bind(this.widthProperty());
        backgroundImage.fitHeightProperty().bind(this.heightProperty());

        this.topLbl = new Label();
        this.topLbl.setFont(Font.font("Franklin Gothic Heavy", FontWeight.BOLD, FontPosture.ITALIC, 70));
        this.topLbl.setTextFill(Color.rgb(193, 214, 200, 1));
        this.topLbl.setTextAlignment(TextAlignment.CENTER);
        HBox topLblWrapper = new HBox(this.topLbl);
        topLblWrapper.setAlignment(Pos.CENTER);
        topLblWrapper.setPadding(new Insets(10, 0, 10, 0));

        // ... (Kode untuk styling tidak berubah) ...
        Rectangle persegi = new Rectangle();
        persegi.setWidth(800);
        persegi.setHeight(800);
        persegi.setStyle("-fx-fill: #FFFFFF;-fx-opacity: 0.35;-fx-arc-width: 100;-fx-arc-height: 100;");
        Label namaKegiatanLbl = new Label("NAMA AKTIVITAS");
        namaKegiatanLbl.setFont(Font.font("Segoe UI", 20));
        namaKegiatanLbl.setTextFill(Color.rgb(1, 47, 16, 1));
        Label kategoriLbl = new Label("KATEGORI");
        kategoriLbl.setFont(Font.font("Segoe UI", 20));
        kategoriLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        Label tanggalLbl = new Label("TANGGAL");
        tanggalLbl.setFont(Font.font("Segoe UI", 20));
        tanggalLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        Label waktuLbl = new Label("WAKTU");
        waktuLbl.setFont(Font.font("Segoe UI", 20));
        waktuLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        Label lokasiLbl = new Label("LOKASI");
        lokasiLbl.setFont(Font.font("Segoe UI", 20));
        lokasiLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        Label deskripsiLbl = new Label("DESKRIPSI");
        deskripsiLbl.setFont(Font.font("Segoe UI", 20));
        deskripsiLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        Label pengingatLbl = new Label("PENGINGAT EMAIL");
        pengingatLbl.setFont(Font.font("Segoe UI", 20));
        pengingatLbl.setTextFill(Color.rgb(1, 47, 16, 1));

        String fieldStyle = "-fx-background-color: rgb(0, 6, 18, 0.35);-fx-border-color: transparent;-fx-border-radius: 5;-fx-text-fill: rgb(193, 214, 200, 1);-fx-font-size: 15px;";
        double fieldPrefWidth = 600;
        double fieldPrefHeight = 50;

        this.namaKegiatanField = new TextField();
        this.namaKegiatanField.setPrefSize(fieldPrefWidth, fieldPrefHeight);
        this.namaKegiatanField.setMinWidth(Region.USE_PREF_SIZE);
        this.namaKegiatanField.setStyle(fieldStyle);

        ArrayList<String> kategoriItems = new ArrayList<>();
        kategoriItems.add("AKADEMIK");
        kategoriItems.add("NON-AKADEMIK");
        this.kategoriCB = new ComboBox<>();
        this.kategoriCB.getItems().addAll(kategoriItems);
        this.kategoriCB.setPrefSize(fieldPrefWidth, fieldPrefHeight);
        this.kategoriCB.setMaxWidth(Double.MAX_VALUE);
        this.kategoriCB.setStyle(fieldStyle + "-fx-background-insets: 0;");

        this.tanggalDP = new DatePicker();
        this.tanggalDP.setPrefSize(fieldPrefWidth, fieldPrefHeight);
        this.tanggalDP.setMaxWidth(Double.MAX_VALUE);
        this.tanggalDP.setStyle(fieldStyle);
        this.tanggalDP.getEditor().setStyle("-fx-text-fill: rgb(193, 214, 200, 1);");

        this.waktuField = new TextField();
        this.waktuField.setPromptText("HH:mm (contoh: 14:30)");
        this.waktuField.setPrefSize(fieldPrefWidth, fieldPrefHeight);
        this.waktuField.setMinWidth(Region.USE_PREF_SIZE);
        this.waktuField.setStyle(fieldStyle);

        this.lokasiField = new TextField();
        this.lokasiField.setPrefSize(fieldPrefWidth, fieldPrefHeight);
        this.lokasiField.setMinWidth(Region.USE_PREF_SIZE);
        this.lokasiField.setStyle(fieldStyle);

        this.deskripsiField = new TextField();
        this.deskripsiField.setPrefSize(fieldPrefWidth, fieldPrefHeight);
        this.deskripsiField.setMinWidth(Region.USE_PREF_SIZE);
        this.deskripsiField.setStyle(fieldStyle);

        this.ingatkanSayaCheckBox = new CheckBox("Ingatkan saya");
        this.ingatkanSayaCheckBox.setPrefHeight(fieldPrefHeight);
        this.ingatkanSayaCheckBox.setTextFill(Color.rgb(1, 47, 16, 1));
        this.ingatkanSayaCheckBox.setFont(Font.font("Segoe UI", 16));

        this.jamSebelumComboBox = new ComboBox<>();
        this.jamSebelumComboBox.getItems().addAll(1, 2, 3, 6, 12, 24);
        this.jamSebelumComboBox.setPromptText("Jam sebelum");
        this.jamSebelumComboBox.setPrefWidth(150);
        this.jamSebelumComboBox.setStyle(fieldStyle + "-fx-background-insets: 0;");
        this.jamSebelumComboBox.setDisable(true);

        this.ingatkanSayaCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            jamSebelumComboBox.setDisable(!newVal);
            if (!newVal) {
                jamSebelumComboBox.getSelectionModel().clearSelection();
            } else { jamSebelumComboBox.getSelectionModel().selectFirst();
            }
        });

        HBox pengingatOptionsBox = new HBox(10, this.ingatkanSayaCheckBox, this.jamSebelumComboBox);
        pengingatOptionsBox.setAlignment(Pos.CENTER_LEFT);

        // Logika pengisian form diubah untuk menggunakan 'activityToEdit' dan getter baru
        if (this.activityToEdit != null) {
            this.topLbl.setText("EDIT JADWAL");
            this.namaKegiatanField.setText(this.activityToEdit.getTitle()); // .getTitle()
            this.kategoriCB.setValue(this.activityToEdit.getCategory()); // .getCategory()
            if (this.activityToEdit.getTanggalBatas() != null) {
                this.tanggalDP.setValue(this.activityToEdit.getTanggalBatas().toLocalDate());
                this.waktuField.setText(this.activityToEdit.getTanggalBatas().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            this.lokasiField.setText(this.activityToEdit.getLocation()); // .getLocation()
            this.deskripsiField.setText(this.activityToEdit.getDescription()); // .getDescription()
        } else {
            this.topLbl.setText("MASUKKAN JADWAL BARU");
        }

        GridPane inputGridPane = new GridPane();
        inputGridPane.setHgap(50);
        inputGridPane.setVgap(33);
        inputGridPane.setAlignment(Pos.CENTER);
        inputGridPane.setPadding(new Insets(30, 50, 25, 50));

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setHalignment(HPos.LEFT);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        inputGridPane.getColumnConstraints().addAll(col0, col1);

        inputGridPane.add(namaKegiatanLbl, 0, 0);
        inputGridPane.add(this.namaKegiatanField, 1, 0);
        inputGridPane.add(kategoriLbl, 0, 1);
        inputGridPane.add(this.kategoriCB, 1, 1);
        inputGridPane.add(tanggalLbl, 0, 2);
        inputGridPane.add(this.tanggalDP, 1, 2);
        inputGridPane.add(waktuLbl, 0, 3);
        inputGridPane.add(this.waktuField, 1, 3);
        inputGridPane.add(lokasiLbl, 0, 4);
        inputGridPane.add(this.lokasiField, 1, 4);
        inputGridPane.add(deskripsiLbl, 0, 5);
        inputGridPane.add(this.deskripsiField, 1, 5);
        inputGridPane.add(pengingatLbl, 0, 6);
        inputGridPane.add(pengingatOptionsBox, 1, 6);

        String buttonStyle = "-fx-background-color: rgb(1, 47, 16, 1);" +
                "-fx-border-color: transparent;" +
                "-fx-text-fill: rgb(193, 214, 200, 1);" +
                "-fx-font-size: 18px;-fx-cursor: hand;" +
                "-fx-font-weight: BOLD;" +
                "-fx-background-radius: 30;";

        Button batalBtn = new Button("BATAL");
        batalBtn.setStyle(buttonStyle);
        batalBtn.setPrefSize(180, 35);
        batalBtn.setTranslateX(-100);
        batalBtn.setTranslateY(605);

        Button simpanBtn = new Button("SIMPAN");
        simpanBtn.setStyle(buttonStyle);
        simpanBtn.setPrefSize(180, 35);
        simpanBtn.setTranslateX(100);
        simpanBtn.setTranslateY(605);

        StackPane inputCard = new StackPane();
        persegi.widthProperty().bind(inputGridPane.widthProperty());
        persegi.heightProperty().bind(inputGridPane.heightProperty());
        inputCard.getChildren().addAll(persegi, inputGridPane);
        inputCard.setPadding(new Insets(20));

        StackPane formContentVBox = new StackPane(inputCard, batalBtn, simpanBtn);
        formContentVBox.setAlignment(Pos.TOP_CENTER);
        formContentVBox.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(formContentVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        BorderPane pageLayout = new BorderPane();
        pageLayout.setTop(topLblWrapper);
        pageLayout.setCenter(scrollPane);
        pageLayout.setPadding(new Insets(10, 30, 10, 30));

        this.getChildren().addAll(backgroundImage, pageLayout);

        batalBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Batal");
            alert.setHeaderText("Anda yakin ingin membatalkan?");
            // Menggunakan activityToEdit
            alert.setContentText(this.activityToEdit != null ? "Perubahan tidak akan disimpan." : "Jadwal baru tidak akan dibuat.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                this.app.switchSceneSchedulePage();
            }
        });

        simpanBtn.setOnAction(e -> {
            handleSimpan();
        });
    }

    private void handleSimpan() {
        String namaKegiatan = this.namaKegiatanField.getText();
        String kategori = this.kategoriCB.getValue();
        LocalDate tanggal = this.tanggalDP.getValue();
        String waktuText = this.waktuField.getText();
        String lokasi = this.lokasiField.getText();
        String deskripsi = this.deskripsiField.getText();
        boolean ingatkan = this.ingatkanSayaCheckBox.isSelected();
        Integer jamSebelumDeadline = this.jamSebelumComboBox.getValue();

        // ... (Logika validasi input tidak berubah) ...
        if (namaKegiatan == null || namaKegiatan.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Nama Kegiatan tidak boleh kosong.");
            return;
        }

        if (kategori == null) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Kategori harus dipilih.");
            return;
        }

        if (tanggal == null) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Tanggal harus dipilih.");
            return;
        }

        LocalDateTime tanggalBatasGabungan = null;
        if (waktuText != null && !waktuText.trim().isEmpty()) {
            try {
                LocalTime waktu = LocalTime.parse(waktuText, DateTimeFormatter.ofPattern("HH:mm"));
                tanggalBatasGabungan = LocalDateTime.of(tanggal, waktu);
            } catch (DateTimeParseException ex) {
                showAlert(Alert.AlertType.ERROR, "Format Waktu Salah", "Gunakan format HH:mm untuk waktu (contoh: 09:30 atau 14:00).");
                return;
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Waktu harus diisi.");
            return;
        }
        if (ingatkan && (jamSebelumDeadline == null || jamSebelumDeadline <= 0)) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Pilih berapa jam sebelum deadline untuk pengingat.");
            return;
        }

        String lokasiInput = (lokasi == null || lokasi.trim().isEmpty()) ? "Belum Diisi" : lokasi.trim();

        boolean sukses;

        Activity activityHasil = null; // DIGANTI ke Activity

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Simpan");
        confirmAlert.setHeaderText("Anda yakin ingin menyimpan jadwal ini?");
        confirmAlert.setContentText("Pastikan semua data sudah benar.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ActivityManager activityManager = this.app.getPengelolaActivity();

            if (activityToEdit == null) {
                // Membuat aktivitas baru
                activityHasil = activityManager.createActivity(
                        namaKegiatan,
                        deskripsi,
                        tanggalBatasGabungan,
                        kategori,
                        lokasiInput
                );
                sukses = (activityHasil != null);

            } else {
                // Mengubah aktivitas yang ada
                sukses = activityManager.updateActivity(
                        activityToEdit.getActivityId(), // .getActivityId()
                        namaKegiatan,
                        deskripsi,
                        tanggalBatasGabungan,
                        kategori,
                        lokasiInput
                );
                if(sukses) activityHasil = activityManager.getActivityById(activityToEdit.getActivityId());
            }

            if (sukses) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data jadwal berhasil disimpan.");
                if (ingatkan && jamSebelumDeadline != null && activityHasil != null && activityManager.getCurrentUser() != null) {
                    LocalDateTime waktuPengingat = tanggalBatasGabungan.minusHours(jamSebelumDeadline);
                    User currentUser = activityManager.getCurrentUser();
                    app.scheduleEmailReminder(activityHasil, currentUser, waktuPengingat);
                }
                this.app.switchSceneSchedulePage();
            } else {
                showAlert(Alert.AlertType.WARNING, "Gagal Menyimpan", "Gagal menyimpan data, kemungkinan ada jadwal yang sama");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}