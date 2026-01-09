package com.artgallery.dao;

import com.artgallery.model.Artwork;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ArtworkDAO extends GenericDAO<Artwork> {

    public ArtworkDAO() {
        super(Artwork.class);
    }

    @Override
    public void delete(Artwork artwork) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.createMutationQuery("DELETE FROM OrderItem WHERE artwork.id = :id")
                    .setParameter("id", artwork.getId())
                    .executeUpdate();

            session.createMutationQuery("DELETE FROM Artwork WHERE id = :id")
                    .setParameter("id", artwork.getId())
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw e;
        }
    }
}
