package com.example.controller;

import com.example.dao.DestinasiDAO;
import com.example.dao.ItineraryDAO;
import com.example.model.Destinasi;
import com.example.model.Itinerary;
import com.example.model.ItineraryDetail;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class ItineraryController {

    @FXML private ListView<Itinerary> listItinerary;
    @FXML private VBox detailContainer;
    
    @FXML private Label lblNamaRencana;
    @FXML private Label lblTanggal;
    @FXML private Label lblTotalBiaya;

    @FXML private ComboBox<Destinasi> cmbDestinasi;
    @FXML private DatePicker dpTanggalKunjungan;
    @FXML private TableView<ItineraryDetail> tableDetail;

    private ItineraryDAO itineraryDAO = new ItineraryDAO();
    private DestinasiDAO destinasiDAO = new DestinasiDAO();
    private Itinerary selectedItinerary;

    @FXML
    public void initialize() {
        loadItineraryList();
        loadComboDestinasi();

        listItinerary.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showDetail(newSel);
            }
        });
    }

    private void loadItineraryList() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        List<Itinerary> list = itineraryDAO.findByUser(userId);
        listItinerary.setItems(FXCollections.observableArrayList(list));
        
        if (list.isEmpty()) {
            detailContainer.setVisible(false);
        }
    }

    private void loadComboDestinasi() {
        List<Destinasi> list = destinasiDAO.findAll();
        cmbDestinasi.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void handleCreateNew() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buat Rencana Baru");
        dialog.setHeaderText("Nama Rencana Perjalanan:");
        dialog.setContentText("Nama:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            Itinerary it = new Itinerary();
            it.setUserId(SessionManager.getInstance().getCurrentUserId());
            it.setNamaRencana(result.get());
            it.setTglRencana(Date.valueOf(LocalDate.now())); // Simplified

            int id = itineraryDAO.create(it);
            if (id > 0) {
                loadItineraryList();
            } else {
                AlertHelper.showError("Gagal", "Gagal membuat rencana.");
            }
        }
    }

    private void showDetail(Itinerary it) {
        selectedItinerary = it;
        detailContainer.setVisible(true);
        lblNamaRencana.setText(it.getNamaRencana());
        lblTanggal.setText("Tanggal: " + it.getTglRencana().toString());
        lblTotalBiaya.setText(it.getFormattedBiaya());

        loadTableDetail();
    }

    private void loadTableDetail() {
        if (selectedItinerary == null) return;
        List<ItineraryDetail> details = itineraryDAO.findDetailsByItinerary(selectedItinerary.getId());
        tableDetail.setItems(FXCollections.observableArrayList(details));
        
        // Recalculate total cost locally and update DB/UI if needed
        double total = details.stream().mapToDouble(ItineraryDetail::getHargaTiket).sum();
        lblTotalBiaya.setText(String.format("Rp %,.0f", total));
    }

    @FXML
    private void handleAddDetail() {
        if (selectedItinerary == null) return;

        Destinasi d = cmbDestinasi.getValue();
        LocalDate tanggal = dpTanggalKunjungan.getValue();

        if (d == null || tanggal == null) {
            AlertHelper.showWarning("Perhatian", "Pilih destinasi dan tanggal terlebih dahulu.");
            return;
        }

        ItineraryDetail detail = new ItineraryDetail();
        detail.setItineraryId(selectedItinerary.getId());
        detail.setDestinasiId(d.getId());
        detail.setTanggalKunjungan(Date.valueOf(tanggal));
        // Simple auto ordering
        detail.setUrutan(tableDetail.getItems().size() + 1);

        if (itineraryDAO.addDetail(detail)) {
            dpTanggalKunjungan.setValue(null);
            cmbDestinasi.getSelectionModel().clearSelection();
            loadTableDetail();
            
            // Reload list to update cost
            int idx = listItinerary.getSelectionModel().getSelectedIndex();
            loadItineraryList();
            listItinerary.getSelectionModel().select(idx);
        } else {
            AlertHelper.showError("Gagal", "Gagal menambahkan destinasi ke rencana.");
        }
    }

    @FXML
    private void handleDeleteItinerary() {
        if (selectedItinerary == null) return;

        if (AlertHelper.showConfirmation("Hapus", "Yakin ingin menghapus rencana ini?")) {
            if (itineraryDAO.delete(selectedItinerary.getId())) {
                detailContainer.setVisible(false);
                loadItineraryList();
            }
        }
    }
}
