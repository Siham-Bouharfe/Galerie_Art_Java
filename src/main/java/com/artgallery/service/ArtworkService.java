package com.artgallery.service;

import com.artgallery.dao.ArtworkDAO;
import com.artgallery.model.Artwork;
import java.util.List;

public class ArtworkService {
    private ArtworkDAO artworkDAO = new ArtworkDAO();

    public List<Artwork> getAllArtworks() {
        return artworkDAO.findAll();
    }

    public void addArtwork(Artwork artwork) {
        artworkDAO.save(artwork);
    }

    public Artwork updateArtwork(Artwork artwork) {
        return artworkDAO.update(artwork);
    }

    public void deleteArtwork(Artwork artwork) {
        artworkDAO.delete(artwork);
    }
}
