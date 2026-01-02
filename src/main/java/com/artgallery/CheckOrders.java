package com.artgallery;

import com.artgallery.dao.OrderDAO;
import com.artgallery.model.Order;
import java.util.List;

public class CheckOrders {
    public static void main(String[] args) {
        System.out.println("Vérification des commandes...");
        OrderDAO orderDAO = new OrderDAO();
        try {
            List<Order> orders = orderDAO.findAll();
            System.out.println("Nombre de commandes trouvées : " + orders.size());
            for (Order o : orders) {
                System.out.println("- Commande #" + o.getId() + " par " + o.getUser().getUsername() + " : "
                        + o.getTotalAmount() + "€");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Fin de la vérification.");
        System.exit(0);
    }
}
