package tubes.backend;

import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Kelas ini digunakan untuk mengatur konfigurasi SMTP dan mengirim email melalui aplikasi
public class EmailService {
    // Menyimpan username email yang digunakan untuk autentikasi SMTP
    private final String username;
    // Menyimpan password email yang digunakan untuk autentikasi SMTP
    private final String password;
    // Menyimpan properti konfigurasi SMTP
    private final Properties props;

    // Konstruktor utama untuk menginisialisasi konfigurasi SMTP secara detail
    public EmailService(String host, String port, String username, String password, boolean auth, boolean starttls, boolean sslEnable) {
        this.username = username;
        this.password = password;

        this.props = new Properties();
        this.props.put("mail.smtp.host", host);
        this.props.put("mail.smtp.port", port);
        this.props.put("mail.smtp.auth", String.valueOf(auth));

        if (starttls) {
            this.props.put("mail.smtp.starttls.enable", "true");
        }
        if (sslEnable) {
            this.props.put("mail.smtp.socketFactory.port", port);
            this.props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            this.props.put("mail.smtp.ssl.enable", "true");
        }
        System.out.println("EmailService diinisialisasi dengan config SMTP: Host=" + host + ", Port=" + port + ", User=" + username);
    }

    // Konstruktor deprecated, digunakan untuk inisialisasi menggunakan Map konfigurasi SMTP
    // Disarankan menggunakan konstruktor utama untuk konfigurasi yang lebih jelas
    @Deprecated
    public EmailService(Map<String, String> smtpConfig) {
        this(
            smtpConfig.getOrDefault("host", "smtp.gmail.com"),
            smtpConfig.getOrDefault("port", "587"),
            smtpConfig.getOrDefault("username", "default_user@example.com"),
            smtpConfig.getOrDefault("password", "default_password"),
            Boolean.parseBoolean(smtpConfig.getOrDefault("auth", "true")),
            Boolean.parseBoolean(smtpConfig.getOrDefault("starttls_enable", "true")),
            Boolean.parseBoolean(smtpConfig.getOrDefault("ssl_enable", "false"))
        );
        System.out.println("EmailService diinisialisasi menggunakan konstruktor Map (deprecated). Harap perbarui ke konstruktor detail.");
    }

    // Fungsi untuk mengirim email dengan format HTML ke alamat tujuan tertentu
    // Mengembalikan true jika email berhasil dikirim, false jika gagal
    public boolean kirimEmail(String to, String subjek, String isiHtml) { 
        Session session = Session.getInstance(this.props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subjek);
            message.setContent(isiHtml, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email HTML berhasil dikirim ke: " + to + " dengan subjek: " + subjek);
            return true;
        } catch (MessagingException e) {
            System.err.println("Gagal mengirim email HTML ke: " + to + ". Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}