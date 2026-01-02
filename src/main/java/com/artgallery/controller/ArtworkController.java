package com.artgallery.controller;

import java.io.IOException;
import java.math.BigDecimal;
import com.artgallery.App;
import com.artgallery.service.GalleryService;
import com.artgallery.model.Artwork;
import com.artgallery.model.Artist;
import com.artgallery.model.Category;
import com.artgallery.model.User;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ArtworkController {

    @FXML
    private TableView<Artwork> artworkTable;
    @FXML
    private TableColumn<Artwork, String> titleColumn;
    @FXML
    private TableColumn<Artwork, String> artistColumn;
    @FXML
    private TableColumn<Artwork, String> categoryColumn;
    @FXML
    private TableColumn<Artwork, Double> priceColumn;

    // Champs pour l'ajout (Admin seulement)
    @FXML
    private VBox adminBox;
    @FXML
    private TextField titleField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<Artist> artistComboBox;
    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private Label messageLabel;

    private GalleryService galleryService = new GalleryService();
    // In a real app, Cart should be shared/persisted. Here we might need a
    // singleton or pass it around.
    // For simplicity, let's assume CartController will handle fetching the CURRENT
    // cart or creating one.
    // Here we just add to a static cart reference or similar?
    // Let's use a simple static way for this demo to hold the 'current session
    // cart' in OrderService or App.
    // Better: CartController has the logic, but here we just need to "Add to Cart".
    // Let's instantiate OrderService.

    // We need a place to store the active cart.
    // Let's add a static field in App or LoginController for the active Cart.

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        // For complex objects, we might need a custom cell factory or toString on the
        // object
        // Let's assume toString returns name for Artist/Category or use simple binding
        artistColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist().getName()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategory().getName()));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadArtworks();

        User currentUser = LoginController.currentUser;
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            adminBox.setVisible(true);
            loadComboData();
        } else {
            adminBox.setVisible(false);
        }
    }

    private void loadArtworks() {
        artworkTable.setItems(FXCollections.observableArrayList(galleryService.getAllArtworks()));
    }

    private void loadComboData() {
        artistComboBox.setItems(FXCollections.observableArrayList(galleryService.getAllArtists()));
        categoryComboBox.setItems(FXCollections.observableArrayList(galleryService.getAllCategories()));
        // Setup display converter if needed, or rely on toString()
    }

    @FXML
    private void handleAddArtwork() {
        try {
            String title = titleField.getText();
            BigDecimal price = new BigDecimal(priceField.getText());
            Artist artist = artistComboBox.getValue();
            Category category = categoryComboBox.getValue();

            Artwork artwork = new Artwork();
            artwork.setTitle(title);
            artwork.setPrice(price);
            artwork.setArtist(artist);
            artwork.setCategory(category);
            artwork.setImageUrl("default.jpg"); // Placeholder
            artwork.setAvailable(true);

            galleryService.addArtwork(artwork);
            loadArtworks();
            messageLabel.setText("Oeuvre ajoutée !");
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddToCart() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Veuillez sélectionner une oeuvre");
            return;
        }

        // Add to global cart (simplification see CartController note)
        CartController.addToCart(selected);
        messageLabel.setText("Ajouté au panier !");
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("main"); // Retour Dashboard
    }
}
