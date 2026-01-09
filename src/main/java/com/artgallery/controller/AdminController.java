package com.artgallery.controller;

import com.artgallery.util.ImageUtils;

import com.artgallery.App;
// import com.artgallery.dao.ArtistDAO;
// import com.artgallery.dao.ArtworkDAO;
// import com.artgallery.dao.CategoryDAO;
import com.artgallery.model.Artist;
import com.artgallery.model.Artwork;
import com.artgallery.model.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.artgallery.model.Order;
import com.artgallery.service.ArtistService;
import com.artgallery.service.ArtworkService;
import com.artgallery.service.CategoryService;
import com.artgallery.service.OrderService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.stage.FileChooser;

public class AdminController {

    // Artist Tab
    @FXML
    private VBox artistListContainer;
    @FXML
    private VBox artistFormContainer;
    @FXML
    private Button btnAddArtist;
    @FXML
    private Button btnUpdateArtist;
    @FXML
    private Button btnDeleteArtist;

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

    // Order Tab
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

    // Artwork Tab
    @FXML
    private VBox artworkListContainer;
    @FXML
    private VBox artworkFormContainer;
    @FXML
    private Button btnAddArtwork;
    @FXML
    private Button btnUpdateArtwork;
    @FXML
    private Button btnDeleteArtwork;

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

    // Category Tab
    @FXML
    private VBox categoryListContainer;
    @FXML
    private VBox categoryFormContainer;
    @FXML
    private Button btnAddCategory;
    @FXML
    private Button btnUpdateCategory;
    @FXML
    private Button btnDeleteCategory;

    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Long> categoryIdColumn;
    @FXML
    private TableColumn<Category, String> categoryNameColumn;
    @FXML
    private TextField categoryNameField;

    private ArtistService artistService = new ArtistService();
    private ArtworkService artworkService = new ArtworkService();
    private CategoryService categoryService = new CategoryService();
    private OrderService orderService = new OrderService();

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

        showArtistList();
        showArtworkList();
        showCategoryList();
    }

    // artists

    private void setupArtistTable() {
        artistIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        artistBioColumn.setCellValueFactory(new PropertyValueFactory<>("bio"));
        artistTable.setItems(artistList);

        // Selection listener: Switch to Edit Form
        artistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                artistNameField.setText(newSelection.getName());
                artistBioArea.setText(newSelection.getBio());
                showArtistForm(true); // Edit Mode
            }
        });
    }

    @FXML
    private void showArtistList() {
        artistListContainer.setVisible(true);
        artistFormContainer.setVisible(false);
        artistTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void showArtistAddForm() {
        clearArtistForm();
        showArtistForm(false); // Add Mode
    }

    private void showArtistForm(boolean isEdit) {
        artistListContainer.setVisible(false);
        artistFormContainer.setVisible(true);

        btnAddArtist.setVisible(!isEdit);
        btnAddArtist.setManaged(!isEdit);

        btnUpdateArtist.setVisible(isEdit);
        btnUpdateArtist.setManaged(isEdit);

        btnDeleteArtist.setVisible(isEdit);
        btnDeleteArtist.setManaged(isEdit);
    }

    @FXML
    private void addArtist() {
        Artist artist = new Artist();
        artist.setName(artistNameField.getText());
        artist.setBio(artistBioArea.getText());
        artistService.addArtist(artist);
        refreshArtists();
        showArtistList(); // Return to list
    }

    @FXML
    private void updateArtist() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(artistNameField.getText());
            selected.setBio(artistBioArea.getText());
            artistService.updateArtist(selected);
            refreshArtists();
            showArtistList(); // Return to list
        }
    }

    @FXML
    private void deleteArtist() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected != null && showConfirmation("Confirmation", "Êtes-vous sûr de vouloir supprimer cet artiste ?")) {
            artistService.deleteArtist(selected);
            refreshArtists();
            showArtistList(); // Return to list
        }
    }

    private void clearArtistForm() {
        artistNameField.clear();
        artistBioArea.clear();
    }

    private void refreshArtists() {
        artistList.setAll(artistService.getAllArtists());
        artworkArtistCombo.setItems(artistList);
    }

    // artworks

    private void setupArtworkTable() {
        artworkListView.setItems(artworkList);

        // Custom cell factory
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

                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(80);
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-border-color: #ddd; -fx-border-width: 1px;");
                    imageView.setImage(ImageUtils.loadImage(artwork.getImageUrl()));

                    VBox infoBox = new VBox(5);
                    infoBox.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(infoBox, Priority.ALWAYS);

                    Label titleLabel = new Label(artwork.getTitle());
                    titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
                    Label priceLabel = new Label("Prix: " + artwork.getPrice() + " Dhs");
                    priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4caf50; -fx-font-weight: bold;");
                    Label artistLabel = new Label(
                            "Artiste: " + (artwork.getArtist() != null ? artwork.getArtist().getName() : "Inconnu")); // Safety
                                                                                                                      // check
                    artistLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                    Label categoryLabel = new Label("Catégorie: "
                            + (artwork.getCategory() != null ? artwork.getCategory().getName() : "Inconnue"));
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
                showArtworkForm(true); // Edit Mode
            }
        });
    }

    @FXML
    private void showArtworkList() {
        artworkListContainer.setVisible(true);
        artworkFormContainer.setVisible(false);
        artworkListView.getSelectionModel().clearSelection();
    }

    @FXML
    private void showArtworkAddForm() {
        clearArtworkForm();
        showArtworkForm(false); // Add Mode
    }

    private void showArtworkForm(boolean isEdit) {
        artworkListContainer.setVisible(false);
        artworkFormContainer.setVisible(true);

        btnAddArtwork.setVisible(!isEdit);
        btnAddArtwork.setManaged(!isEdit);

        btnUpdateArtwork.setVisible(isEdit);
        btnUpdateArtwork.setManaged(isEdit);

        btnDeleteArtwork.setVisible(isEdit);
        btnDeleteArtwork.setManaged(isEdit);
    }

    @FXML
    private void addArtwork() {
        try {
            Artwork artwork = new Artwork();
            updateArtworkFromForm(artwork);
            artworkService.addArtwork(artwork);
            refreshArtworks();
            showSuccessAlert("Succès", "L'œuvre a été ajoutée avec succès.");
            showArtworkList();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Prix ou quantité invalide");
        }
    }

    @FXML
    private void updateArtwork() {
        Artwork selected = artworkListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                updateArtworkFromForm(selected);
                artworkService.updateArtwork(selected);
                refreshArtworks();
                showSuccessAlert("Succès", "L'œuvre a été modifiée avec succès.");
                showArtworkList();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez saisir un prix et une quantité valides.");
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de modifier l'œuvre : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteArtwork() {
        Artwork selected = artworkListView.getSelectionModel().getSelectedItem();
        if (selected != null && showConfirmation("Confirmation", "Êtes-vous sûr de vouloir supprimer cette œuvre ?")) {
            try {
                artworkService.deleteArtwork(selected);
                refreshArtworks();
                showSuccessAlert("Succès", "L'œuvre a été supprimée.");
                showArtworkList();
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur technique s'est produite lors de la suppression : " + e.getMessage());
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

    private void clearArtworkForm() {
        artworkTitleField.clear();
        artworkDescArea.clear();
        artworkPriceField.clear();
        artworkQuantityField.clear();
        artworkImageUrlField.clear();
        artworkArtistCombo.setValue(null);
        artworkCategoryCombo.setValue(null);
        artworkAvailableCheck.setSelected(true);
    }

    private void refreshArtworks() {
        artworkList.setAll(artworkService.getAllArtworks());
    }

    // categories

    private void setupCategoryTable() {
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryTable.setItems(categoryList);

        // Selection listener
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                categoryNameField.setText(newSelection.getName());
                showCategoryForm(true); // Edit Mode
            }
        });
    }

    @FXML
    private void showCategoryList() {
        categoryListContainer.setVisible(true);
        categoryFormContainer.setVisible(false);
        categoryTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void showCategoryAddForm() {
        clearCategoryForm();
        showCategoryForm(false);
    }

    private void showCategoryForm(boolean isEdit) {
        categoryListContainer.setVisible(false);
        categoryFormContainer.setVisible(true);

        btnAddCategory.setVisible(!isEdit);
        btnAddCategory.setManaged(!isEdit);

        btnUpdateCategory.setVisible(isEdit);
        btnUpdateCategory.setManaged(isEdit);

        btnDeleteCategory.setVisible(isEdit);
        btnDeleteCategory.setManaged(isEdit);
    }

    @FXML
    private void addCategory() {
        Category category = new Category();
        category.setName(categoryNameField.getText());
        categoryService.addCategory(category);
        refreshCategories();
        showCategoryList();
    }

    @FXML
    private void updateCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(categoryNameField.getText());
            categoryService.updateCategory(selected);
            refreshCategories();
            showCategoryList();
        }
    }

    @FXML
    private void deleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected != null
                && showConfirmation("Confirmation", "Êtes-vous sûr de vouloir supprimer cette catégorie ?")) {
            categoryService.deleteCategory(selected);
            refreshCategories();
            showCategoryList();
        }
    }

    private void clearCategoryForm() {
        categoryNameField.clear();
    }

    private void refreshCategories() {
        categoryList.setAll(categoryService.getAllCategories());
        artworkCategoryCombo.setItems(categoryList);
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
        artistList.setAll(artistService.getAllArtists());
        artworkList.setAll(artworkService.getAllArtworks());
        categoryList.setAll(categoryService.getAllCategories());
        orderList.setAll(orderService.getAllOrders());

        artworkArtistCombo.setItems(artistList);
        artworkCategoryCombo.setItems(categoryList);
    }

    @FXML
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(artworkImageUrlField.getScene().getWindow());

        if (selectedFile != null) {
            try {
                Path imagesDir = Paths.get("src/main/resources/images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = imagesDir.resolve(fileName);
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
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

    // New Confirmation Dialog
    private boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(content);


        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}
