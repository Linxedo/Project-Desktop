package com.example.controller;

import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.stream.Collectors;

public class AdminUsersController {

    @FXML private ComboBox<String> cmbRole;
    @FXML private TableView<User> tableUsers;

    private UserDAO userDAO = new UserDAO();
    private List<User> allUsers;

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList("Semua Role", "Admin", "Pengelola", "Wisatawan"));
        cmbRole.getSelectionModel().selectFirst();
        loadData();
    }

    private void loadData() {
        allUsers = userDAO.findAll();
        handleFilter();
    }

    @FXML
    private void handleFilter() {
        String filter = cmbRole.getValue();
        if ("Semua Role".equals(filter)) {
            tableUsers.setItems(FXCollections.observableArrayList(allUsers));
        } else {
            List<User> filtered = allUsers.stream()
                .filter(u -> u.getRole().equals(filter))
                .collect(Collectors.toList());
            tableUsers.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void handleDelete() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Peringatan", "Pilih user yang ingin dihapus.");
            return;
        }

        if (selected.getRole().equals("Admin")) {
            AlertHelper.showError("Error", "Tidak bisa menghapus akun Admin.");
            return;
        }

        if (AlertHelper.showConfirmation("Hapus", "Yakin ingin menghapus user " + selected.getUsername() + "?")) {
            if (userDAO.delete(selected.getId())) {
                loadData();
                AlertHelper.showSuccess("Sukses", "User berhasil dihapus.");
            }
        }
    }
}
