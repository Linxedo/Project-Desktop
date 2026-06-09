package com.example.model;

import java.sql.Time;

/**
 * Model class untuk tabel itinerary_detail.
 */
public class ItineraryDetail {

    private int id;
    private int itineraryId;
    private int destinasiId;
    private java.sql.Date tanggalKunjungan;
    private int urutan;

    // Additional fields for View
    private String namaDestinasi;
    private String kategoriDestinasi;
    private String namaWisata;
    private double hargaTiket;

    public ItineraryDetail() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItineraryId() { return itineraryId; }
    public void setItineraryId(int itineraryId) { this.itineraryId = itineraryId; }

    public int getDestinasiId() { return destinasiId; }
    public void setDestinasiId(int destinasiId) { this.destinasiId = destinasiId; }

    public java.sql.Date getTanggalKunjungan() { return tanggalKunjungan; }
    public void setTanggalKunjungan(java.sql.Date tanggalKunjungan) { this.tanggalKunjungan = tanggalKunjungan; }

    public int getUrutan() { return urutan; }
    public void setUrutan(int urutan) { this.urutan = urutan; }

    public String getNamaDestinasi() { return namaDestinasi; }
    public void setNamaDestinasi(String namaDestinasi) { this.namaDestinasi = namaDestinasi; }

    public String getKategoriDestinasi() { return kategoriDestinasi; }
    public void setKategoriDestinasi(String kategoriDestinasi) { this.kategoriDestinasi = kategoriDestinasi; }

    public String getNamaWisata() { return namaWisata; }
    public void setNamaWisata(String namaWisata) { this.namaWisata = namaWisata; }

    public double getHargaTiket() { return hargaTiket; }
    public void setHargaTiket(double hargaTiket) { this.hargaTiket = hargaTiket; }

    // Helper for table view
    public String getFormattedWaktu() {
        return tanggalKunjungan != null ? tanggalKunjungan.toString() : "-";
    }

    @Override
    public String toString() {
        return urutan + ". " + namaWisata + " (" + getFormattedWaktu() + ")";
    }
}
