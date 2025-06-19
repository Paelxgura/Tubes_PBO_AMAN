// File: Schedule.java (FINAL v2)
package tubes.backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Schedule {
    private int scheduleId; // Diubah dari id
    private LocalDate date;
    private LocalTime time;

    public Schedule(int scheduleId, LocalDate date, LocalTime time) { // Diubah dari id
        this.scheduleId = scheduleId; // Diubah dari id
        this.date = date;
        this.time = time;
    }

    public Schedule(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    // --- Getters & Setters Disesuaikan ---
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    // Getters & Setters lain (tidak berubah)
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getFormattedDateTime() {
        if (date == null || time == null) return "N/A";
        LocalDateTime dateTime = date.atTime(time);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm E, dd MMM yy");
        return dateTime.format(formatter);
    }
}