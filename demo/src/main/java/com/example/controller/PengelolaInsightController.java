package com.example.controller;

import com.example.dao.UlasanDAO;
import com.example.model.Ulasan;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.List;

public class PengelolaInsightController {

    @FXML private Label lblTotalUlasan;
    @FXML private Label lblAvgRating;
    @FXML private TableView<Ulasan> tableUlasan;

    private UlasanDAO ulasanDAO = new UlasanDAO();

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData() {
        int pengelolaId = SessionManager.getInstance().getCurrentUserId();
        List<Ulasan> list = ulasanDAO.findByPengelola(pengelolaId);
        tableUlasan.setItems(FXCollections.observableArrayList(list));

        lblTotalUlasan.setText(String.valueOf(list.size()));

        if (!list.isEmpty()) {
            double avg = list.stream().mapToInt(Ulasan::getRating).average().orElse(0);
            lblAvgRating.setText(String.format("%.1f", avg));
        } else {
            lblAvgRating.setText("0.0");
        }
    }
}
