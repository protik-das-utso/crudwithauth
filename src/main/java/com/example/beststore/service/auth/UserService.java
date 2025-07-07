package com.example.beststore.service.auth;

import com.example.beststore.model.auth.User;

public interface UserService {
    boolean registerUser(User user);
    boolean emailExists(String email);
    User loginUser(String email, String password);
}
