package com.example.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class untuk tabel itinerary.
 */
public class Itinerary {

    private int id;
    private int userId;
    private String namaRencana;
    private Date tglRencana;
    private double totalEstimasiBiaya;
    private Timestamp createdAt;

    // Relasi
    private List<ItineraryDetail> details = new ArrayList<>();

    public Itinerary() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNamaRencana() { return namaRencana; }
    public void setNamaRencana(String namaRencana) { this.namaRencana = namaRencana; }

    public Date getTglRencana() { return tglRencana; }
    public void setTglRencana(Date tglRencana) { this.tglRencana = tglRencana; }

    public double getTotalEstimasiBiaya() { return totalEstimasiBiaya; }
    public void setTotalEstimasiBiaya(double totalEstimasiBiaya) { this.totalEstimasiBiaya = totalEstimasiBiaya; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<ItineraryDetail> getDetails() { return details; }
    public void setDetails(List<ItineraryDetail> details) { this.details = details; }

    public String getFormattedBiaya() {
        return String.format("Rp %,.0f", totalEstimasiBiaya);
    }

    @Override
    public String toString() {
        return namaRencana + " (" + tglRencana + ")";
    }
}
