package com.example.controller;

import com.example.App;
import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        lblError.setText("");
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            lblError.setText("Username dan Password tidak boleh kosong.");
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            SessionManager.getInstance().login(user);
            System.out.println("Login berhasil sebagai: " + user.getRole());
            try {
                App.setRoot("main_layout");
            } catch (IOException e) {
                e.printStackTrace();
                lblError.setText("Gagal memuat layout utama.");
            }
        } else {
            lblError.setText("Username atau Password salah.");
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGuestMode() {
        SessionManager.getInstance().logout(); // Ensure no active session
        try {
            App.setRoot("main_layout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
