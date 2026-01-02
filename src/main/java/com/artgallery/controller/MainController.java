package com.artgallery.controller;

import java.io.IOException;
import com.artgallery.App;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private void goToArtworks() throws IOException {
        App.setRoot("artwork_list");
    }

    @FXML
    private void goToCart() throws IOException {
        App.setRoot("cart");
    }

    @FXML
    private void goToProfile() {

        System.out.println("Profile clicked");
    }

    @FXML
    private void handleLogout() throws IOException {
        LoginController.currentUser = null;
        App.setRoot("login");
    }
}
