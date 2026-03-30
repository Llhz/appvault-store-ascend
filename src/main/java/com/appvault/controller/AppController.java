package com.appvault.controller;

import com.appvault.model.AppListing;
import com.appvault.model.Review;
import com.appvault.model.User;
import com.appvault.dto.AppSuggestDto;
import com.appvault.service.AppListingService;
import com.appvault.service.ReviewService;
import com.appvault.service.UserService;
import com.appvault.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AppController {

    @Autowired private AppListingService appListingService;
    @Autowired private ReviewService reviewService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserService userService;

    @GetMapping("/browse")
    public String browse(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(required = false) Long categoryId,
                         @RequestParam(defaultValue = "rating") String sort,
                         Model model) {
        Sort sortOrder = buildSort(sort);
        Page<AppListing> apps;
        if (categoryId != null) {
            apps = appListingService.findByCategory(categoryId, PageRequest.of(page, 12, sortOrder));
            model.addAttribute("currentCategory", categoryRepository.findById(categoryId).orElse(null));
        } else {
            apps = appListingService.findAll(PageRequest.of(page, 12, sortOrder));
        }
        model.addAttribute("apps", apps);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("currentSort", sort);
        model.addAttribute("categoryId", categoryId);
        return "app/browse";
    }

    @GetMapping("/browse/category/{categoryId}")
    public String browseByCategory(@PathVariable Long categoryId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "rating") String sort,
                                   Model model) {
        return browse(page, categoryId, sort, model);
    }

    @GetMapping("/app/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        AppListing app = appListingService.findById(id);
        List<Review> reviews = reviewService.findByApp(id);
        model.addAttribute("app", app);
        model.addAttribute("reviews", reviews);
        model.addAttribute("relatedApps", appListingService.findRelated(app, 4));
        model.addAttribute("categories", categoryRepository.findAll());

        // Compute rating breakdown (percentage per star 1-5)
        Map<Integer, Integer> ratingCounts = new HashMap<>();
        for (int s = 1; s <= 5; s++) ratingCounts.put(s, 0);
        for (Review r : reviews) {
            ratingCounts.put(r.getRating(), ratingCounts.get(r.getRating()) + 1);
        }
        if (!reviews.isEmpty()) {
            for (int s = 1; s <= 5; s++) {
                ratingCounts.put(s, ratingCounts.get(s) * 100 / reviews.size());
            }
        }
        model.addAttribute("ratingCounts", ratingCounts);

        if (auth != null && auth.isAuthenticated()) {
            Long userId = getUserId(auth);
            boolean reviewed = userId != null && reviewService.hasUserReviewedApp(userId, id);
            model.addAttribute("alreadyReviewed", reviewed);
        } else {
            model.addAttribute("alreadyReviewed", false);
        }
        return "app/detail";
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        Page<AppListing> results = appListingService.searchByQuery(q, PageRequest.of(page, 12));
        model.addAttribute("apps", results);
        model.addAttribute("query", q);
        model.addAttribute("categories", categoryRepository.findAll());
        return "app/search-results";
    }

    @GetMapping("/search/suggest")
    @ResponseBody
    public List<AppSuggestDto> suggest(@RequestParam(defaultValue = "") String q) {
        if (q.trim().length() < 2) {
            return java.util.Collections.emptyList();
        }
        return appListingService.searchSuggestions(q.trim(), 5);
    }

    private Sort buildSort(String sort) {
        switch (sort) {
            case "newest": return Sort.by("createdAt").descending();
            case "name": return Sort.by("name").ascending();
            case "price-low": return Sort.by("price").ascending();
            case "price-high": return Sort.by("price").descending();
            default: return Sort.by("rating").descending();
        }
    }

    private Long getUserId(Authentication auth) {
        if (auth == null) return null;
        return userService.findByEmail(auth.getName())
                .map(User::getId)
                .orElse(null);
    }
}
