package com.artgallery.service;

import com.artgallery.dao.CategoryDAO;
import com.artgallery.model.Category;
import java.util.List;

public class CategoryService {
    private CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public void addCategory(Category category) {
        categoryDAO.save(category);
    }

    public void updateCategory(Category category) {
        categoryDAO.update(category);
    }

    public void deleteCategory(Category category) {
        categoryDAO.delete(category);
    }
}
