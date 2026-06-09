package com.example.dao;

import com.example.model.Destinasi;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel destinasi.
 */
public class DestinasiDAO {

    /**
     * Ambil semua destinasi dengan nama kategori (JOIN).
     */
    public List<Destinasi> findAll() {
        List<Destinasi> list = new ArrayList<>();
        String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                     "FROM destinasi d " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "LEFT JOIN users u ON d.pengelola_id = u.id " +
                     "ORDER BY d.id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] FindAll error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Ambil top destinasi berdasarkan rating tertinggi.
     */
    public List<Destinasi> getTopDestinasi(int limit) {
        List<Destinasi> list = new ArrayList<>();
        String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                     "FROM destinasi d " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "LEFT JOIN users u ON d.pengelola_id = u.id " +
                     "ORDER BY d.rating_rata_rata DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] GetTop error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Cari destinasi berdasarkan nama (LIKE search).
     */
    public List<Destinasi> findByNama(String keyword) {
        List<Destinasi> list = new ArrayList<>();
        String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                     "FROM destinasi d " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "LEFT JOIN users u ON d.pengelola_id = u.id " +
                     "WHERE LOWER(d.nama_wisata) LIKE LOWER(?) " +
                     "ORDER BY d.rating_rata_rata DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] FindByNama error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Filter destinasi berdasarkan kategori.
     */
    public List<Destinasi> findByKategori(int kategoriId) {
        List<Destinasi> list = new ArrayList<>();
        String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                     "FROM destinasi d " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "LEFT JOIN users u ON d.pengelola_id = u.id " +
                     "WHERE d.kategori_id = ? ORDER BY d.rating_rata_rata DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, kategoriId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] FindByKategori error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Ambil destinasi milik pengelola tertentu.
     */
    public List<Destinasi> findByPengelola(int pengelolaId) {
        List<Destinasi> list = new ArrayList<>();
        String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                     "FROM destinasi d " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "LEFT JOIN users u ON d.pengelola_id = u.id " +
                     "WHERE d.pengelola_id = ? ORDER BY d.id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pengelolaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] FindByPengelola error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Cari destinasi berdasarkan ID.
     */
    public Destinasi findById(int id) {
        String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                     "FROM destinasi d " +
                     "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                     "LEFT JOIN users u ON d.pengelola_id = u.id " +
                     "WHERE d.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] FindById error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Tambah destinasi baru.
     */
    public boolean create(Destinasi d) {
        String sql = "INSERT INTO destinasi (nama_wisata, kategori_id, alamat, deskripsi, harga_tiket, " +
                     "jam_operasional, image_path, pengelola_id, peta_lokasi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNamaWisata());
            ps.setInt(2, d.getKategoriId());
            ps.setString(3, d.getAlamat());
            ps.setString(4, d.getDeskripsi());
            ps.setDouble(5, d.getHargaTiket());
            ps.setString(6, d.getJamOperasional());
            ps.setString(7, d.getImagePath());
            ps.setInt(8, d.getPengelolaId());
            ps.setString(9, d.getPetaLokasi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] Create error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update destinasi.
     */
    public boolean update(Destinasi d) {
        String sql = "UPDATE destinasi SET nama_wisata = ?, kategori_id = ?, alamat = ?, deskripsi = ?, " +
                     "harga_tiket = ?, jam_operasional = ?, image_path = ?, peta_lokasi = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNamaWisata());
            ps.setInt(2, d.getKategoriId());
            ps.setString(3, d.getAlamat());
            ps.setString(4, d.getDeskripsi());
            ps.setDouble(5, d.getHargaTiket());
            ps.setString(6, d.getJamOperasional());
            ps.setString(7, d.getImagePath());
            ps.setString(8, d.getPetaLokasi());
            ps.setInt(9, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] Update error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Hapus destinasi.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM destinasi WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] Delete error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Hitung total destinasi.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM destinasi";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DestinasiDAO] CountAll error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Cari destinasi berdasarkan nama dan/atau kategori.
     */
    public List<Destinasi> search(String keyword, int kategoriId) {
        if (keyword != null && !keyword.trim().isEmpty() && kategoriId > 0) {
            List<Destinasi> list = new ArrayList<>();
            String sql = "SELECT d.*, k.nama_kategori, u.username AS pengelola_nama " +
                         "FROM destinasi d " +
                         "LEFT JOIN kategori k ON d.kategori_id = k.id " +
                         "LEFT JOIN users u ON d.pengelola_id = u.id " +
                         "WHERE LOWER(d.nama_wisata) LIKE LOWER(?) AND d.kategori_id = ? " +
                         "ORDER BY d.rating_rata_rata DESC";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + keyword + "%");
                ps.setInt(2, kategoriId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(mapResultSet(rs));
            } catch (SQLException e) {
                System.err.println("[DestinasiDAO] Search error: " + e.getMessage());
            }
            return list;
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            return findByNama(keyword);
        } else if (kategoriId > 0) {
            return findByKategori(kategoriId);
        } else {
            return findAll();
        }
    }

    private Destinasi mapResultSet(ResultSet rs) throws SQLException {
        Destinasi d = new Destinasi();
        d.setId(rs.getInt("id"));
        d.setNamaWisata(rs.getString("nama_wisata"));
        d.setKategoriId(rs.getInt("kategori_id"));
        d.setAlamat(rs.getString("alamat"));
        d.setDeskripsi(rs.getString("deskripsi"));
        d.setHargaTiket(rs.getDouble("harga_tiket"));
        d.setJamOperasional(rs.getString("jam_operasional"));
        d.setRatingRataRata(rs.getDouble("rating_rata_rata"));
        d.setImagePath(rs.getString("image_path"));
        d.setPengelolaId(rs.getInt("pengelola_id"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            d.setPetaLokasi(rs.getString("peta_lokasi"));
        } catch (SQLException ignored) {}
        try {
            d.setKategoriNama(rs.getString("nama_kategori"));
            d.setPengelolaNama(rs.getString("pengelola_nama"));
        } catch (SQLException ignored) {}
        return d;
    }
}
