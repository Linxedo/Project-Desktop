package com.example.controller;

import com.example.dao.DestinasiDAO;
import com.example.dao.KategoriDAO;
import com.example.model.Destinasi;
import com.example.model.Kategori;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardHomeController {

    @FXML private Label lblWelcome;
    @FXML private HBox filterContainer;
    @FXML private FlowPane cardContainer;
    @FXML private VBox emptyState;

    private DestinasiDAO destinasiDAO = new DestinasiDAO();
    private KategoriDAO kategoriDAO = new KategoriDAO();
    private int selectedKategoriId = -1;

    public static String pendingSearchKeyword = null;

    @FXML
    public void initialize() {
        loadKategoriFilters();
        
        if (pendingSearchKeyword != null && !pendingSearchKeyword.trim().isEmpty()) {
            loadDestinasi(pendingSearchKeyword, -1);
            pendingSearchKeyword = null; // Clear after use
        } else {
            loadDestinasi(null, -1);
        }
    }

    private void loadKategoriFilters() {
        filterContainer.getChildren().clear();
        
        // "Semua" button
        Button btnSemua = new Button("Semua");
        btnSemua.getStyleClass().add("filter-chip");
        btnSemua.setOnAction(e -> {
            selectedKategoriId = -1;
            loadDestinasi(null, selectedKategoriId);
        });
        filterContainer.getChildren().add(btnSemua);

        List<Kategori> kategoris = kategoriDAO.findAll();
        for (Kategori k : kategoris) {
            Button btn = new Button(k.getNamaKategori());
            btn.getStyleClass().add("filter-chip");
            btn.setOnAction(e -> {
                selectedKategoriId = k.getId();
                loadDestinasi(null, selectedKategoriId);
            });
            filterContainer.getChildren().add(btn);
        }
    }

    private void loadDestinasi(String keyword, int kategoriId) {
        cardContainer.getChildren().clear();
        
        List<Destinasi> list = destinasiDAO.search(keyword, kategoriId);

        if (list.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        } else {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            
            for (Destinasi d : list) {
                cardContainer.getChildren().add(createDestinasiCard(d));
            }
        }
    }

    private VBox createDestinasiCard(Destinasi d) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        card.setPrefWidth(260);

        javafx.scene.Node imageNode;
        if (d.getFirstImagePath() != null && !d.getFirstImagePath().equals("placeholder.jpg")) {
            java.io.File file = new java.io.File("uploads/" + d.getFirstImagePath());
            if (file.exists()) {
                ImageView imgView = new ImageView(new javafx.scene.image.Image(file.toURI().toString()));
                imgView.setFitWidth(260);
                imgView.setFitHeight(140);
                // Kita gunakan kliping sederhana dengan StackPane / style
                javafx.scene.layout.StackPane sp = new javafx.scene.layout.StackPane(imgView);
                sp.setStyle("-fx-background-radius: 12 12 0 0; -fx-background-color: #2A2A2A;");
                imageNode = sp;
            } else {
                imageNode = createPlaceholder(d.getNamaWisata());
            }
        } else {
            imageNode = createPlaceholder(d.getNamaWisata());
        }

        VBox body = new VBox(6);
        body.getStyleClass().add("card-body");

        Label lblKategori = new Label(d.getKategoriNama());
        lblKategori.getStyleClass().add("card-category");

        Label lblNama = new Label(d.getNamaWisata());
        lblNama.getStyleClass().add("card-title");

        Label lblAlamat = new Label("📍 " + d.getAlamat());
        lblAlamat.getStyleClass().add("card-location");

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        Label lblHarga = new Label(d.getFormattedHarga());
        lblHarga.getStyleClass().add("card-price");
        
        HBox rightFooter = new HBox(4);
        rightFooter.setAlignment(Pos.CENTER_RIGHT);
        Label lblRating = new Label("⭐ " + String.format("%.1f", d.getRatingRataRata()));
        lblRating.getStyleClass().add("card-rating");
        rightFooter.getChildren().add(lblRating);
        
        HBox.setHgrow(lblHarga, javafx.scene.layout.Priority.ALWAYS);
        footer.getChildren().addAll(lblHarga, rightFooter);

        body.getChildren().addAll(lblKategori, lblNama, lblAlamat, footer);
        card.getChildren().addAll(imageNode, body);

        card.setOnMouseClicked(e -> {
            DetailDestinasiController.selectedDestinasi = d;
            MainLayoutController.getInstance().loadContent("detail_destinasi");
        });

        return card;
    }

    @FXML
    private void handleShowAll() {
        selectedKategoriId = -1;
        loadDestinasi(null, -1);
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
