package com.example.util;

import java.util.regex.Pattern;

/**
 * Utility class untuk validasi input pengguna.
 * Mencegah input tidak valid dan SQL Injection.
 */
public class ValidationHelper {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9_]{3,50}$");

    /**
     * Cek apakah string kosong atau null.
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Cek apakah string TIDAK kosong.
     */
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * Validasi format email.
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validasi username (3-50 karakter, huruf/angka/underscore).
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) return false;
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validasi password (minimal 6 karakter).
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    /**
     * Cek apakah string adalah angka positif.
     */
    public static boolean isPositiveNumber(String text) {
        if (isEmpty(text)) return false;
        try {
            double val = Double.parseDouble(text.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Cek apakah string adalah integer positif.
     */
    public static boolean isPositiveInteger(String text) {
        if (isEmpty(text)) return false;
        try {
            int val = Integer.parseInt(text.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse string ke integer dengan default value.
     */
    public static int parseIntOrDefault(String text, int defaultValue) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Parse string ke double dengan default value.
     */
    public static double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * Validasi panjang teks.
     */
    public static boolean isValidLength(String text, int min, int max) {
        if (text == null) return min == 0;
        int len = text.trim().length();
        return len >= min && len <= max;
    }

    /**
     * Validasi rating (1-5).
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}
