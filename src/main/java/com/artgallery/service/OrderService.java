package com.artgallery.service;

import com.artgallery.dao.OrderDAO;
import com.artgallery.model.Order;
import com.artgallery.model.OrderItem;
import com.artgallery.model.User;
import com.artgallery.model.Artwork;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();

    // Simple in-memory cart for the session, or we could persist it.
    // For this simple app, we'll create an Order object that acts as a cart until
    // saved.

    public Order createCart(User user) {
        Order cart = new Order();
        cart.setUser(user);
        cart.setOrderDate(LocalDateTime.now());
        cart.setStatus("PENDING");
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setItems(new ArrayList<>());
        return cart;
    }

    public void addToCart(Order cart, Artwork artwork) {
        if (!artwork.isAvailable() || artwork.getQuantity() <= 0) {
            throw new IllegalStateException("Oeuvre non disponible");
        }

        // Count how many of this artwork are already in the cart
        long countInCart = cart.getItems().stream()
                .filter(item -> item.getArtwork().getId().equals(artwork.getId()))
                .count();

        if (countInCart >= artwork.getQuantity()) {
            throw new IllegalStateException("Quantité insuffisante pour cette oeuvre");
        }

        OrderItem item = new OrderItem();
        item.setOrder(cart);
        item.setArtwork(artwork);
        item.setPrice(artwork.getPrice());

        cart.getItems().add(item);
        recalculateTotal(cart);
    }

    public void removeFromCart(Order cart, OrderItem item) {
        cart.getItems().remove(item);
        recalculateTotal(cart);
    }

    private void recalculateTotal(Order cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : cart.getItems()) {
            total = total.add(item.getPrice());
        }
        cart.setTotalAmount(total);
    }

    public void checkout(Order cart) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Panier vide");
        }

        // Verify stock again before checkout
        for (OrderItem item : cart.getItems()) {
            if (item.getArtwork().getQuantity() <= 0) {
                throw new IllegalStateException(
                        "Oeuvre " + item.getArtwork().getTitle() + " n'est plus disponible en stock.");
            }
        }

        cart.setOrderDate(LocalDateTime.now());
        cart.setStatus("COMPLETED");

        // Decrement stock
        com.artgallery.dao.ArtworkDAO artworkDAO = new com.artgallery.dao.ArtworkDAO();
        for (OrderItem item : cart.getItems()) {
            Artwork artwork = item.getArtwork();
            artwork.setQuantity(artwork.getQuantity() - 1);
            if (artwork.getQuantity() <= 0) {
                artwork.setAvailable(false);
            }
            artworkDAO.update(artwork);
        }

        orderDAO.save(cart);
    }

    public List<Order> getUserOrders(User user) {
        return orderDAO.findByUser(user);
    }
}
