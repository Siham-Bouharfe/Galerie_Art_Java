package com.artgallery.dao;

import com.artgallery.model.Order;
import com.artgallery.model.User;
import org.hibernate.Session;
import java.util.List;

public class OrderDAO extends GenericDAO<Order> {

    public OrderDAO() {
        super(Order.class);
    }

    public List<Order> findByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Order o WHERE o.user.id = :userId", Order.class)
                    .setParameter("userId", user.getId())
                    .list();
        }
    }
}
