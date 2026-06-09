package com.example.controller;

import com.example.dao.KategoriDAO;
import com.example.model.Kategori;
import com.example.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class AdminKategoriController {

    @FXML private TextField txtNamaKategori;
    @FXML private TableView<Kategori> tableKategori;

    private KategoriDAO kategoriDAO = new KategoriDAO();

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData() {
        List<Kategori> list = kategoriDAO.findAll();
        tableKategori.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void handleAdd() {
        String nama = txtNamaKategori.getText();
        if (nama == null || nama.trim().isEmpty()) {
            AlertHelper.showWarning("Peringatan", "Nama kategori tidak boleh kosong.");
            return;
        }

        if (kategoriDAO.create(nama.trim())) {
            txtNamaKategori.clear();
            loadData();
        } else {
            AlertHelper.showError("Gagal", "Gagal menambah kategori. Mungkin nama sudah ada.");
        }
    }

    @FXML
    private void handleDelete() {
        Kategori selected = tableKategori.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih kategori yang ingin dihapus.");
            return;
        }

        if (AlertHelper.showConfirmation("Hapus", "Yakin ingin menghapus kategori " + selected.getNamaKategori() + "?")) {
            if (kategoriDAO.delete(selected.getId())) {
                loadData();
            } else {
                AlertHelper.showError("Gagal", "Gagal menghapus kategori. Pastikan tidak ada destinasi yang menggunakan kategori ini.");
            }
        }
    }
}
