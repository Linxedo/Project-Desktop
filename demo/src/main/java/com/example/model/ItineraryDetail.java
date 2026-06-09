package com.example.model;

import java.sql.Time;

/**
 * Model class untuk tabel itinerary_detail.
 */
public class ItineraryDetail {

    private int id;
    private int itineraryId;
    private int destinasiId;
    private Time waktuKunjungan;
    private int urutan;

    // Dari JOIN
    private String namaWisata;
    private double hargaTiket;

    public ItineraryDetail() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItineraryId() { return itineraryId; }
    public void setItineraryId(int itineraryId) { this.itineraryId = itineraryId; }

    public int getDestinasiId() { return destinasiId; }
    public void setDestinasiId(int destinasiId) { this.destinasiId = destinasiId; }

    public Time getWaktuKunjungan() { return waktuKunjungan; }
    public void setWaktuKunjungan(Time waktuKunjungan) { this.waktuKunjungan = waktuKunjungan; }

    public int getUrutan() { return urutan; }
    public void setUrutan(int urutan) { this.urutan = urutan; }

    public String getNamaWisata() { return namaWisata; }
    public void setNamaWisata(String namaWisata) { this.namaWisata = namaWisata; }

    public double getHargaTiket() { return hargaTiket; }
    public void setHargaTiket(double hargaTiket) { this.hargaTiket = hargaTiket; }

    public String getFormattedWaktu() {
        return waktuKunjungan != null ? waktuKunjungan.toString().substring(0, 5) : "-";
    }

    @Override
    public String toString() {
        return urutan + ". " + namaWisata + " (" + getFormattedWaktu() + ")";
    }
}
