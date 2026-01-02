package com.artgallery.controller;

import com.artgallery.App;
import com.artgallery.dao.ArtistDAO;
import com.artgallery.dao.ArtworkDAO;
import com.artgallery.dao.CategoryDAO;
import com.artgallery.model.Artist;
import com.artgallery.model.Artwork;
import com.artgallery.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.artgallery.model.Order;
// import com.artgallery.model.OrderItem;
import com.artgallery.dao.OrderDAO;

public class AdminController {

    // === Artist Tab ===
    @FXML
    private TableView<Artist> artistTable;
    @FXML
    private TableColumn<Artist, Long> artistIdColumn;
    @FXML
    private TableColumn<Artist, String> artistNameColumn;
    @FXML
    private TableColumn<Artist, String> artistBioColumn;

    @FXML
    private TextField artistNameField;
    @FXML
    private TextArea artistBioArea;

    // === Order Tab ===
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Long> orderIdColumn;
    @FXML
    private TableColumn<Order, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<Order, String> orderUserColumn;
    @FXML
    private TableColumn<Order, String> orderArtworksColumn;
    @FXML
    private TableColumn<Order, BigDecimal> orderTotalColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;

    // === Artwork Tab ===
    @FXML
    private TableView<Artwork> artworkTable;
    @FXML
    private TableColumn<Artwork, Long> artworkIdColumn;
    @FXML
    private TableColumn<Artwork, String> artworkTitleColumn;
    @FXML
    private TableColumn<Artwork, BigDecimal> artworkPriceColumn;
    @FXML
    private TableColumn<Artwork, Integer> artworkQuantityColumn;
    @FXML
    private TableColumn<Artwork, String> artworkArtistColumn; // To show Artist Name
    @FXML
    private TableColumn<Artwork, String> artworkCategoryColumn; // To show Category Name

    @FXML
    private TextField artworkTitleField;
    @FXML
    private TextArea artworkDescArea;
    @FXML
    private TextField artworkPriceField;
    @FXML
    private TextField artworkQuantityField;
    @FXML
    private TextField artworkImageUrlField;
    @FXML
    private ComboBox<Artist> artworkArtistCombo;
    @FXML
    private ComboBox<Category> artworkCategoryCombo;
    @FXML
    private CheckBox artworkAvailableCheck;

    private ArtistDAO artistDAO = new ArtistDAO();
    private ArtworkDAO artworkDAO = new ArtworkDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private OrderDAO orderDAO = new OrderDAO();

    private ObservableList<Artist> artistList = FXCollections.observableArrayList();
    private ObservableList<Artwork> artworkList = FXCollections.observableArrayList();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupArtistTable();
        setupArtistTable();
        setupArtworkTable();
        setupOrderTable();
        loadData();
        loadData();
    }

    private void setupArtistTable() {
        artistIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistBioColumn.setCellValueFactory(new PropertyValueFactory<>("bio"));
        artistTable.setItems(artistList);

        // Selection listener to populate form
        artistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                artistNameField.setText(newSelection.getName());
                artistBioArea.setText(newSelection.getBio());
            }
        });
    }

    private void setupArtworkTable() {
        artworkIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        artworkTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artworkPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        artworkQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Custom cell factories for related objects
        artworkArtistColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getArtist().getName()));

        artworkCategoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCategory().getName()));

        artworkTable.setItems(artworkList);

        // Selection listener
        artworkTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                artworkTitleField.setText(newSelection.getTitle());
                artworkDescArea.setText(newSelection.getDescription());
                artworkPriceField.setText(newSelection.getPrice().toString());
                artworkQuantityField.setText(String.valueOf(newSelection.getQuantity()));
                artworkImageUrlField.setText(newSelection.getImageUrl());
                artworkArtistCombo.setValue(newSelection.getArtist());
                artworkCategoryCombo.setValue(newSelection.getCategory());
                artworkAvailableCheck.setSelected(newSelection.isAvailable());
            }
        });
    }

    private void setupOrderTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        orderUserColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getUser().getUsername()));

        orderArtworksColumn.setCellValueFactory(cellData -> {
            String artworks = cellData.getValue().getItems().stream()
                    .map(item -> item.getArtwork().getTitle())
                    .collect(Collectors.joining(", "));
            return new javafx.beans.property.SimpleStringProperty(artworks);
        });

        orderTable.setItems(orderList);
    }

    private void loadData() {
        artistList.setAll(artistDAO.findAll());
        artworkList.setAll(artworkDAO.findAll());
        artistList.setAll(artistDAO.findAll());
        artworkList.setAll(artworkDAO.findAll());
        categoryList.setAll(categoryDAO.findAll());
        orderList.setAll(orderDAO.findAll());

        artworkArtistCombo.setItems(artistList);
        artworkCategoryCombo.setItems(categoryList);
    }

    // === Artist Actions ===
    @FXML
    private void addArtist() {
        Artist artist = new Artist();
        artist.setName(artistNameField.getText());
        artist.setBio(artistBioArea.getText());
        artistDAO.save(artist);
        refreshArtists();
        clearArtistForm();
    }

    @FXML
    private void updateArtist() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(artistNameField.getText());
            selected.setBio(artistBioArea.getText());
            artistDAO.update(selected);
            refreshArtists();
            clearArtistForm();
        }
    }

    @FXML
    private void deleteArtist() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            artistDAO.delete(selected);
            refreshArtists();
            clearArtistForm();
        }
    }

    @FXML
    private void clearArtistForm() {
        artistNameField.clear();
        artistBioArea.clear();
        artistTable.getSelectionModel().clearSelection();
    }

    private void refreshArtists() {
        artistList.setAll(artistDAO.findAll());
        // Also refresh combos dependent on artists
        artworkArtistCombo.setItems(artistList);
    }

    // === Artwork Actions ===
    @FXML
    private void addArtwork() {
        try {
            Artwork artwork = new Artwork();
            updateArtworkFromForm(artwork);
            artworkDAO.save(artwork);
            refreshArtworks();
            clearArtworkForm();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix invalide");
        }
    }

    @FXML
    private void updateArtwork() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                updateArtworkFromForm(selected);
                artworkDAO.update(selected);
                refreshArtworks();
                clearArtworkForm();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Prix invalide");
            }
        }
    }

    private void updateArtworkFromForm(Artwork artwork) {
        artwork.setTitle(artworkTitleField.getText());
        artwork.setDescription(artworkDescArea.getText());
        artwork.setPrice(new BigDecimal(artworkPriceField.getText()));
        artwork.setQuantity(Integer.parseInt(artworkQuantityField.getText()));
        artwork.setImageUrl(artworkImageUrlField.getText());
        artwork.setArtist(artworkArtistCombo.getValue());
        artwork.setCategory(artworkCategoryCombo.getValue());
        artwork.setAvailable(artworkAvailableCheck.isSelected());
    }

    @FXML
    private void deleteArtwork() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            artworkDAO.delete(selected);
            refreshArtworks();
            clearArtworkForm();
        }
    }

    @FXML
    private void clearArtworkForm() {
        artworkTitleField.clear();
        artworkDescArea.clear();
        artworkPriceField.clear();
        artworkQuantityField.clear();
        artworkImageUrlField.clear();
        artworkArtistCombo.getSelectionModel().clearSelection();
        artworkCategoryCombo.getSelectionModel().clearSelection();
        artworkAvailableCheck.setSelected(true);
        artworkTable.getSelectionModel().clearSelection();
    }

    private void refreshArtworks() {
        artworkList.setAll(artworkDAO.findAll());
    }

    @FXML
    private void handleLogout() throws IOException {
        LoginController.currentUser = null;
        App.setRoot("login");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
