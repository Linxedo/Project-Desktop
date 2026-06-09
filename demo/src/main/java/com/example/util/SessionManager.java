package com.example.util;

import com.example.model.User;

/**
 * Singleton untuk menyimpan sesi pengguna yang sedang login.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set user yang sedang login.
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Logout — hapus data sesi.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Cek apakah ada user yang login.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Dapatkan user yang sedang login.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Dapatkan ID user yang login.
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    /**
     * Dapatkan role user yang login.
     */
    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : "Guest";
    }

    /**
     * Cek apakah user saat ini adalah Admin.
     */
    public boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole());
    }

    /**
     * Cek apakah user saat ini adalah Pengelola.
     */
    public boolean isPengelola() {
        return currentUser != null && "Pengelola".equals(currentUser.getRole());
    }

    /**
     * Cek apakah user saat ini adalah Wisatawan.
     */
    public boolean isWisatawan() {
        return currentUser != null && "Wisatawan".equals(currentUser.getRole());
    }
}
