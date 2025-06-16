package tubes.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Kelas ini merepresentasikan data tugas milik user pada aplikasi
public class Tugas {
    // Menyimpan id unik tugas
    private int id;
    // Menyimpan id user pemilik tugas
    private int userId;
    // Menyimpan judul tugas
    private String judul;
    // Menyimpan deskripsi tugas
    private String deskripsi;
    // Menyimpan batas waktu tugas
    private LocalDateTime tanggalBatas;
    // Menyimpan kategori tugas
    private String kategori;
    // Menyimpan lokasi tugas
    private String lokasi;
    // Menyimpan status tugas (misal: Belum Dimulai, Selesai, dll)
    private String status;

    // Konstruktor untuk mengambil data tugas dari database
    public Tugas(int id, int userId, String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi, String status) {
        this.id = id;
        this.userId = userId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tanggalBatas = tanggalBatas;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.status = status;
    }

    // Konstruktor untuk membuat tugas baru dari UI (belum memiliki id)
    public Tugas(int userId, String judul, String deskripsi, LocalDateTime tanggalBatas, String kategori, String lokasi) {
        this.userId = userId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tanggalBatas = tanggalBatas;
        this.kategori = kategori;
        this.lokasi = lokasi;
        this.status = "Belum Dimulai";
    }

    //GETTERS
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public LocalDateTime getTanggalBatas() { return tanggalBatas; }
    public String getKategori() { return kategori; }
    public String getLokasi() { return lokasi; }
    public String getStatus() { return status; }

    //SETTERS
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setJudul(String judul) { this.judul = judul; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setTanggalBatas(LocalDateTime tanggalBatas) { this.tanggalBatas = tanggalBatas; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setStatus(String status) { this.status = status; }

    // Mengembalikan batas waktu tugas dalam format string yang mudah dibaca
    public String getTanggalBatasFormatted() {
        if (tanggalBatas == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm E, dd MMM yy");
        return tanggalBatas.format(formatter);
    }

    // Membuat laporan ringkas tentang tugas ini dalam bentuk string
    public String buatLaporan() {
        return String.format("Tugas: %s\nDeskripsi: %s\nBatas Waktu: %s\nKategori: %s\nStatus: %s",
                judul,
                deskripsi == null ? "-" : deskripsi,
                getTanggalBatasFormatted(),
                kategori == null ? "-" : kategori,
                status);
    }

    @Override
    public String toString() {
        return "Tugas{" +
                "id=" + id +
                ", userId=" + userId +
                ", judul='" + judul + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tugas tugas = (Tugas) o;
        return id == tugas.id && userId == tugas.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}