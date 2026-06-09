package com.example.dao;

import com.example.model.Itinerary;
import com.example.model.ItineraryDetail;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel itinerary dan itinerary_detail.
 */
public class ItineraryDAO {

    // ==================== ITINERARY ====================

    /**
     * Buat itinerary baru dan kembalikan ID-nya.
     */
    public int create(Itinerary it) {
        String sql = "INSERT INTO itinerary (user_id, nama_rencana, tgl_rencana) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, it.getUserId());
            ps.setString(2, it.getNamaRencana());
            ps.setDate(3, it.getTglRencana());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("[ItineraryDAO] Create error: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Ambil semua itinerary milik user.
     */
    public List<Itinerary> findByUser(int userId) {
        List<Itinerary> list = new ArrayList<>();
        String sql = "SELECT * FROM itinerary WHERE user_id = ? ORDER BY tgl_rencana DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapItinerary(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ItineraryDAO] FindByUser error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Hapus itinerary (cascade hapus detail juga).
     */
    public boolean delete(int itineraryId) {
        String sql = "DELETE FROM itinerary WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itineraryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ItineraryDAO] Delete error: " + e.getMessage());
        }
        return false;
    }

    // ==================== ITINERARY DETAIL ====================

    /**
     * Tambah detail ke itinerary.
     */
    public boolean addDetail(ItineraryDetail detail) {
        String sql = "INSERT INTO itinerary_detail (itinerary_id, destinasi_id, waktu_kunjungan, urutan) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getItineraryId());
            ps.setInt(2, detail.getDestinasiId());
            ps.setTime(3, detail.getWaktuKunjungan());
            ps.setInt(4, detail.getUrutan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ItineraryDAO] AddDetail error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ambil detail itinerary dengan nama destinasi (JOIN).
     */
    public List<ItineraryDetail> findDetailsByItinerary(int itineraryId) {
        List<ItineraryDetail> list = new ArrayList<>();
        String sql = "SELECT id2.*, d.nama_wisata, d.harga_tiket " +
                     "FROM itinerary_detail id2 " +
                     "JOIN destinasi d ON id2.destinasi_id = d.id " +
                     "WHERE id2.itinerary_id = ? ORDER BY id2.urutan";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itineraryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItineraryDetail detail = new ItineraryDetail();
                detail.setId(rs.getInt("id"));
                detail.setItineraryId(rs.getInt("itinerary_id"));
                detail.setDestinasiId(rs.getInt("destinasi_id"));
                detail.setWaktuKunjungan(rs.getTime("waktu_kunjungan"));
                detail.setUrutan(rs.getInt("urutan"));
                detail.setNamaWisata(rs.getString("nama_wisata"));
                detail.setHargaTiket(rs.getDouble("harga_tiket"));
                list.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("[ItineraryDAO] FindDetails error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Hapus detail dari itinerary.
     */
    public boolean deleteDetail(int detailId) {
        String sql = "DELETE FROM itinerary_detail WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ItineraryDAO] DeleteDetail error: " + e.getMessage());
        }
        return false;
    }

    private Itinerary mapItinerary(ResultSet rs) throws SQLException {
        Itinerary it = new Itinerary();
        it.setId(rs.getInt("id"));
        it.setUserId(rs.getInt("user_id"));
        it.setNamaRencana(rs.getString("nama_rencana"));
        it.setTglRencana(rs.getDate("tgl_rencana"));
        it.setTotalEstimasiBiaya(rs.getDouble("total_estimasi_biaya"));
        it.setCreatedAt(rs.getTimestamp("created_at"));
        return it;
    }
}
