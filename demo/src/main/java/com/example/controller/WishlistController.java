package com.example.controller;

import com.example.dao.WishlistDAO;
import com.example.model.Destinasi;
import com.example.model.Wishlist;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class WishlistController {

    @FXML private FlowPane cardContainer;
    @FXML private VBox emptyState;

    private WishlistDAO wishlistDAO = new WishlistDAO();

    @FXML
    public void initialize() {
        loadWishlist();
    }

    private void loadWishlist() {
        cardContainer.getChildren().clear();
        int userId = SessionManager.getInstance().getCurrentUserId();
        List<Wishlist> list = wishlistDAO.findByUser(userId);

        if (list.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);

            for (Wishlist w : list) {
                cardContainer.getChildren().add(createWishlistCard(w));
            }
        }
    }

    private VBox createWishlistCard(Wishlist w) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setPrefWidth(260);

        javafx.scene.Node imageNode;
        if (w.getFirstImagePath() != null && !w.getFirstImagePath().equals("placeholder.jpg")) {
            java.io.File file = new java.io.File("uploads/" + w.getFirstImagePath());
            if (file.exists()) {
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(new javafx.scene.image.Image(file.toURI().toString()));
                imgView.setFitWidth(260);
                imgView.setFitHeight(140);
                javafx.scene.layout.StackPane sp = new javafx.scene.layout.StackPane(imgView);
                sp.setStyle("-fx-background-radius: 12 12 0 0; -fx-background-color: #2A2A2A;");
                imageNode = sp;
            } else {
                imageNode = createPlaceholder(w.getNamaWisata());
            }
        } else {
            imageNode = createPlaceholder(w.getNamaWisata());
        }

        VBox body = new VBox(6);
        body.getStyleClass().add("card-body");

        Label lblKategori = new Label(w.getKategoriNama());
        lblKategori.getStyleClass().add("card-category");

        Label lblNama = new Label(w.getNamaWisata());
        lblNama.getStyleClass().add("card-title");

        Button btnRemove = new Button("Hapus");
        btnRemove.getStyleClass().add("btn-outline");
        btnRemove.setStyle("-fx-padding: 4 12; -fx-font-size: 11px;");
        btnRemove.setOnAction(e -> {
            wishlistDAO.remove(SessionManager.getInstance().getCurrentUserId(), w.getDestinasiId());
            loadWishlist(); // Reload
        });

        HBox footer = new HBox(btnRemove);
        footer.setAlignment(Pos.CENTER_RIGHT);

        body.getChildren().addAll(lblKategori, lblNama, footer);
        card.getChildren().addAll(imageNode, body);

        // Click on image/name to view detail
        card.setOnMouseClicked(e -> {
            // Need to convert Wishlist to Destinasi for detail view
            Destinasi d = new Destinasi();
            d.setId(w.getDestinasiId());
            d.setNamaWisata(w.getNamaWisata());
            d.setKategoriNama(w.getKategoriNama());
            d.setRatingRataRata(w.getRatingRataRata());
            d.setHargaTiket(w.getHargaTiket());
            d.setAlamat(w.getAlamat());
            d.setImagePath(w.getImagePath());
            DetailDestinasiController.selectedDestinasi = d;
            MainLayoutController.getInstance().loadContent("detail_destinasi");
        });

        return card;
    }

    @FXML
    private void handleExplore() {
        MainLayoutController.getInstance().loadContent("dashboard_home");
    }

    private VBox createPlaceholder(String name) {
        VBox imgPlaceholder = new VBox();
        imgPlaceholder.setPrefHeight(140);
        imgPlaceholder.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 12 12 0 0;");
        imgPlaceholder.setAlignment(Pos.CENTER);
        imgPlaceholder.getChildren().add(new Label("🖼️ " + name));
        return imgPlaceholder;
    }
}
