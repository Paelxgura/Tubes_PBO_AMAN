// File: User.java (FINAL v2)
package tubes.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private int userId; // Diubah dari id
    private String username;
    private String email;
    private String password;
    private List<Activity> daftarAktivitas;

    public User(int userId, String username, String email, String password) { // Diubah dari id
        this.userId = userId; // Diubah dari id
        this.username = username;
        this.email = email;
        this.password = password;
        this.daftarAktivitas = new ArrayList<>();
    }

    // --- Getters & Setters Disesuaikan ---
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    // Getters & Setters lain (tidak berubah)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public List<Activity> getDaftarAktivitas() { return daftarAktivitas; }
    public void setDaftarAktivitas(List<Activity> daftarAktivitas) { this.daftarAktivitas = daftarAktivitas; }

    public void tambahAktivitas(Activity activity) {
        if (activity != null && activity.getUserId() == this.userId) { // Diubah
            this.daftarAktivitas.add(activity);
        }
    }

    public boolean hapusAktivitas(int activityId) {
        return this.daftarAktivitas.removeIf(activity -> activity.getActivityId() == activityId);
    }

    public boolean verifikasiPassword(String inputPassword) {
        return Objects.equals(this.password, inputPassword);
    }
}