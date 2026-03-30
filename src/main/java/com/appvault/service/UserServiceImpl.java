package com.appvault.service;

import com.appvault.dto.UserProfileDto;
import com.appvault.dto.UserRegistrationDto;
import com.appvault.model.Role;
import com.appvault.model.User;
import com.appvault.repository.RoleRepository;
import com.appvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerNewUser(UserRegistrationDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName("ROLE_USER").ifPresent(roles::add);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateProfile(UserProfileDto dto, User currentUser) {
        currentUser.setFirstName(dto.getFirstName());
        currentUser.setLastName(dto.getLastName());
        currentUser.setAvatarUrl(dto.getAvatarUrl());
        userRepository.save(currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
