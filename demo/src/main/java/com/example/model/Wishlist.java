package com.example.model;

import java.sql.Timestamp;

/**
 * Model class untuk tabel wishlist.
 */
public class Wishlist {

    private int id;
    private int userId;
    private int destinasiId;
    private Timestamp createdAt;

    // Dari JOIN
    private String namaWisata;
    private String kategoriNama;
    private double hargaTiket;
    private double ratingRataRata;
    private String imagePath;
    private String alamat;

    public Wishlist() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDestinasiId() { return destinasiId; }
    public void setDestinasiId(int destinasiId) { this.destinasiId = destinasiId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getNamaWisata() { return namaWisata; }
    public void setNamaWisata(String namaWisata) { this.namaWisata = namaWisata; }

    public String getKategoriNama() { return kategoriNama; }
    public void setKategoriNama(String kategoriNama) { this.kategoriNama = kategoriNama; }

    public double getHargaTiket() { return hargaTiket; }
    public void setHargaTiket(double hargaTiket) { this.hargaTiket = hargaTiket; }

    public double getRatingRataRata() { return ratingRataRata; }
    public void setRatingRataRata(double ratingRataRata) { this.ratingRataRata = ratingRataRata; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
}
