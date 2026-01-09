package com.artgallery.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.artgallery.App;
import com.artgallery.service.OrderService;
import com.artgallery.model.Order;
import com.artgallery.model.Artwork;
import com.artgallery.model.OrderItem;

import javafx.beans.property.SimpleIntegerProperty;
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

    private static Order currentCart;
    private static OrderService orderService = new OrderService();

    public static void addToCart(Artwork artwork) {
        if (currentCart == null) {
            currentCart = orderService.createCart(LoginController.currentUser);
        }
        orderService.addToCart(currentCart, artwork);
    }

    // Inner class for aggregation
    public static class CartEntry {
        private final Artwork artwork;
        private final int quantity;
        private final BigDecimal totalPrice;
        private final List<OrderItem> items;

        public CartEntry(Artwork artwork, List<OrderItem> items) {
            this.artwork = artwork;
            this.items = items;
            this.quantity = items.size();

            // Sum prices of all items
            BigDecimal sum = BigDecimal.ZERO;
            for (OrderItem item : items) {
                sum = sum.add(item.getPrice());
            }
            this.totalPrice = sum;
        }

        public Artwork getArtwork() {
            return artwork;
        }

        public int getQuantity() {
            return quantity;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public List<OrderItem> getItems() {
            return items;
        }
    }

    @FXML
    private TableView<CartEntry> cartTable;
    @FXML
    private TableColumn<CartEntry, String> artworkColumn;
    @FXML
    private TableColumn<CartEntry, Integer> quantityColumn;
    @FXML
    private TableColumn<CartEntry, Number> priceColumn;
    @FXML
    private TableColumn<CartEntry, Void> actionColumn;
    @FXML
    private Label totalLabel;
    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        artworkColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getArtwork().getTitle()));

        quantityColumn.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(
                cellData.getValue().getTotalPrice().doubleValue()));

        if (currentCart != null) {
            refreshTable();
        } else {
            totalLabel.setText("Total: 0 Dhs");
        }

        setupActionColumn();
    }

    private void setupActionColumn() {
        Callback<TableColumn<CartEntry, Void>, TableCell<CartEntry, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<CartEntry, Void> call(final TableColumn<CartEntry, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button();

                    {
                        btn.setMinWidth(100);
                        btn.setOnAction(event -> {
                            CartEntry entry = getTableView().getItems().get(getIndex());
                            if (entry != null && !entry.getItems().isEmpty()) {
                                // Remove only the first item in the list (decrement quantity by 1)
                                orderService.removeFromCart(currentCart, entry.getItems().get(0));
                                refreshTable();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            CartEntry entry = getTableView().getItems().get(getIndex());
                            // Dynamic text and style
                            if (entry != null) {
                                if (entry.getQuantity() > 1) {
                                    btn.setText(" Réduire");
                                    btn.setStyle(
                                            "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                                } else {
                                    btn.setText(" Supprimer");
                                    btn.setStyle(
                                            "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                                }
                            }
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }

    private void refreshTable() {
        if (currentCart != null) {
            List<OrderItem> allItems = currentCart.getItems();

            // Group by Artwork ID
            Map<Long, List<OrderItem>> groupedItems = allItems.stream()
                    .collect(Collectors.groupingBy(item -> item.getArtwork().getId()));

            List<CartEntry> entries = new ArrayList<>();
            for (List<OrderItem> group : groupedItems.values()) {
                if (!group.isEmpty()) {
                    entries.add(new CartEntry(group.get(0).getArtwork(), group));
                }
            }

            cartTable.setItems(FXCollections.observableArrayList(entries));
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
