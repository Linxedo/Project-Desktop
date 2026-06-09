package com.example.controller;

import com.example.dao.BookingDAO;
import com.example.model.Booking;
import com.example.model.Destinasi;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.sql.Date;
import java.time.LocalDate;

public class BookingFormController {

    public static Destinasi destinasi;

    @FXML private Label lblDestinasi;
    @FXML private Label lblHargaSatuan;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> spinnerTiket;
    @FXML private Label lblTotalHarga;

    private BookingDAO bookingDAO = new BookingDAO();

    @FXML
    public void initialize() {
        if (destinasi == null) {
            handleCancel();
            return;
        }

        lblDestinasi.setText(destinasi.getNamaWisata());
        lblHargaSatuan.setText("Harga: " + destinasi.getFormattedHarga() + " / tiket");

        // Init DatePicker (disable past dates if possible, simplified here)
        datePicker.setValue(LocalDate.now().plusDays(1)); // Default besok

        // Init Spinner
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        spinnerTiket.setValueFactory(valueFactory);

        // Listener for total calculation
        spinnerTiket.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());

        calculateTotal();
    }

    private void calculateTotal() {
        int qty = spinnerTiket.getValue();
        double total = qty * destinasi.getHargaTiket();
        lblTotalHarga.setText(String.format("Rp %,.0f", total));
    }

    @FXML
    private void handleConfirmBooking() {
        LocalDate date = datePicker.getValue();
        if (date == null || date.isBefore(LocalDate.now())) {
            AlertHelper.showError("Error", "Pilih tanggal kunjungan yang valid (hari ini atau kedepannya).");
            return;
        }

        int qty = spinnerTiket.getValue();
        double total = qty * destinasi.getHargaTiket();

        Booking b = new Booking();
        b.setUserId(SessionManager.getInstance().getCurrentUserId());
        b.setDestinasiId(destinasi.getId());
        b.setTglKunjungan(Date.valueOf(date));
        b.setJumlahTiket(qty);
        b.setTotalHarga(total);

        if (bookingDAO.create(b)) {
            AlertHelper.showSuccess("Sukses", "Booking berhasil dibuat dan menunggu verifikasi pengelola.");
            MainLayoutController.getInstance().loadContent("booking_history");
        } else {
            AlertHelper.showError("Gagal", "Terjadi kesalahan sistem saat membuat booking.");
        }
    }

    @FXML
    private void handleCancel() {
        MainLayoutController.getInstance().loadContent("detail_destinasi");
    }
}
