package com.example.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model class untuk tabel booking.
 */
public class Booking {

    private int id;
    private int userId;
    private int destinasiId;
    private Date tglKunjungan;
    private int jumlahTiket;
    private double totalHarga;
    private String status; // Pending, Terverifikasi, Ditolak
    private Timestamp createdAt;

    // Fields dari JOIN
    private String username;
    private String namaWisata;

    public Booking() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDestinasiId() { return destinasiId; }
    public void setDestinasiId(int destinasiId) { this.destinasiId = destinasiId; }

    public Date getTglKunjungan() { return tglKunjungan; }
    public void setTglKunjungan(Date tglKunjungan) { this.tglKunjungan = tglKunjungan; }

    public int getJumlahTiket() { return jumlahTiket; }
    public void setJumlahTiket(int jumlahTiket) { this.jumlahTiket = jumlahTiket; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNamaWisata() { return namaWisata; }
    public void setNamaWisata(String namaWisata) { this.namaWisata = namaWisata; }

    public String getFormattedTotalHarga() {
        return String.format("Rp %,.0f", totalHarga);
    }

    @Override
    public String toString() {
        return "Booking #" + id + " - " + namaWisata + " (" + status + ")";
    }
}
