package com.artgallery.service;

import com.artgallery.dao.ArtworkDAO;
import com.artgallery.dao.ArtistDAO;
import com.artgallery.dao.CategoryDAO;
import com.artgallery.model.Artwork;
import com.artgallery.model.Artist;
import com.artgallery.model.Category;
import java.util.List;

public class GalleryService {
    private ArtworkDAO artworkDAO = new ArtworkDAO();
    private ArtistDAO artistDAO = new ArtistDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    public List<Artwork> getAllArtworks() {
        return artworkDAO.findAll();
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public List<Artist> getAllArtists() {
        return artistDAO.findAll();
    }

    public void addArtwork(Artwork artwork) {
        artworkDAO.save(artwork);
    }

    public void addCategory(Category category) {
        categoryDAO.save(category);
    }

    public void addArtist(Artist artist) {
        artistDAO.save(artist);
    }

    public Artwork updateArtwork(Artwork artwork) {
        return artworkDAO.update(artwork);
    }

    public void deleteArtwork(Artwork artwork) {
        artworkDAO.delete(artwork);
    }
}
