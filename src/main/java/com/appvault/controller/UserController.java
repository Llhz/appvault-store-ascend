package com.appvault.controller;

import com.appvault.dto.UserProfileDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.User;
import com.appvault.service.ReviewService;
import com.appvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private ReviewService reviewService;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = getUser(auth);
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAvatarUrl(user.getAvatarUrl());
        model.addAttribute("user", user);
        model.addAttribute("profileDto", dto);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") UserProfileDto dto,
                                 BindingResult result,
                                 Authentication auth, Model model) {
        User user = getUser(auth);
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "user/profile";
        }
        userService.updateProfile(dto, user);
        return "redirect:/user/profile?saved";
    }

    @GetMapping("/my-reviews")
    public String myReviews(Authentication auth, Model model) {
        User user = getUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewService.findByUser(user.getId()));
        return "user/my-reviews";
    }

    private User getUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
