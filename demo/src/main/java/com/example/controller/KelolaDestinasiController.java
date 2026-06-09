package com.example.controller;

import com.example.dao.DestinasiDAO;
import com.example.dao.KategoriDAO;
import com.example.model.Destinasi;
import com.example.model.Kategori;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class KelolaDestinasiController {

    @FXML private TableView<Destinasi> tableDestinasi;
    @FXML private VBox formContainer;
    
    @FXML private Label lblFormTitle;
    @FXML private TextField txtNama;
    @FXML private ComboBox<Kategori> cmbKategori;
    @FXML private TextField txtHarga;
    @FXML private TextField txtJam;
    @FXML private TextArea txtAlamat;
    @FXML private TextArea txtDeskripsi;
    @FXML private Label lblId;
    @FXML private Button btnDelete;

    @FXML private Label lblNamaFile;
    @FXML private TextField txtPetaLokasi;

    private DestinasiDAO destinasiDAO = new DestinasiDAO();
    private KategoriDAO kategoriDAO = new KategoriDAO();
    private java.io.File selectedImageFile;
    private String currentImagePath = "placeholder.jpg";

    @FXML
    public void initialize() {
        loadTable();
        loadKategori();

        tableDestinasi.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                showEditForm(newSel);
            }
        });
    }

    private void loadTable() {
        int pengelolaId = SessionManager.getInstance().getCurrentUserId();
        List<Destinasi> list = destinasiDAO.findByPengelola(pengelolaId);
        tableDestinasi.setItems(FXCollections.observableArrayList(list));
    }

    private void loadKategori() {
        List<Kategori> list = kategoriDAO.findAll();
        cmbKategori.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void handlePilihGambar() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Pilih Gambar Destinasi");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        java.io.File file = fileChooser.showOpenDialog(formContainer.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            lblNamaFile.setText(file.getName());
        }
    }

    @FXML
    private void handleShowAddForm() {
        tableDestinasi.getSelectionModel().clearSelection();
        lblFormTitle.setText("Tambah Destinasi Baru");
        lblId.setText("");
        txtNama.clear();
        cmbKategori.getSelectionModel().clearSelection();
        txtHarga.clear();
        txtJam.clear();
        txtAlamat.clear();
        txtDeskripsi.clear();
        txtPetaLokasi.clear();
        selectedImageFile = null;
        currentImagePath = "placeholder.jpg";
        lblNamaFile.setText("Belum ada gambar");
        
        btnDelete.setVisible(false);
        formContainer.setVisible(true);
        formContainer.setManaged(true);
    }

    private void showEditForm(Destinasi d) {
        lblFormTitle.setText("Edit Destinasi");
        lblId.setText(String.valueOf(d.getId()));
        txtNama.setText(d.getNamaWisata());
        
        for (Kategori k : cmbKategori.getItems()) {
            if (k.getId() == d.getKategoriId()) {
                cmbKategori.getSelectionModel().select(k);
                break;
            }
        }
        
        txtHarga.setText(String.valueOf((int)d.getHargaTiket()));
        txtJam.setText(d.getJamOperasional());
        txtAlamat.setText(d.getAlamat());
        txtDeskripsi.setText(d.getDeskripsi());
        txtPetaLokasi.setText(d.getPetaLokasi() != null ? d.getPetaLokasi() : "");

        currentImagePath = d.getImagePath() != null ? d.getImagePath() : "placeholder.jpg";
        lblNamaFile.setText(currentImagePath);
        selectedImageFile = null;

        btnDelete.setVisible(true);
        formContainer.setVisible(true);
        formContainer.setManaged(true);
    }

    @FXML
    private void handleSave() {
        String nama = txtNama.getText();
        Kategori kat = cmbKategori.getValue();
        String harga = txtHarga.getText();
        String jam = txtJam.getText();
        String alamat = txtAlamat.getText();
        String deskripsi = txtDeskripsi.getText();
        String petaLokasi = txtPetaLokasi.getText();

        if (nama.isEmpty() || kat == null || harga.isEmpty()) {
            AlertHelper.showError("Error", "Nama, Kategori, dan Harga tidak boleh kosong.");
            return;
        }

        double hrg;
        try {
            hrg = Double.parseDouble(harga);
        } catch (NumberFormatException e) {
            AlertHelper.showError("Error", "Harga harus berupa angka.");
            return;
        }

        // Handle Image Copy
        String finalImagePath = currentImagePath;
        if (selectedImageFile != null) {
            try {
                java.io.File uploadDir = new java.io.File("uploads");
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String newFileName = System.currentTimeMillis() + "_" + selectedImageFile.getName();
                java.io.File destFile = new java.io.File(uploadDir, newFileName);
                java.nio.file.Files.copy(selectedImageFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                finalImagePath = newFileName;
            } catch (java.io.IOException ex) {
                AlertHelper.showError("Error", "Gagal menyimpan gambar: " + ex.getMessage());
                return;
            }
        }

        Destinasi d = new Destinasi();
        d.setNamaWisata(nama);
        d.setKategoriId(kat.getId());
        d.setHargaTiket(hrg);
        d.setJamOperasional(jam);
        d.setAlamat(alamat);
        d.setDeskripsi(deskripsi);
        d.setPetaLokasi(petaLokasi);
        d.setPengelolaId(SessionManager.getInstance().getCurrentUserId());
        d.setImagePath(finalImagePath);

        String idStr = lblId.getText();
        if (idStr.isEmpty()) {
            // Create
            if (destinasiDAO.create(d)) {
                AlertHelper.showSuccess("Sukses", "Destinasi berhasil ditambahkan.");
                handleCancelForm();
                loadTable();
            } else {
                AlertHelper.showError("Gagal", "Gagal menambahkan destinasi.");
            }
        } else {
            // Update
            d.setId(Integer.parseInt(idStr));
            if (destinasiDAO.update(d)) {
                AlertHelper.showSuccess("Sukses", "Destinasi berhasil diupdate.");
                handleCancelForm();
                loadTable();
            } else {
                AlertHelper.showError("Gagal", "Gagal mengupdate destinasi.");
            }
        }
    }

    @FXML
    private void handleDelete() {
        String idStr = lblId.getText();
        if (idStr.isEmpty()) return;

        if (AlertHelper.showConfirmation("Hapus", "Yakin ingin menghapus destinasi ini?")) {
            if (destinasiDAO.delete(Integer.parseInt(idStr))) {
                handleCancelForm();
                loadTable();
            }
        }
    }

    @FXML
    private void handleCancelForm() {
        formContainer.setVisible(false);
        formContainer.setManaged(false);
        tableDestinasi.getSelectionModel().clearSelection();
        selectedImageFile = null;
    }
}
