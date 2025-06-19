package tubes.launch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tubes.backend.DatabaseManager;
import tubes.backend.EmailService;
import tubes.backend.Notifikasi;
import tubes.backend.ActivityManager;
import tubes.backend.Activity;
import tubes.backend.User;
import tubes.pages.EditSchedule;
import tubes.pages.LogInPage;
import tubes.pages.SchedulePage;
import tubes.pages.SignUpPage;
import tubes.pages.WelcomePage;

// Kelas utama aplikasi, digunakan untuk inisialisasi, pengaturan scene, dan penjadwalan pengingat email
public class mainApp extends Application {

    // Menyimpan stage utama aplikasi
    private Stage stage;
    // Menyimpan scene utama aplikasi
    private Scene scene;

    // Menyimpan instance PengelolaTugas yang digunakan aplikasi
    public static ActivityManager pengelolaActivity;
    // Menyimpan instance Notifikasi untuk pengiriman pengingat
    public static Notifikasi notifikasiService;
    // Menyimpan instance EmailService untuk pengiriman email
    public static EmailService emailService;

    // Scheduler untuk penjadwalan pengiriman email pengingat
    private ScheduledExecutorService scheduler;

    // Konfigurasi SMTP untuk pengiriman email
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "amannotifikasi@gmail.com";
    private static final String SMTP_PASSWORD = "klac pxfp mkne govf";
    private static final boolean SMTP_AUTH = true;
    private static final boolean SMTP_STARTTLS = true;
    private static final boolean SMTP_SSL_ENABLE = false;

    // Fungsi utama yang dijalankan saat aplikasi dimulai
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        pengelolaActivity = new ActivityManager();

        // Inisialisasi database aplikasi
        inisialisasiDatabase();

        // Inisialisasi layanan email dan notifikasi
        emailService = new EmailService(
                SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD,
                SMTP_AUTH, SMTP_STARTTLS, SMTP_SSL_ENABLE
        );
        notifikasiService = new Notifikasi(emailService);

        // Tampilkan halaman welcome sebagai halaman awal
        WelcomePage welcomePage = new WelcomePage(this);
        this.scene = new Scene(welcomePage, 1440, 800);

        stage.setTitle("AMAN");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            System.out.println("Aplikasi ditutup. Mematikan scheduler...");
            shutdownScheduler();
        });
        stage.show();
    }

    // Di mainApp.java
    private void inisialisasiDatabase() {
        System.out.println("Memulai proses inisialisasi database SQLite...");

        String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "User_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL UNIQUE,"
                + "email TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL"
                + ");";

        String createSchedulesTableSql = "CREATE TABLE IF NOT EXISTS schedules ("
                + "Schedule_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "date TEXT NOT NULL,"
                + "time TEXT NOT NULL"
                + ");";

        String createActivitiesTableSql = "CREATE TABLE IF NOT EXISTS activities (" // Diubah
                + "Activity_ID INTEGER PRIMARY KEY AUTOINCREMENT," // Diubah
                + "User_ID INTEGER NOT NULL," // Diubah
                + "Schedule_ID INTEGER UNIQUE," // Diubah
                + "Title TEXT NOT NULL," // Diubah
                + "Description TEXT," // Diubah
                + "Category TEXT," // Diubah
                + "Location TEXT,"
                + "FOREIGN KEY (User_ID) REFERENCES users(User_ID) ON DELETE CASCADE,"
                + "FOREIGN KEY (Schedule_ID) REFERENCES schedules(Schedule_ID) ON DELETE CASCADE"
                + ");";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTableSql);
            stmt.execute(createSchedulesTableSql);
            stmt.execute(createActivitiesTableSql); // Diubah
            // Trigger dihapus karena kolom 'updated_at' sudah tidak ada
            System.out.println("Database (users, schedules, activities) berhasil disiapkan.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk menjadwalkan pengiriman email pengingat  ke user pada waktu tertentu
    public void scheduleEmailReminder(Activity activity, User user, LocalDateTime waktuPengingat) {
        if (scheduler == null || scheduler.isShutdown()) {
            System.err.println("Scheduler tidak aktif. Tidak dapat menjadwalkan pengingat.");
            return;
        }
        if (activity == null || user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.err.println("Data activity atau user tidak valid untuk penjadwalan email.");
            return;
        }

        long delay;
        if (waktuPengingat == null) {
            System.err.println("Waktu pengingat tidak valid (null) untuk activity: " + activity.getTitle());
            return;
        }

        if (waktuPengingat.isBefore(LocalDateTime.now())) {
            System.out.println("Waktu pengingat untuk activity '" + activity.getTitle() + "' (" + waktuPengingat + ") sudah lewat. Mengirim segera.");
            delay = 0;
        } else {
            delay = Duration.between(LocalDateTime.now(), waktuPengingat).toMillis();
        }

        if (delay < 0) {
            delay = 0;
        }

        scheduler.schedule(() -> {
            System.out.println("Scheduler: Memulai proses pengiriman pengingat untuk tugas: " + activity.getTitle());
            boolean terkirim = notifikasiService.kirimPengingat(activity, user);
            if (terkirim) {
                System.out.println("Scheduler: Pengingat untuk '" + activity.getTitle() + "' berhasil dikirim ke " + user.getEmail());
            } else {
                System.err.println("Scheduler: Gagal mengirim pengingat untuk '" + activity.getTitle() + "' ke " + user.getEmail());
            }
        }, delay, TimeUnit.MILLISECONDS);

        if (delay == 0) {
            System.out.println("Pengingat untuk tugas '" + activity.getTitle() + "' dijadwalkan untuk dikirim segera.");
        } else {
            System.out.println("Pengingat untuk tugas '" + activity.getTitle() + "' dijadwalkan pada " + waktuPengingat + " (delay: " + delay + "ms)");
        }
    }

    // Fungsi untuk mematikan scheduler saat aplikasi ditutup
    public void shutdownScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        System.err.println("Scheduler tidak dapat dimatikan dengan benar.");
                    }
                }
            } catch (InterruptedException ie) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // Fungsi untuk menampilkan halaman WelcomePage
    public void switchSceneWelcomePage(){
        this.scene.setRoot(new WelcomePage(this));
    }

    // Fungsi untuk menampilkan halaman LogInPage
    public void switchSceneLogInPage(){
        this.scene.setRoot(new LogInPage(this));
    }

    // Fungsi untuk menampilkan halaman SignUpPage
    public void switchSceneSignUpPage(){
        this.scene.setRoot(new SignUpPage(this));
    }

    // Fungsi untuk menampilkan halaman SchedulePage jika user sudah login
    public void switchSceneSchedulePage(){
        if (pengelolaActivity == null || pengelolaActivity.getCurrentUser() == null) {
            System.out.println("Tidak ada user yang login atau PengelolaActivity belum siap, mengarahkan ke halaman Login.");
            switchSceneLogInPage();
            return;
        }
        this.scene.setRoot(new SchedulePage(this));
    }

    // Fungsi untuk menampilkan halaman EditSchedulePage untuk mengedit tugas tertentu
    public void switchSceneEditSchedulePage(Activity activityToEdit){
        if (pengelolaActivity == null || pengelolaActivity.getCurrentUser() == null) {
            switchSceneLogInPage();
            return;
        }
        this.scene.setRoot(new EditSchedule(this, activityToEdit));
    }

    // Fungsi untuk menampilkan halaman EditSchedulePage untuk membuat tugas baru
    public void switchSceneEditSchedulePage(){
        if (pengelolaActivity == null || pengelolaActivity.getCurrentUser() == null) {
            switchSceneLogInPage();
            return;
        }
        this.scene.setRoot(new EditSchedule(this, null));
    }

    // Mengambil instance PengelolaActivity yang sedang digunakan aplikasi
    public ActivityManager getPengelolaActivity() {
        return pengelolaActivity;
    }

    // Fungsi main untuk menjalankan aplikasi JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}