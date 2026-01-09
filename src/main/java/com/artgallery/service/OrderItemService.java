package com.artgallery.service;

import com.artgallery.model.OrderItem;
import com.artgallery.model.Order;
import com.artgallery.model.Artwork;

public class OrderItemService {

    public OrderItem createOrderItem(Order order, Artwork artwork) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setArtwork(artwork);
        item.setPrice(artwork.getPrice());
        return item;
    }
}
