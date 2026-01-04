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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import java.io.InputStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.artgallery.model.Order;
// import com.artgallery.model.OrderItem;
import com.artgallery.dao.OrderDAO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.stage.FileChooser;

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
    private ListView<Artwork> artworkListView;

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

    // === Category Tab ===
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Long> categoryIdColumn;
    @FXML
    private TableColumn<Category, String> categoryNameColumn;

    @FXML
    private TextField categoryNameField;

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
        setupArtworkTable();
        setupCategoryTable();
        setupOrderTable();
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
        artworkListView.setItems(artworkList);

        // Custom cell factory for visual display
        artworkListView.setCellFactory(listView -> new ListCell<Artwork>() {
            @Override
            protected void updateItem(Artwork artwork, boolean empty) {
                super.updateItem(artwork, empty);

                if (empty || artwork == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox hbox = new HBox(15);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setStyle(
                            "-fx-padding: 10px; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

                    // Image thumbnail
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(80);
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 1px;");

                    try {
                        String imagePath = artwork.getImageUrl();
                        if (imagePath != null && !imagePath.isEmpty()) {
                            InputStream imageStream = getClass().getResourceAsStream("/" + imagePath);
                            if (imageStream != null) {
                                imageView.setImage(new Image(imageStream));
                            } else {
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                            }
                        } else {
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                        }
                    } catch (Exception e) {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                    }

                    // Info panel
                    VBox infoBox = new VBox(5);
                    infoBox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(infoBox, Priority.ALWAYS);

                    Label titleLabel = new Label(artwork.getTitle());
                    titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

                    Label priceLabel = new Label("Prix: " + artwork.getPrice() + " €");
                    priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4caf50; -fx-font-weight: bold;");

                    Label artistLabel = new Label("Artiste: " + artwork.getArtist().getName());
                    artistLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                    Label categoryLabel = new Label("Catégorie: " + artwork.getCategory().getName());
                    categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                    Label quantityLabel = new Label("Quantité: " + artwork.getQuantity());
                    quantityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                    infoBox.getChildren().addAll(titleLabel, priceLabel, artistLabel, categoryLabel, quantityLabel);

                    hbox.getChildren().addAll(imageView, infoBox);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        // Selection listener
        artworkListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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

    private void setupCategoryTable() {
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryTable.setItems(categoryList);

        // Selection listener to populate form
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoryNameField.setText(newSelection.getName());
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

    private void clearArtistForm() {
        artistNameField.clear();
        artistBioArea.clear();
    }

    @FXML
    private void updateArtist() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(artistNameField.getText());
            selected.setBio(artistBioArea.getText());
            artistDAO.update(selected);
            refreshArtists();
        }
    }

    @FXML
    private void deleteArtist() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            artistDAO.delete(selected);
            refreshArtists();
        }
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
            showSuccessAlert("Succès", "L'œuvre a été ajoutée avec succès.");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix ou quantité invalide");
        }
    }

    private void clearArtworkForm() {
        artworkTitleField.clear();
        artworkDescArea.clear();
        artworkPriceField.clear();
        artworkQuantityField.clear();
        artworkImageUrlField.clear();
        artworkArtistCombo.setValue(null);
        artworkCategoryCombo.setValue(null);
        artworkAvailableCheck.setSelected(true);
        artworkListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void updateArtwork() {
        Artwork selected = artworkListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                updateArtworkFromForm(selected);
                artworkDAO.update(selected);
                refreshArtworks();
                showSuccessAlert("Succès", "L'œuvre a été modifiée avec succès.");
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez saisir un prix et une quantité valides.");
                refreshArtworks(); // Revert UI changes
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modifier l'œuvre : " + e.getMessage());
                refreshArtworks(); // Revert UI changes
                e.printStackTrace();
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
        Artwork selected = artworkListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                artworkDAO.delete(selected);
                refreshArtworks();
                showSuccessAlert("Succès", "L'œuvre a été supprimée.");
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur technique s'est produite lors de la suppression : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void refreshArtworks() {
        artworkList.setAll(artworkDAO.findAll());
    }

    // === Category Actions ===
    @FXML
    private void addCategory() {
        Category category = new Category();
        category.setName(categoryNameField.getText());
        categoryDAO.save(category);
        refreshCategories();
        clearCategoryForm();
    }

    private void clearCategoryForm() {
        categoryNameField.clear();
    }

    @FXML
    private void updateCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(categoryNameField.getText());
            categoryDAO.update(selected);
            refreshCategories();

        }
    }

    @FXML
    private void deleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            categoryDAO.delete(selected);
            refreshCategories();

        }
    }

    private void refreshCategories() {
        categoryList.setAll(categoryDAO.findAll());
        // Also refresh combos dependent on categories
        artworkCategoryCombo.setItems(categoryList);
    }

    // === Image Upload ===
    @FXML
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        // Filtres d'extensions
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"));

        // Ouvrir le sélecteur de fichier
        File selectedFile = fileChooser.showOpenDialog(artworkImageUrlField.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Créer le dossier images s'il n'existe pas
                Path imagesDir = Paths.get("src/main/resources/images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }

                // Générer un nom de fichier unique (timestamp + nom original)
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = imagesDir.resolve(fileName);

                // Copier le fichier
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le champ avec le chemin relatif
                artworkImageUrlField.setText("images/" + fileName);

                showSuccessAlert("Succès", "Image uploadée avec succès !");

            } catch (IOException e) {
                showAlert("Erreur", "Impossible de copier l'image : " + e.getMessage());
            }
        }
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

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
