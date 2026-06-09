package com.example.controller;

import com.example.dao.BookingDAO;
import com.example.model.Booking;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.stream.Collectors;

public class BookingHistoryController {

    @FXML private ComboBox<String> cmbStatus;
    @FXML private TableView<Booking> tableBooking;

    private BookingDAO bookingDAO = new BookingDAO();
    private List<Booking> allBookings;

    @FXML
    public void initialize() {
        cmbStatus.setItems(FXCollections.observableArrayList("Semua", "Pending", "Terverifikasi", "Ditolak"));
        cmbStatus.getSelectionModel().selectFirst();

        loadData();
    }

    private void loadData() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        allBookings = bookingDAO.findByUser(userId);
        updateTable(allBookings);
    }

    private void updateTable(List<Booking> list) {
        ObservableList<Booking> data = FXCollections.observableArrayList(list);
        tableBooking.setItems(data);
    }

    @FXML
    private void handleFilter() {
        String filter = cmbStatus.getValue();
        if ("Semua".equals(filter)) {
            updateTable(allBookings);
        } else {
            List<Booking> filtered = allBookings.stream()
                .filter(b -> b.getStatus().equals(filter))
                .collect(Collectors.toList());
            updateTable(filtered);
        }
    }
}
