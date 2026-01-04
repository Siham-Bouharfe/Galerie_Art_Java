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

            // 1. Supprimer tous les OrderItems liés à cette œuvre via HQL
            session.createMutationQuery("DELETE FROM OrderItem WHERE artwork.id = :id")
                    .setParameter("id", artwork.getId())
                    .executeUpdate();

            // 2. Supprimer l'œuvre elle-même via HQL (évite les erreurs de cascade des
            // parents)
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
