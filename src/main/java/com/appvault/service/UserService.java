package com.appvault.service;

import com.appvault.dto.UserProfileDto;
import com.appvault.dto.UserRegistrationDto;
import com.appvault.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerNewUser(UserRegistrationDto dto);
    Optional<User> findByEmail(String email);
    void updateProfile(UserProfileDto dto, User currentUser);
    List<User> findAll();
}
