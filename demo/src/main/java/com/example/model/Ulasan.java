package com.example.model;

import java.sql.Timestamp;

/**
 * Model class untuk tabel ulasan.
 */
public class Ulasan {

    private int id;
    private int userId;
    private int destinasiId;
    private int rating; // 1-5
    private String komentar;
    private Timestamp createdAt;

    // Dari JOIN
    private String username;
    private String namaWisata;

    public Ulasan() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDestinasiId() { return destinasiId; }
    public void setDestinasiId(int destinasiId) { this.destinasiId = destinasiId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getKomentar() { return komentar; }
    public void setKomentar(String komentar) { this.komentar = komentar; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNamaWisata() { return namaWisata; }
    public void setNamaWisata(String namaWisata) { this.namaWisata = namaWisata; }

    /**
     * Format rating sebagai bintang.
     */
    public String getRatingStars() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rating; i++) sb.append("★");
        for (int i = rating; i < 5; i++) sb.append("☆");
        return sb.toString();
    }

    @Override
    public String toString() {
        return username + ": " + getRatingStars() + " - " + komentar;
    }
}
