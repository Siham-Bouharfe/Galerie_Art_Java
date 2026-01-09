package com.artgallery.service;

import com.artgallery.dao.ArtistDAO;
import com.artgallery.model.Artist;
import java.util.List;

public class ArtistService {
    private ArtistDAO artistDAO = new ArtistDAO();

    public List<Artist> getAllArtists() {
        return artistDAO.findAll();
    }

    public void addArtist(Artist artist) {
        artistDAO.save(artist);
    }

    public void updateArtist(Artist artist) {
        artistDAO.update(artist);
    }

    public void deleteArtist(Artist artist) {
        artistDAO.delete(artist);
    }
}
