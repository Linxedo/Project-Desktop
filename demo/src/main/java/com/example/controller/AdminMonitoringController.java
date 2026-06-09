package com.example.controller;

import com.example.dao.BookingDAO;
import com.example.dao.DestinasiDAO;
import com.example.dao.UserDAO;
import com.example.model.Booking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.List;

public class AdminMonitoringController {

    @FXML private Label lblTotalUser;
    @FXML private Label lblTotalDestinasi;
    @FXML private Label lblTotalBooking;
    
    @FXML private TableView<Booking> tableBooking;

    private UserDAO userDAO = new UserDAO();
    private DestinasiDAO destinasiDAO = new DestinasiDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        loadStats();
        loadRecentBookings();
    }

    private void loadStats() {
        lblTotalUser.setText(String.valueOf(userDAO.countAll()));
        lblTotalDestinasi.setText(String.valueOf(destinasiDAO.countAll()));
        lblTotalBooking.setText(String.valueOf(bookingDAO.countAll()));
    }

    private void loadRecentBookings() {
        List<Booking> list = bookingDAO.findAll();
        // Just show all for now, in real app might limit to top 50
        tableBooking.setItems(FXCollections.observableArrayList(list));
    }
}
