package com.example.dao;

import com.example.model.Kategori;
import com.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel kategori.
 */
public class KategoriDAO {

    /**
     * Ambil semua kategori.
     */
    public List<Kategori> findAll() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY nama_kategori";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Kategori(rs.getInt("id"), rs.getString("nama_kategori")));
            }
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] FindAll error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Tambah kategori baru.
     */
    public boolean create(String namaKategori) {
        String sql = "INSERT INTO kategori (nama_kategori) VALUES (?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] Create error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Hapus kategori berdasarkan ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM kategori WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] Delete error: " + e.getMessage());
        }
        return false;
    }
}
