package com.artgallery.controller;

import java.io.IOException;
import java.io.InputStream;
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
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.Optional;

public class ArtworkController {

    @FXML
    private FlowPane galleryPane;
    @FXML
    private HBox adminToolbar;
    @FXML
    private Label messageLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button cartButton;

    private GalleryService galleryService = new GalleryService();
    private User currentUser;
    private boolean isAdmin;

    @FXML
    public void initialize() {
        currentUser = LoginController.currentUser;
        isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());

        // Afficher/masquer les éléments selon le rôle
        if (adminToolbar != null) {
            adminToolbar.setVisible(isAdmin);
            adminToolbar.setManaged(isAdmin);
        }

        // Pour les admins, on garde le bouton retour mais vers le dashboard
        if (isAdmin) {
            if (backButton != null) {
                backButton.setText("⬅️ Tableau de Bord");
                backButton.setVisible(true);
                backButton.setManaged(true);
                backButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            if (cartButton != null) {
                cartButton.setVisible(false);
                cartButton.setManaged(false);
            }
        } else {
            // Pour les utilisateurs normaux, on cache le bouton retour (page racine)
            if (backButton != null) {
                backButton.setVisible(false);
                backButton.setManaged(false);
            }
        }

        loadArtworks();
    }

    private void loadArtworks() {
        galleryPane.getChildren().clear();

        for (Artwork artwork : galleryService.getAllArtworks()) {
            VBox card = createArtworkCard(artwork);
            galleryPane.getChildren().add(card);
        }
    }

    private VBox createArtworkCard(Artwork artwork) {
        VBox card = new VBox(10);
        card.getStyleClass().add("artwork-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(240);
        card.setMinHeight(350);

        // Image container with border
        VBox imageContainer = new VBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setStyle("-fx-border-color: #eee; -fx-border-width: 1; -fx-background-color: #fafafa;");
        imageContainer.setPrefSize(200, 200);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageContainer.getChildren().add(imageView);

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

        // Titre
        Label titleLabel = new Label(artwork.getTitle());
        titleLabel.getStyleClass().add("sub-header");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(220);
        titleLabel.setAlignment(Pos.CENTER);

        // Artiste
        Label artistLabel = new Label("👨‍🎨 " + artwork.getArtist().getName());
        artistLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Catégorie
        Label categoryLabel = new Label("🏷️ " + artwork.getCategory().getName());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Prix
        Label priceLabel = new Label(artwork.getPrice() + " Dhs");
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        // Boutons selon le rôle
        if (isAdmin) {
            HBox buttonBox = new HBox(5);
            buttonBox.setAlignment(Pos.CENTER);

            Button editBtn = new Button("Modifier");
            editBtn.getStyleClass().add("btn-primary");
            editBtn.setOnAction(e -> handleEditArtwork(artwork));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("btn-danger");
            deleteBtn.setOnAction(e -> handleDeleteArtwork(artwork));

            buttonBox.getChildren().addAll(editBtn, deleteBtn);
            card.getChildren().addAll(imageContainer, titleLabel, artistLabel, categoryLabel, priceLabel, buttonBox);
        } else {
            // Utilisateur : Voir bouton Ajouter au Panier
            Button addToCartBtn = new Button("🛒 Ajouter au Panier");
            addToCartBtn.getStyleClass().add("btn-success");
            addToCartBtn.setPrefWidth(200);
            addToCartBtn.setOnAction(e -> handleAddToCart(artwork));
            card.getChildren().addAll(imageContainer, titleLabel, artistLabel, categoryLabel, priceLabel, addToCartBtn);
        }

        return card;
    }

    @FXML
    private void handleAddToCart(Artwork artwork) {
        if (artwork == null) {
            messageLabel.setText("Erreur : oeuvre non trouvée");
            return;
        }
        CartController.addToCart(artwork);
        messageLabel.setText(artwork.getTitle() + " ajouté au panier !");
    }

    @FXML
    private void handleAddNewArtwork() {
        Dialog<Artwork> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une œuvre");
        dialog.setHeaderText("Nouvelle œuvre d'art");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Titre");
        TextField priceField = new TextField();
        priceField.setPromptText("Prix");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantité");
        TextField imageField = new TextField();
        imageField.setPromptText("images/nom_image.jpg");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(3);

        ComboBox<Artist> artistCombo = new ComboBox<>(
                FXCollections.observableArrayList(galleryService.getAllArtists()));
        ComboBox<Category> categoryCombo = new ComboBox<>(
                FXCollections.observableArrayList(galleryService.getAllCategories()));

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Prix:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantité:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Image:"), 0, 3);
        grid.add(imageField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descArea, 1, 4);
        grid.add(new Label("Artiste:"), 0, 5);
        grid.add(artistCombo, 1, 5);
        grid.add(new Label("Catégorie:"), 0, 6);
        grid.add(categoryCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Artwork artwork = new Artwork();
                    artwork.setTitle(titleField.getText());
                    artwork.setPrice(new BigDecimal(priceField.getText()));
                    artwork.setQuantity(Integer.parseInt(quantityField.getText()));
                    artwork.setImageUrl(imageField.getText());
                    artwork.setDescription(descArea.getText());
                    artwork.setArtist(artistCombo.getValue());
                    artwork.setCategory(categoryCombo.getValue());
                    artwork.setAvailable(true);
                    return artwork;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Artwork> result = dialog.showAndWait();
        result.ifPresent(artwork -> {
            galleryService.addArtwork(artwork);
            loadArtworks();
            messageLabel.setText("Œuvre ajoutée avec succès !");
        });
    }

    @FXML
    private void handleEditArtwork(Artwork artwork) {
        Dialog<Artwork> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'œuvre");
        dialog.setHeaderText("Modifier : " + artwork.getTitle());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(artwork.getTitle());
        TextField priceField = new TextField(artwork.getPrice().toString());
        TextField quantityField = new TextField(String.valueOf(artwork.getQuantity()));
        TextField imageField = new TextField(artwork.getImageUrl());
        TextArea descArea = new TextArea(artwork.getDescription());
        descArea.setPrefRowCount(3);

        ComboBox<Artist> artistCombo = new ComboBox<>(
                FXCollections.observableArrayList(galleryService.getAllArtists()));
        artistCombo.setValue(artwork.getArtist());
        ComboBox<Category> categoryCombo = new ComboBox<>(
                FXCollections.observableArrayList(galleryService.getAllCategories()));
        categoryCombo.setValue(artwork.getCategory());

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Prix:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Quantité:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Image:"), 0, 3);
        grid.add(imageField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descArea, 1, 4);
        grid.add(new Label("Artiste:"), 0, 5);
        grid.add(artistCombo, 1, 5);
        grid.add(new Label("Catégorie:"), 0, 6);
        grid.add(categoryCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Create a copy or update fields carefully
                    artwork.setTitle(titleField.getText());
                    artwork.setPrice(new BigDecimal(priceField.getText()));
                    artwork.setQuantity(Integer.parseInt(quantityField.getText()));
                    artwork.setImageUrl(imageField.getText());
                    artwork.setDescription(descArea.getText());
                    artwork.setArtist(artistCombo.getValue());
                    artwork.setCategory(categoryCombo.getValue());
                    return artwork;
                } catch (NumberFormatException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur de saisie");
                    errorAlert.setHeaderText("Données invalides");
                    errorAlert.setContentText("Veuillez vérifier que le prix et la quantité sont des nombres valides.");
                    errorAlert.showAndWait();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        Optional<Artwork> result = dialog.showAndWait();
        result.ifPresent(updatedArtwork -> {
            try {
                galleryService.updateArtwork(updatedArtwork);
                loadArtworks();
                messageLabel.setText("Œuvre modifiée avec succès !");
            } catch (Exception e) {
                messageLabel.setText("Erreur lors de la modification : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleDeleteArtwork(Artwork artwork) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'œuvre ?");
        alert.setContentText("Voulez-vous vraiment supprimer \"" + artwork.getTitle() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            galleryService.deleteArtwork(artwork);
            loadArtworks();
            messageLabel.setText("Œuvre supprimée !");
        }
    }

    @FXML
    private void handleGoToCart() throws IOException {
        App.setRoot("cart");
    }

    @FXML
    private void handleLogout() throws IOException {
        LoginController.currentUser = null;
        App.setRoot("login");
    }

    @FXML
    private void goBack() throws IOException {
        if (isAdmin) {
            App.setRoot("admin_dashboard");
        } else {
            // Pour un utilisateur normal, on peut le renvoyer au login (logout)
            // ou simplement ne rien faire s'il est déjà sur sa page principale
            handleLogout();
        }
    }
}
