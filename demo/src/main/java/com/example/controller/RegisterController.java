package com.example.controller;

import com.example.App;
import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.util.AlertHelper;
import com.example.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Label lblError;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList("Wisatawan", "Pengelola"));
        cmbRole.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {
        lblError.setText("");

        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String role = cmbRole.getValue();

        // Validations
        if (!ValidationHelper.isValidUsername(username)) {
            lblError.setText("Username tidak valid (min 3 karakter).");
            return;
        }
        if (!ValidationHelper.isValidEmail(email)) {
            lblError.setText("Format email tidak valid.");
            return;
        }
        if (!ValidationHelper.isValidPassword(password)) {
            lblError.setText("Password min 6 karakter.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            lblError.setText("Konfirmasi password tidak cocok.");
            return;
        }

        // Check uniqueness
        if (userDAO.isUsernameExists(username)) {
            lblError.setText("Username sudah digunakan.");
            return;
        }
        if (userDAO.isEmailExists(email)) {
            lblError.setText("Email sudah digunakan.");
            return;
        }

        // Create user
        User newUser = new User(username, password, email, role);
        if (userDAO.register(newUser)) {
            AlertHelper.showSuccess("Registrasi Berhasil", "Akun berhasil dibuat. Silakan login.");
            handleGoToLogin();
        } else {
            lblError.setText("Terjadi kesalahan saat registrasi.");
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
