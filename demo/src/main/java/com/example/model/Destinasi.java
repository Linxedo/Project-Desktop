package com.example.model;

import java.sql.Timestamp;

/**
 * Model class untuk tabel destinasi.
 */
public class Destinasi {

    private int id;
    private String namaWisata;
    private int kategoriId;
    private String kategoriNama; // Dari JOIN
    private String alamat;
    private String deskripsi;
    private double hargaTiket;
    private String jamOperasional;
    private double ratingRataRata;
    private String imagePath;
    private int pengelolaId;
    private String pengelolaNama; // Dari JOIN
    private String petaLokasi;
    private Timestamp createdAt;

    public Destinasi() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPetaLokasi() { return petaLokasi; }
    public void setPetaLokasi(String petaLokasi) { this.petaLokasi = petaLokasi; }

    public String getNamaWisata() { return namaWisata; }
    public void setNamaWisata(String namaWisata) { this.namaWisata = namaWisata; }

    public int getKategoriId() { return kategoriId; }
    public void setKategoriId(int kategoriId) { this.kategoriId = kategoriId; }

    public String getKategoriNama() { return kategoriNama; }
    public void setKategoriNama(String kategoriNama) { this.kategoriNama = kategoriNama; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public double getHargaTiket() { return hargaTiket; }
    public void setHargaTiket(double hargaTiket) { this.hargaTiket = hargaTiket; }

    public String getJamOperasional() { return jamOperasional; }
    public void setJamOperasional(String jamOperasional) { this.jamOperasional = jamOperasional; }

    public double getRatingRataRata() { return ratingRataRata; }
    public void setRatingRataRata(double ratingRataRata) { this.ratingRataRata = ratingRataRata; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getPengelolaId() { return pengelolaId; }
    public void setPengelolaId(int pengelolaId) { this.pengelolaId = pengelolaId; }

    public String getPengelolaNama() { return pengelolaNama; }
    public void setPengelolaNama(String pengelolaNama) { this.pengelolaNama = pengelolaNama; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Format harga tiket ke format Rupiah.
     */
    public String getFormattedHarga() {
        if (hargaTiket == 0) return "Gratis";
        return String.format("Rp %,.0f", hargaTiket);
    }

    /**
     * Format rating sebagai string bintang.
     */
    public String getRatingStars() {
        int fullStars = (int) ratingRataRata;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fullStars; i++) sb.append("★");
        for (int i = fullStars; i < 5; i++) sb.append("☆");
        return sb.toString() + String.format(" %.1f", ratingRataRata);
    }

    @Override
    public String toString() {
        return namaWisata;
    }
}
