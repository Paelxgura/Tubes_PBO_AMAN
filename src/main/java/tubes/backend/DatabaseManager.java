package tubes.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Kelas utilitas untuk mengelola koneksi dan resource database SQLite pada aplikasi
public class DatabaseManager {
    // Nama file database SQLite yang digunakan aplikasi
    private static final String DB_NAME = "aman_app.db";
    // URL koneksi JDBC yang digunakan untuk menghubungkan ke database SQLite
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    // Membuka koneksi ke database SQLite dan mengaktifkan foreign key constraint
    // Mengembalikan objek Connection jika berhasil, atau null jika gagal
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL);

            if (connection != null) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }

        } catch (SQLException e) {
            System.err.println("Koneksi ke SQLite gagal: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Menutup koneksi database yang sudah tidak digunakan lagi
    // Penting untuk mencegah kebocoran resource pada aplikasi
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi SQLite: " + e.getMessage());
            }
        }
    }

    // Menutup objek Statement setelah selesai digunakan
    // Membantu membebaskan resource database
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup Statement SQLite: " + e.getMessage());
            }
        }
    }

    // Menutup objek ResultSet setelah selesai digunakan
    // Mencegah kebocoran resource pada aplikasi
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup ResultSet SQLite: " + e.getMessage());
            }
        }
    }
}