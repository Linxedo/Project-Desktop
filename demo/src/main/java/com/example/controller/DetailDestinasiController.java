package com.example.controller;

import com.example.dao.UlasanDAO;
import com.example.dao.WishlistDAO;
import com.example.model.Destinasi;
import com.example.model.Ulasan;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class DetailDestinasiController {

    public static Destinasi selectedDestinasi;

    @FXML private Label lblNama;
    @FXML private Label lblKategori;
    @FXML private Label lblRating;
    @FXML private Label lblHarga;
    @FXML private Label lblAlamat;
    @FXML private Label lblJam;
    @FXML private Label lblPengelola;
    @FXML private Label lblDeskripsi;
    @FXML private Label lblMap;

    @FXML private HBox actionButtons;
    @FXML private Button btnBooking;
    @FXML private Button btnWishlist;
    @FXML private Button btnReview;

    @FXML private VBox reviewContainer;
    @FXML private Label lblNoReviews;

    private UlasanDAO ulasanDAO = new UlasanDAO();
    private WishlistDAO wishlistDAO = new WishlistDAO();

    @FXML
    public void initialize() {
        if (selectedDestinasi == null) {
            handleBack();
            return;
        }

        populateData();
        setupButtons();
        loadReviews();
    }

    private void populateData() {
        lblNama.setText(selectedDestinasi.getNamaWisata());
        lblKategori.setText(selectedDestinasi.getKategoriNama());
        lblRating.setText(selectedDestinasi.getRatingStars());
        lblHarga.setText(selectedDestinasi.getFormattedHarga());
        lblAlamat.setText(selectedDestinasi.getAlamat());
        lblJam.setText(selectedDestinasi.getJamOperasional());
        lblPengelola.setText(selectedDestinasi.getPengelolaNama() != null ? selectedDestinasi.getPengelolaNama() : "Admin");
        lblDeskripsi.setText(selectedDestinasi.getDeskripsi());
        lblMap.setText("📍 Peta Lokasi: " + selectedDestinasi.getNamaWisata());
    }

    private void setupButtons() {
        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();
        boolean isWisatawan = SessionManager.getInstance().isWisatawan();

        btnBooking.setVisible(isWisatawan);
        btnBooking.setManaged(isWisatawan);
        
        btnWishlist.setVisible(isWisatawan);
        btnWishlist.setManaged(isWisatawan);

        btnReview.setVisible(isWisatawan);
        btnReview.setManaged(isWisatawan);

        if (isWisatawan) {
            int userId = SessionManager.getInstance().getCurrentUserId();
            if (wishlistDAO.isWishlisted(userId, selectedDestinasi.getId())) {
                btnWishlist.setText("❤️ Hapus Wishlist");
            } else {
                btnWishlist.setText("♡ Tambah Wishlist");
            }
        }
    }

    private void loadReviews() {
        reviewContainer.getChildren().clear();
        List<Ulasan> ulasanList = ulasanDAO.findByDestinasi(selectedDestinasi.getId());

        if (ulasanList.isEmpty()) {
            lblNoReviews.setVisible(true);
            lblNoReviews.setManaged(true);
        } else {
            lblNoReviews.setVisible(false);
            lblNoReviews.setManaged(false);

            for (Ulasan u : ulasanList) {
                VBox card = new VBox(4);
                card.setStyle("-fx-background-color: #2A2A2A; -fx-padding: 12; -fx-background-radius: 8;");
                
                HBox header = new HBox(8);
                Label lblUser = new Label(u.getUsername());
                lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
                Label lblStars = new Label(u.getRatingStars());
                lblStars.setStyle("-fx-text-fill: #FFD700;");
                header.getChildren().addAll(lblUser, lblStars);

                Label lblComment = new Label(u.getKomentar());
                lblComment.setWrapText(true);
                lblComment.setStyle("-fx-text-fill: #E0E0E0;");

                card.getChildren().addAll(header, lblComment);
                reviewContainer.getChildren().add(card);
            }
        }
    }

    @FXML
    private void handleBack() {
        MainLayoutController.getInstance().loadContent("dashboard_home");
    }

    @FXML
    private void handleBooking() {
        BookingFormController.destinasi = selectedDestinasi;
        MainLayoutController.getInstance().loadContent("booking_form");
    }

    @FXML
    private void handleWishlist() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        int destId = selectedDestinasi.getId();

        if (wishlistDAO.isWishlisted(userId, destId)) {
            wishlistDAO.remove(userId, destId);
            btnWishlist.setText("♡ Tambah Wishlist");
            AlertHelper.showSuccess("Wishlist", "Berhasil dihapus dari wishlist.");
        } else {
            wishlistDAO.add(userId, destId);
            btnWishlist.setText("❤️ Hapus Wishlist");
            AlertHelper.showSuccess("Wishlist", "Berhasil ditambahkan ke wishlist.");
        }
    }

    @FXML
    private void handleReview() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (ulasanDAO.hasReviewed(userId, selectedDestinasi.getId())) {
            AlertHelper.showWarning("Perhatian", "Anda sudah memberikan ulasan untuk destinasi ini.");
            return;
        }

        UlasanFormController.destinasi = selectedDestinasi;
        MainLayoutController.getInstance().loadContent("ulasan_form");
    }
}
