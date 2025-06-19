// File: Activity.java (FINAL)
package tubes.backend;

import java.time.LocalDateTime;
import java.util.Objects;

public class Activity {
    private int activityId; // id -> activityId
    private int userId;
    private String title; // judul -> title
    private String description; // deskripsi -> description
    private String category; // kategori -> category
    private String location;
    private Schedule schedule;

    // Konstruktor untuk mengambil dari DB
    public Activity(int activityId, int userId, String title, String description, String category, String location, Schedule schedule) {
        this.activityId = activityId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.schedule = schedule;
    }

    // Konstruktor untuk membuat baru dari UI
    public Activity(int userId, String title, String description, String category, String location, Schedule schedule) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.schedule = schedule;
    }

    // GETTERS (nama sudah disesuaikan)
    public int getActivityId() { return activityId; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public Schedule getSchedule() { return schedule; }

    // SETTERS (nama sudah disesuaikan)
    public void setActivityId(int activityId) { this.activityId = activityId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setLocation(String location) { this.location = location; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }

    // Helper methods
    public String getTanggalBatasFormatted() {
        if (schedule == null) return null;
        return schedule.getFormattedDateTime();
    }

    public LocalDateTime getTanggalBatas() {
        if (this.schedule == null || this.schedule.getDate() == null || this.schedule.getTime() == null) {
            return null;
        }
        return LocalDateTime.of(this.schedule.getDate(), this.schedule.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return activityId == activity.activityId && userId == activity.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, userId);
    }
}