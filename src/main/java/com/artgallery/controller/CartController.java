package com.artgallery.controller;

import java.io.IOException;
import com.artgallery.App;
import com.artgallery.service.OrderService;
import com.artgallery.model.Order;
import com.artgallery.model.Artwork;
import com.artgallery.model.OrderItem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.util.Callback;

public class CartController {

    // STATIC CART for simplicity in this demo without dependency injection
    // framework
    private static Order currentCart;
    private static OrderService orderService = new OrderService();

    public static void addToCart(Artwork artwork) {
        if (currentCart == null) {
            currentCart = orderService.createCart(LoginController.currentUser);
        }
        orderService.addToCart(currentCart, artwork);
    }

    @FXML
    private TableView<OrderItem> cartTable;
    @FXML
    private TableColumn<OrderItem, String> artworkColumn;
    @FXML
    private TableColumn<OrderItem, Number> priceColumn;
    @FXML
    private TableColumn<OrderItem, Void> actionColumn;
    @FXML
    private Label totalLabel;
    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        artworkColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtwork().getTitle()));
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(
                cellData.getValue().getPrice().doubleValue()));

        if (currentCart != null) {
            refreshTable();
        } else {
            totalLabel.setText("Total: 0 Dhs");
        }

        setupActionColumn();
    }

    private void setupActionColumn() {
        Callback<TableColumn<OrderItem, Void>, TableCell<OrderItem, Void>> cellFactory = new Callback<TableColumn<OrderItem, Void>, TableCell<OrderItem, Void>>() {
            @Override
            public TableCell<OrderItem, Void> call(final TableColumn<OrderItem, Void> param) {
                final TableCell<OrderItem, Void> cell = new TableCell<OrderItem, Void>() {
                    private final Button btn = new Button("❌ Supprimer");

                    {
                        btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            OrderItem item = getTableView().getItems().get(getIndex());
                            orderService.removeFromCart(currentCart, item);
                            refreshTable();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private void refreshTable() {
        if (currentCart != null) {
            cartTable.setItems(FXCollections.observableArrayList(currentCart.getItems()));
            totalLabel.setText("Total: " + currentCart.getTotalAmount() + " Dhs");
        } else {
            cartTable.getItems().clear();
            totalLabel.setText("Total: 0 Dhs");
        }
    }

    @FXML
    private void handleCheckout() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            messageLabel.setText("Votre panier est vide.");
            return;
        }
        try {
            orderService.checkout(currentCart);
            messageLabel.setText("Commande validée !");
            currentCart = null; // Reset cart
            cartTable.getItems().clear();
            totalLabel.setText("Total: 0 Dhs");
        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("artwork_list");
    }
}
