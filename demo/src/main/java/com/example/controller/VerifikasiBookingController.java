package com.example.controller;

import com.example.dao.BookingDAO;
import com.example.model.Booking;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.stream.Collectors;

public class VerifikasiBookingController {

    @FXML private ComboBox<String> cmbStatus;
    @FXML private TableView<Booking> tableBooking;

    private BookingDAO bookingDAO = new BookingDAO();
    private List<Booking> allBookings;

    @FXML
    public void initialize() {
        cmbStatus.setItems(FXCollections.observableArrayList("Semua", "Pending", "Terverifikasi", "Ditolak"));
        cmbStatus.getSelectionModel().select("Pending");

        loadData();
    }

    private void loadData() {
        int pengelolaId = SessionManager.getInstance().getCurrentUserId();
        allBookings = bookingDAO.findByPengelola(pengelolaId);
        handleFilter();
    }

    @FXML
    private void handleFilter() {
        String filter = cmbStatus.getValue();
        if ("Semua".equals(filter)) {
            tableBooking.setItems(FXCollections.observableArrayList(allBookings));
        } else {
            List<Booking> filtered = allBookings.stream()
                .filter(b -> b.getStatus().equals(filter))
                .collect(Collectors.toList());
            tableBooking.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void handleTerima() {
        updateStatus("Terverifikasi");
    }

    @FXML
    private void handleTolak() {
        updateStatus("Ditolak");
    }

    private void updateStatus(String status) {
        Booking selected = tableBooking.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih data booking terlebih dahulu.");
            return;
        }

        if (!selected.getStatus().equals("Pending")) {
            AlertHelper.showWarning("Peringatan", "Hanya booking dengan status Pending yang bisa diubah.");
            return;
        }

        if (bookingDAO.updateStatus(selected.getId(), status)) {
            AlertHelper.showSuccess("Sukses", "Status booking berhasil diubah menjadi " + status);
            loadData();
        } else {
            AlertHelper.showError("Gagal", "Terjadi kesalahan sistem.");
        }
    }
}
