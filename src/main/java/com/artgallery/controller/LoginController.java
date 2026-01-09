package com.artgallery.controller;

import java.io.IOException;
import com.artgallery.App;
import com.artgallery.service.UserService;
import com.artgallery.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private UserService userService = new UserService();

    // Pour stocker l'utilisateur connecté globalement (simple session management)
    public static User currentUser;

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userService.login(username, password);

        if (user != null) {
            currentUser = user;
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                App.setRoot("admin_dashboard");
            } else {
                System.out.println(" Redirection directe vers la galerie...");
                App.setRoot("artwork_list");
            }
        } else {
            errorLabel.setText("Identifiants incorrects");
        }
    }

    @FXML
    private void goToRegister() throws IOException {
        App.setRoot("register");
    }
}
