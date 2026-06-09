package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern untuk koneksi PostgreSQL.
 * Menjamin hanya satu instance koneksi yang aktif.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Konfigurasi Database
    private static final String URL = "jdbc:postgresql://localhost:5432/wisata_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

    /**
     * Private constructor — Singleton Pattern.
     * Membuat koneksi ke database PostgreSQL.
     */
    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[DB] Koneksi ke database berhasil!");
            // Migration for ItineraryDetail: Change waktu_kunjungan to tanggal_kunjungan
            try (java.sql.Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE itinerary_detail ADD COLUMN IF NOT EXISTS tanggal_kunjungan DATE");
            } catch (SQLException e) {
                System.out.println("Column tanggal_kunjungan already exists or could not be added.");
            }
            // Auto-migration
            try (java.sql.Statement stmt = connection.createStatement()) {
                stmt.execute("ALTER TABLE destinasi ADD COLUMN IF NOT EXISTS peta_lokasi TEXT;");
                System.out.println("[DB] Auto-migration untuk peta_lokasi berhasil.");
            } catch (SQLException ex) {
                System.err.println("[DB ERROR] Gagal melakukan auto-migration: " + ex.getMessage());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] PostgreSQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Gagal terhubung ke database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan instance Singleton dari DatabaseConnection.
     * Jika koneksi null atau sudah ditutup, buat koneksi baru.
     */
    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.connection == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Error memeriksa status koneksi: " + e.getMessage());
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Mendapatkan objek Connection untuk operasi database.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Koneksi database dibuat ulang.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Gagal membuat ulang koneksi: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Menutup koneksi database.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
