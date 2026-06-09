package com.example.controller;

import com.example.App;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainLayoutController {

    @FXML private VBox sidebar;
    @FXML private VBox menuContainer;
    @FXML private TextField txtSearch;
    @FXML private Label lblUsername;
    @FXML private Label lblRole;
    @FXML private StackPane contentArea;

    private static MainLayoutController instance;

    public MainLayoutController() {
        instance = this;
    }

    public static MainLayoutController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        updateUserInfo();
        loadMenus();
        loadContent("dashboard_home"); // Default view
    }

    private void updateUserInfo() {
        if (SessionManager.getInstance().isLoggedIn()) {
            lblUsername.setText(SessionManager.getInstance().getCurrentUser().getUsername());
            lblRole.setText(SessionManager.getInstance().getCurrentRole());
        } else {
            lblUsername.setText("Guest");
            lblRole.setText("Tamu");
        }
    }

    private void loadMenus() {
        menuContainer.getChildren().clear();

        // All Roles
        addMenu("🏠  Beranda", "dashboard_home");

        if (SessionManager.getInstance().isLoggedIn()) {
            if (SessionManager.getInstance().isWisatawan()) {
                addMenu("🗓️  Itinerary", "itinerary");
                addMenu("❤️  Wishlist", "wishlist");
                addMenu("📋  Riwayat Booking", "booking_history");
            } else if (SessionManager.getInstance().isPengelola()) {
                addMenu("🏞️  Kelola Destinasi", "kelola_destinasi");
                addMenu("✅  Verifikasi Booking", "verifikasi_booking");
                addMenu("📊  Insight Ulasan", "pengelola_insight");
            } else if (SessionManager.getInstance().isAdmin()) {
                addMenu("📈  Monitoring", "admin_monitoring");
                addMenu("👥  Kelola User", "admin_users");
                addMenu("🏷️  Kelola Kategori", "admin_kategori");
            }
        }
    }

    private void addMenu(String title, String fxml) {
        Button btn = new Button(title);
        btn.getStyleClass().add("sidebar-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> loadContent(fxml));
        menuContainer.getChildren().add(btn);
    }

    public void loadContent(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/" + fxml + ".fxml"));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Highlight active menu (simple visual logic)
            // Can be enhanced by storing active button state
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat view: " + fxml);
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText();
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Need to pass search keyword to DashboardHomeController.
            // A simple way is to load dashboard and let it handle its own state,
            // or pass via a static/shared context. For simplicity, we just load home.
            // A robust way: Set keyword in a static variable, then load dashboard_home.
            DashboardHomeController.pendingSearchKeyword = keyword;
            loadContent("dashboard_home");
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
