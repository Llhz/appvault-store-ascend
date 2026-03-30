package com.appvault.controller;

import com.appvault.dto.ReviewDto;
import com.appvault.model.User;
import com.appvault.service.ReviewService;
import com.appvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private UserService userService;

    @PostMapping("/app/{appId}")
    public String submitReview(@PathVariable Long appId,
                                @Valid @ModelAttribute ReviewDto dto,
                                BindingResult result,
                                Authentication auth) {
        if (result.hasErrors()) {
            return "redirect:/app/" + appId + "?reviewError";
        }
        User user = getUser(auth);
        if (user == null) return "redirect:/auth/login";
        reviewService.save(dto, user, appId);
        return "redirect:/app/" + appId + "#reviews";
    }

    @PostMapping("/{reviewId}/helpful")
    @ResponseBody
    public ResponseEntity<?> markHelpful(@PathVariable Long reviewId) {
        reviewService.incrementHelpful(reviewId);
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                                @RequestParam(defaultValue = "/") String returnUrl,
                                Authentication auth) {
        User user = getUser(auth);
        if (user == null) return "redirect:/auth/login";
        reviewService.delete(reviewId, user);
        return "redirect:" + returnUrl;
    }

    private User getUser(Authentication auth) {
        if (auth == null) return null;
        return userService.findByEmail(auth.getName()).orElse(null);
    }
}
