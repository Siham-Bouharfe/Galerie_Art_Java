package com.artgallery.service;

import com.artgallery.dao.UserDAO;
import com.artgallery.model.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public User register(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole("CUSTOMER"); // par defaut
        
        userDAO.save(user);
        return user;
    }

    public User login(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
}
