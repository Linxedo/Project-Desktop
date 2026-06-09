package com.example.dao;

import com.example.model.Ulasan;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel ulasan.
 */
public class UlasanDAO {

    /**
     * Tambah ulasan baru.
     */
    public boolean create(Ulasan ulasan) {
        String sql = "INSERT INTO ulasan (user_id, destinasi_id, rating, komentar) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ulasan.getUserId());
            ps.setInt(2, ulasan.getDestinasiId());
            ps.setInt(3, ulasan.getRating());
            ps.setString(4, ulasan.getKomentar());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UlasanDAO] Create error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ambil ulasan berdasarkan destinasi.
     */
    public List<Ulasan> findByDestinasi(int destinasiId) {
        List<Ulasan> list = new ArrayList<>();
        String sql = "SELECT ul.*, u.username " +
                     "FROM ulasan ul JOIN users u ON ul.user_id = u.id " +
                     "WHERE ul.destinasi_id = ? ORDER BY ul.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, destinasiId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[UlasanDAO] FindByDestinasi error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Ambil ulasan untuk destinasi milik pengelola tertentu.
     */
    public List<Ulasan> findByPengelola(int pengelolaId) {
        List<Ulasan> list = new ArrayList<>();
        String sql = "SELECT ul.*, u.username, d.nama_wisata " +
                     "FROM ulasan ul " +
                     "JOIN users u ON ul.user_id = u.id " +
                     "JOIN destinasi d ON ul.destinasi_id = d.id " +
                     "WHERE d.pengelola_id = ? ORDER BY ul.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pengelolaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[UlasanDAO] FindByPengelola error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Cek apakah user sudah memberi ulasan untuk destinasi tertentu.
     */
    public boolean hasReviewed(int userId, int destinasiId) {
        String sql = "SELECT COUNT(*) FROM ulasan WHERE user_id = ? AND destinasi_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, destinasiId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[UlasanDAO] HasReviewed error: " + e.getMessage());
        }
        return false;
    }

    private Ulasan mapResultSet(ResultSet rs) throws SQLException {
        Ulasan ul = new Ulasan();
        ul.setId(rs.getInt("id"));
        ul.setUserId(rs.getInt("user_id"));
        ul.setDestinasiId(rs.getInt("destinasi_id"));
        ul.setRating(rs.getInt("rating"));
        ul.setKomentar(rs.getString("komentar"));
        ul.setCreatedAt(rs.getTimestamp("created_at"));
        try { ul.setUsername(rs.getString("username")); } catch (SQLException ignored) {}
        try { ul.setNamaWisata(rs.getString("nama_wisata")); } catch (SQLException ignored) {}
        return ul;
    }
}
