package com.artgallery.dao;

import com.artgallery.model.Artwork;
import org.hibernate.Session;
import java.util.List;

public class ArtworkDAO extends GenericDAO<Artwork> {

    public ArtworkDAO() {
        super(Artwork.class);
    }
    
    // Exemple de méthode spécifique : trouver par catégorie
    public List<Artwork> findByCategory(Long categoryId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Artwork a WHERE a.category.id = :catId", Artwork.class)
                    .setParameter("catId", categoryId)
                    .list();
        }
    }
}
