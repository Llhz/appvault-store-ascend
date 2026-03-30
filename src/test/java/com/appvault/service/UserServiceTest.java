package com.appvault.service;

import com.appvault.dto.UserRegistrationDto;
import com.appvault.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void findAdminByEmail() {
        assertTrue(userService.findByEmail("admin@appvault.com").isPresent());
    }

    @Test
    void findNonExistentUser() {
        assertFalse(userService.findByEmail("nobody@nowhere.com").isPresent());
    }

    @Test
    void registerNewUser() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("test.new@example.com");
        dto.setPassword("Password1!");
        dto.setConfirmPassword("Password1!");

        User user = userService.registerNewUser(dto);
        assertNotNull(user.getId());
        assertEquals("Test", user.getFirstName());
        assertEquals("test.new@example.com", user.getEmail());
        assertFalse(user.getRoles().isEmpty());
    }
}
