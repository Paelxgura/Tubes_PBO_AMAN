// File: ActivityManager.java (FINAL LENGKAP v2)
package tubes.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ActivityManager {
    private User currentUser;

    public ActivityManager() { this.currentUser = null; }
    public User getCurrentUser() { return currentUser; }
    public void logout() { this.currentUser = null; }

    // --- METODE UNTUK USER (Lengkap) ---
    public User daftarAkun(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new User(generatedKeys.getInt(1), username, email, password);
                    }
                }
            }
        } catch (SQLException e) {
            // Unik constraint gagal, artinya username/email sudah ada
            System.err.println("Gagal mendaftar, username atau email mungkin sudah ada. " + e.getMessage());
        }
        return null;
    }

    public boolean masukSistem(String usernameOrEmail, String passwordInput) {
        String sql = "SELECT User_ID, username, email, password FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getString("password").equals(passwordInput)) {
                    this.currentUser = new User(rs.getInt("User_ID"), rs.getString("username"), rs.getString("email"), rs.getString("password"));
                    this.currentUser.setDaftarAktivitas(getActivitiesByUserId(this.currentUser.getUserId()));
                    return true;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // --- METODE UNTUK ACTIVITY (Lengkap) ---

    public Activity createActivity(String title, String description, LocalDateTime tanggalBatas, String category, String location) {
        if (currentUser == null) {
            System.err.println("Tidak ada user yang login untuk membuat aktivitas.");
            return null;
        }

        // --- LOGIKA PENCEGAHAN DUPLIKASI (PALING KETAT) ---
        // Query sekarang memeriksa SEMUA atribut utama
        String checkSql = "SELECT a.Activity_ID FROM activities a " +
                "JOIN schedules s ON a.Schedule_ID = s.Schedule_ID " +
                "WHERE a.User_ID = ? AND a.Title = ? AND a.Description = ? " +
                "AND a.Category = ? AND a.Location = ? " +
                "AND s.date = ? AND s.time = ?";

        String insertScheduleSql = "INSERT INTO schedules (date, time) VALUES (?, ?)";
        String insertActivitySql = "INSERT INTO activities (User_ID, Schedule_ID, Title, Description, Category, Location) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();

            // Langkah 1: Periksa apakah ada data yang 100% identik
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, currentUser.getUserId());
                checkStmt.setString(2, title);
                checkStmt.setString(3, description);
                checkStmt.setString(4, category);
                checkStmt.setString(5, location);
                checkStmt.setString(6, tanggalBatas.toLocalDate().toString());
                checkStmt.setString(7, tanggalBatas.toLocalTime().withNano(0).toString());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        return null;
                    }
                }
            }
            // --- SELESAI LOGIKA PENCEGAHAN DUPLIKASI ---

            // Jika tidak duplikat, lanjutkan proses penyimpanan data baru dengan transaksi
            conn.setAutoCommit(false);

            int scheduleId;
            try (PreparedStatement scheduleStmt = conn.prepareStatement(insertScheduleSql, Statement.RETURN_GENERATED_KEYS)) {
                scheduleStmt.setString(1, tanggalBatas.toLocalDate().toString());
                scheduleStmt.setString(2, tanggalBatas.toLocalTime().withNano(0).toString());
                scheduleStmt.executeUpdate();
                try (ResultSet generatedKeys = scheduleStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        scheduleId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Gagal membuat jadwal, ID tidak didapat.");
                    }
                }
            }

            int activityId;
            try (PreparedStatement activityStmt = conn.prepareStatement(insertActivitySql, Statement.RETURN_GENERATED_KEYS)) {
                activityStmt.setInt(1, currentUser.getUserId());
                activityStmt.setInt(2, scheduleId);
                activityStmt.setString(3, title);
                activityStmt.setString(4, description);
                activityStmt.setString(5, category);
                activityStmt.setString(6, location);
                activityStmt.executeUpdate();
                try (ResultSet generatedKeys = activityStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        activityId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Gagal membuat aktivitas, ID tidak didapat.");
                    }
                }
            }

            conn.commit();

            Schedule newSchedule = new Schedule(tanggalBatas.toLocalDate(), tanggalBatas.toLocalTime());
            newSchedule.setScheduleId(scheduleId);
            Activity newActivity = new Activity(activityId, currentUser.getUserId(), title, description, category, location, newSchedule);
            if (this.currentUser != null) {
                this.currentUser.tambahAktivitas(newActivity);
            }
            return newActivity;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            showAlert(Alert.AlertType.ERROR, "Database Error", "Terjadi kesalahan saat menyimpan data.");
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DatabaseManager.closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Tambahkan metode helper ini di dalam kelas ActivityManager untuk menampilkan alert dari backend
// Ini adalah praktik yang kurang ideal (backend sebaiknya tidak memicu UI),
// tapi untuk aplikasi desktop sederhana ini bisa diterima.
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public boolean updateActivity(int activityId, String title, String description, LocalDateTime tanggalBatas, String category, String location) {
        if (currentUser == null) return false;
        String updateActivitySql = "UPDATE activities SET Title = ?, Description = ?, Category = ?, Location = ? WHERE Activity_ID = ? AND User_ID = ?";
        String findScheduleIdSql = "SELECT Schedule_ID FROM activities WHERE Activity_ID = ?";
        String updateScheduleSql = "UPDATE schedules SET date = ?, time = ? WHERE Schedule_ID = ?";
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement activityStmt = conn.prepareStatement(updateActivitySql)) {
                activityStmt.setString(1, title);
                activityStmt.setString(2, description);
                activityStmt.setString(3, category);
                activityStmt.setString(4, location);
                activityStmt.setInt(5, activityId);
                activityStmt.setInt(6, currentUser.getUserId());
                activityStmt.executeUpdate();
            }
            int scheduleId = -1;
            try (PreparedStatement findStmt = conn.prepareStatement(findScheduleIdSql)) {
                findStmt.setInt(1, activityId);
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) scheduleId = rs.getInt("Schedule_ID");
                }
            }
            if (scheduleId != -1) {
                try (PreparedStatement scheduleStmt = conn.prepareStatement(updateScheduleSql)) {
                    scheduleStmt.setString(1, tanggalBatas.toLocalDate().toString());
                    scheduleStmt.setString(2, tanggalBatas.toLocalTime().toString());
                    scheduleStmt.setInt(3, scheduleId);
                    scheduleStmt.executeUpdate();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); DatabaseManager.closeConnection(conn); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public boolean deleteActivity(int activityId) {
        if (currentUser == null) return false;
        String sql = "DELETE FROM activities WHERE Activity_ID = ? AND User_ID = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, activityId);
            pstmt.setInt(2, currentUser.getUserId());
            if (pstmt.executeUpdate() > 0) {
                if (this.currentUser != null) this.currentUser.hapusAktivitas(activityId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Activity> getCurrentUserActivities() {
        if (currentUser == null) return new ArrayList<>();
        return getActivitiesByUserId(currentUser.getUserId());
    }

    public List<Activity> getCurrentUserActivitiesByCategory(String categoryFilter) {
        if (currentUser == null) return new ArrayList<>();
        if (categoryFilter == null || categoryFilter.equalsIgnoreCase("Semua") || categoryFilter.trim().isEmpty()) {
            return getCurrentUserActivities();
        }
        return getActivitiesByUserIdAndCategory(currentUser.getUserId(), categoryFilter);
    }

    public Activity getActivityById(int activityId) {
        if (currentUser == null) return null;
        String sql = "SELECT a.Activity_ID, a.User_ID, a.Title, a.Description, a.Category, a.Location, " +
                "s.Schedule_ID as schedule_id_col, s.date, s.time " +
                "FROM activities a JOIN schedules s ON a.Schedule_ID = s.Schedule_ID " +
                "WHERE a.Activity_ID = ? AND a.User_ID = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, activityId);
            pstmt.setInt(2, currentUser.getUserId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Schedule schedule = new Schedule(rs.getInt("schedule_id_col"), LocalDate.parse(rs.getString("date")), LocalTime.parse(rs.getString("time")));
                    return new Activity(rs.getInt("Activity_ID"), rs.getInt("User_ID"), rs.getString("Title"), rs.getString("Description"),
                            rs.getString("Category"), rs.getString("Location"), schedule);
                }
            }
        } catch (SQLException | DateTimeParseException e) { e.printStackTrace(); }
        return null;
    }

    private List<Activity> getActivitiesByUserId(int userId) {
        List<Activity> activityList = new ArrayList<>();
        String sql = "SELECT a.Activity_ID, a.User_ID, a.Title, a.Description, a.Category, a.Location, " +
                "s.Schedule_ID as schedule_id_col, s.date, s.time " +
                "FROM activities a JOIN schedules s ON a.Schedule_ID = s.Schedule_ID " +
                "WHERE a.User_ID = ? ORDER BY s.date ASC, s.time ASC";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Schedule schedule = new Schedule(rs.getInt("schedule_id_col"), LocalDate.parse(rs.getString("date")), LocalTime.parse(rs.getString("time")));
                    Activity activity = new Activity(rs.getInt("Activity_ID"), rs.getInt("User_ID"), rs.getString("Title"), rs.getString("Description"),
                            rs.getString("Category"), rs.getString("Location"), schedule);
                    activityList.add(activity);
                }
            }
        } catch (SQLException | DateTimeParseException e) { e.printStackTrace(); }
        return activityList;
    }

    private List<Activity> getActivitiesByUserIdAndCategory(int userId, String categoryFilter) {
        List<Activity> activityList = new ArrayList<>();
        String sql = "SELECT a.Activity_ID, a.User_ID, a.Title, a.Description, a.Category, a.Location, " +
                "s.Schedule_ID as schedule_id_col, s.date, s.time " +
                "FROM activities a JOIN schedules s ON a.Schedule_ID = s.Schedule_ID " +
                "WHERE a.User_ID = ? AND a.Category = ? ORDER BY s.date ASC, s.time ASC";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, categoryFilter);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Schedule schedule = new Schedule(rs.getInt("schedule_id_col"), LocalDate.parse(rs.getString("date")), LocalTime.parse(rs.getString("time")));
                    Activity activity = new Activity(rs.getInt("Activity_ID"), rs.getInt("User_ID"), rs.getString("Title"), rs.getString("Description"),
                            rs.getString("Category"), rs.getString("Location"), schedule);
                    activityList.add(activity);
                }
            }
        } catch (SQLException | DateTimeParseException e) { e.printStackTrace(); }
        return activityList;
    }
}