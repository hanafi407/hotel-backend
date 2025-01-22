package com.hanafi.hanafihotel.service;

import com.hanafi.hanafihotel.model.User;

import java.util.List;

public interface UserService {
    User registerUser(User user) ;
    List<User> getAllUsers();
    User getUserById(Long userId);
    User getUserByEmail(String email);
    User findUserByEmail(String email);
    void deleteUser(String email);
}
