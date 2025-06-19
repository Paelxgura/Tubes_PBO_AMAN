// File: Notifikasi.java (FINAL - Sudah Disesuaikan)
package tubes.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notifikasi {
    private EmailService pengirim;

    public Notifikasi(EmailService emailService) {
        this.pengirim = emailService;
    }

    // Fungsi utama untuk mengirim email pengingat
    // Parameter diubah dari 'Tugas' menjadi 'Activity'
    public boolean kirimPengingat(Activity activity, User user) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty() || activity == null) {
            System.err.println("User, email user, atau Activity tidak boleh null untuk mengirim pengingat.");
            return false;
        }
        if (this.pengirim == null) {
            System.err.println("EmailService (pengirim) belum diinisialisasi di Notifikasi.");
            return false;
        }

        String emailTo = user.getEmail();
        // Menggunakan getter baru dari objek 'activity'
        String subjek = "Pengingat Aktivitas: " + activity.getTitle();

        // Menggunakan getter baru dari objek 'activity'
        String tanggalBatasFormatted = (activity.getTanggalBatas() != null) ?
                activity.getTanggalBatas().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'pukul' HH:mm")) :
                "Tidak ditentukan";

        String namaUser = user.getUsername() != null ? user.getUsername() : "Pengguna";

        // Menggunakan getter baru dari objek 'activity'
        String judulAktivitas = activity.getTitle() != null ? activity.getTitle() : "(Tanpa Judul)";
        String deskripsiAktivitas = activity.getDescription() != null && !activity.getDescription().trim().isEmpty() ? activity.getDescription() : "-";
        String kategoriAktivitas = activity.getCategory() != null ? activity.getCategory() : "-";
        String lokasiAktivitas = (activity.getLocation() != null && !activity.getLocation().isEmpty() && !activity.getLocation().equalsIgnoreCase("Belum Diisi")) ? activity.getLocation() : null;

        String urlLogo = "https://placehold.co/100x40/012F10/C1D6C8?text=AMAN";

        // Teks di dalam email disesuaikan menjadi "Aktivitas"
        String isiHtml = String.format("""
        <!DOCTYPE html>
        <html lang="id">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>%s</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; color: #333; }
                .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                .header { text-align: center; margin-bottom: 20px; }
                .header img { max-width: 150px; }
                .content h1 { color: #012F10; font-size: 24px; text-align: center; }
                .content p { line-height: 1.6; margin-bottom: 15px; }
                .task-details { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
                .task-details strong { color: #012F10; }
                .button-container { text-align: center; margin-top: 25px; }
                .button { background-color: #012F10; color: #C1D6C8; padding: 12px 25px; text-decoration: none; border-radius: 25px; font-weight: bold; display: inline-block; }
                .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #777; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                </div>
                <div class="content">
                    <h1>Pengingat Aktivitas Anda!</h1>
                    <p>Halo <strong>%s</strong>,</p>
                    <p>Ini adalah pengingat untuk aktivitas Anda yang akan segera jatuh tempo atau sudah lewat:</p>
                    <div class="task-details">
                        <p><strong>Judul:</strong> %s</p>
                        <p><strong>Waktu:</strong> %s</p>
                        <p><strong>Kategori:</strong> %s</p>
                        %s
                        <p><strong>Deskripsi:</strong></p>
                        <p>%s</p>
                    </div>
                    <p>Mohon untuk segera menyelesaikan aktivitas Anda.</p>
                </div>
                <div class="footer">
                    <p></p>
                </div>
            </div>
        </body>
        </html>
        """,
                subjek,
                namaUser,
                judulAktivitas,
                tanggalBatasFormatted,
                kategoriAktivitas,
                (lokasiAktivitas != null ? String.format("<p><strong>Lokasi:</strong> %s</p>", lokasiAktivitas) : ""),
                deskripsiAktivitas,
                LocalDateTime.now().getYear()
        );

        System.out.println("Notifikasi: Mempersiapkan pengingat HTML untuk aktivitas: " + judulAktivitas + " kepada user: " + namaUser + " (" + emailTo + ")");
        return pengirim.kirimEmail(emailTo, subjek, isiHtml);
    }
}