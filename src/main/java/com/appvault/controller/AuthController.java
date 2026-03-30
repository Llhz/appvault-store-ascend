package com.appvault.controller;

import com.appvault.dto.UserRegistrationDto;
import com.appvault.repository.UserRepository;
import com.appvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                             @RequestParam(required = false) String logout,
                             @RequestParam(required = false) String registered,
                             Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("logout", "You have been logged out.");
        if (registered != null) model.addAttribute("registered", "Registration successful! Please sign in.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                           BindingResult result, Model model) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "email.exists", "An account with this email already exists.");
        }
        if (result.hasErrors()) {
            return "auth/register";
        }
        userService.registerNewUser(dto);
        return "redirect:/auth/login?registered";
    }
}
