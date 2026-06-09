package com.example.dao;

import com.example.model.Wishlist;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel wishlist.
 */
public class WishlistDAO {

    /**
     * Tambah destinasi ke wishlist.
     */
    public boolean add(int userId, int destinasiId) {
        String sql = "INSERT INTO wishlist (user_id, destinasi_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, destinasiId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[WishlistDAO] Add error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Hapus destinasi dari wishlist.
     */
    public boolean remove(int userId, int destinasiId) {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND destinasi_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, destinasiId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[WishlistDAO] Remove error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cek apakah destinasi ada di wishlist user.
     */
    public boolean isWishlisted(int userId, int destinasiId) {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE user_id = ? AND destinasi_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, destinasiId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[WishlistDAO] IsWishlisted error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Ambil semua wishlist milik user dengan data destinasi (JOIN).
     */
    public List<Wishlist> findByUser(int userId) {
        List<Wishlist> list = new ArrayList<>();
        String sql = "SELECT w.*, d.nama_wisata, d.harga_tiket, d.rating_rata_rata, " +
                     "d.image_path, d.alamat, k.nama_kategori " +
                     "FROM wishlist w " +
                     "JOIN destinasi d ON w.destinasi_id = d.id " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "WHERE w.user_id = ? ORDER BY w.created_at DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Wishlist w = new Wishlist();
                w.setId(rs.getInt("id"));
                w.setUserId(rs.getInt("user_id"));
                w.setDestinasiId(rs.getInt("destinasi_id"));
                w.setCreatedAt(rs.getTimestamp("created_at"));
                w.setNamaWisata(rs.getString("nama_wisata"));
                w.setHargaTiket(rs.getDouble("harga_tiket"));
                w.setRatingRataRata(rs.getDouble("rating_rata_rata"));
                w.setImagePath(rs.getString("image_path"));
                w.setAlamat(rs.getString("alamat"));
                try { w.setKategoriNama(rs.getString("nama_kategori")); } catch (SQLException ignored) {}
                list.add(w);
            }
        } catch (SQLException e) {
            System.err.println("[WishlistDAO] FindByUser error: " + e.getMessage());
        }
        return list;
    }
}
