package com.example.dao;

import com.example.model.Booking;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel booking.
 */
public class BookingDAO {

    /**
     * Buat booking baru.
     */
    public boolean create(Booking b) {
        String sql = "INSERT INTO booking (user_id, destinasi_id, tgl_kunjungan, jumlah_tiket, total_harga, status) " +
                     "VALUES (?, ?, ?, ?, ?, 'Pending')";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getDestinasiId());
            ps.setDate(3, b.getTglKunjungan());
            ps.setInt(4, b.getJumlahTiket());
            ps.setDouble(5, b.getTotalHarga());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] Create error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ambil booking berdasarkan user ID (riwayat booking).
     */
    public List<Booking> findByUser(int userId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, d.nama_wisata " +
                     "FROM booking b " +
                     "JOIN users u ON b.user_id = u.id " +
                     "JOIN destinasi d ON b.destinasi_id = d.id " +
                     "WHERE b.user_id = ? ORDER BY b.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] FindByUser error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Ambil booking berdasarkan destinasi pengelola (untuk verifikasi).
     */
    public List<Booking> findByPengelola(int pengelolaId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, d.nama_wisata " +
                     "FROM booking b " +
                     "JOIN users u ON b.user_id = u.id " +
                     "JOIN destinasi d ON b.destinasi_id = d.id " +
                     "WHERE d.pengelola_id = ? ORDER BY b.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pengelolaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] FindByPengelola error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Ambil semua booking (untuk Admin monitoring).
     */
    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.username, d.nama_wisata " +
                     "FROM booking b " +
                     "JOIN users u ON b.user_id = u.id " +
                     "JOIN destinasi d ON b.destinasi_id = d.id " +
                     "ORDER BY b.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] FindAll error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Update status booking (Terverifikasi / Ditolak).
     */
    public boolean updateStatus(int bookingId, String status) {
        String sql = "UPDATE booking SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] UpdateStatus error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Hitung total booking.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM booking";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[BookingDAO] CountAll error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Hitung booking berdasarkan status.
     */
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM booking WHERE status = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[BookingDAO] CountByStatus error: " + e.getMessage());
        }
        return 0;
    }

    private Booking mapResultSet(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setDestinasiId(rs.getInt("destinasi_id"));
        b.setTglKunjungan(rs.getDate("tgl_kunjungan"));
        b.setJumlahTiket(rs.getInt("jumlah_tiket"));
        b.setTotalHarga(rs.getDouble("total_harga"));
        b.setStatus(rs.getString("status"));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            b.setUsername(rs.getString("username"));
            b.setNamaWisata(rs.getString("nama_wisata"));
        } catch (SQLException ignored) {}
        return b;
    }
}
