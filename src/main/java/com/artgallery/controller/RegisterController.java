package com.artgallery.controller;

import java.io.IOException;
import com.artgallery.App;
import com.artgallery.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            userService.register(username, password, email);
            messageLabel.setText("Inscription réussie ! Redirection...");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Délai simulé ou juste un bouton pkus tard. Ici on redirige direct après un
            // court instant ou clic.
            // Pour simplifier l'UX ici, on demande de cliquer sur Login ou on redirige.
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de l'inscription: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goToLogin() throws IOException {
        App.setRoot("login");
    }
}
