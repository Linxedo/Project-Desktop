package com.example.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Optional;

/**
 * Utility class untuk menampilkan Alert dialog dengan tema dark mode.
 */
public class AlertHelper {

    /**
     * Tampilkan alert sukses.
     */
    public static void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Tampilkan alert error.
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Tampilkan alert warning.
     */
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    /**
     * Tampilkan alert info.
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Tampilkan dialog konfirmasi.
     * @return true jika user klik OK, false jika Cancel.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Method utama untuk menampilkan alert.
     */
    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Menerapkan dark mode styling ke dialog.
     */
    private static void styleAlert(Alert alert) {
        try {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle(
                "-fx-background-color: #1E1E1E;" +
                "-fx-border-color: #333333;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
            );
            dialogPane.lookup(".content.label").setStyle(
                "-fx-text-fill: #E0E0E0; -fx-font-size: 14px;"
            );
        } catch (Exception e) {
            // Jika styling gagal, tetap tampilkan alert default
        }
    }
}
