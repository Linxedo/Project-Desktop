package com.example.controller;

import com.example.dao.UlasanDAO;
import com.example.model.Destinasi;
import com.example.model.Ulasan;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class UlasanFormController {

    public static Destinasi destinasi;

    @FXML private Label lblDestinasi;
    @FXML private HBox starContainer;
    @FXML private Label lblRatingValue;
    @FXML private TextArea txtKomentar;

    private UlasanDAO ulasanDAO = new UlasanDAO();
    private int selectedRating = 0;

    @FXML
    public void initialize() {
        if (destinasi == null) {
            handleCancel();
            return;
        }

        lblDestinasi.setText("Untuk Destinasi: " + destinasi.getNamaWisata());
        initStars();
    }

    private void initStars() {
        starContainer.getChildren().clear();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            Label star = new Label("☆");
            star.getStyleClass().add("star-button");
            
            star.setOnMouseEntered(e -> highlightStars(rating));
            star.setOnMouseExited(e -> highlightStars(selectedRating));
            star.setOnMouseClicked(e -> {
                selectedRating = rating;
                lblRatingValue.setText(selectedRating + " / 5");
            });

            starContainer.getChildren().add(star);
        }
        highlightStars(selectedRating);
    }

    private void highlightStars(int count) {
        for (int i = 0; i < 5; i++) {
            Label star = (Label) starContainer.getChildren().get(i);
            if (i < count) {
                star.setText("★");
                star.setStyle("-fx-text-fill: #FFD700;");
            } else {
                star.setText("☆");
                star.setStyle("-fx-text-fill: #444444;");
            }
        }
    }

    @FXML
    private void handleSubmit() {
        if (selectedRating == 0) {
            AlertHelper.showError("Error", "Silakan pilih rating bintang terlebih dahulu.");
            return;
        }

        String komentar = txtKomentar.getText();
        if (komentar == null || komentar.trim().isEmpty()) {
            AlertHelper.showError("Error", "Komentar tidak boleh kosong.");
            return;
        }

        Ulasan ulasan = new Ulasan();
        ulasan.setUserId(SessionManager.getInstance().getCurrentUserId());
        ulasan.setDestinasiId(destinasi.getId());
        ulasan.setRating(selectedRating);
        ulasan.setKomentar(komentar);

        if (ulasanDAO.create(ulasan)) {
            AlertHelper.showSuccess("Sukses", "Terima kasih atas ulasan Anda!");
            handleCancel();
        } else {
            AlertHelper.showError("Gagal", "Gagal menyimpan ulasan.");
        }
    }

    @FXML
    private void handleCancel() {
        MainLayoutController.getInstance().loadContent("detail_destinasi");
    }
}
