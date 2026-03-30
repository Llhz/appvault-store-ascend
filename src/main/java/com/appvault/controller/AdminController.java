package com.appvault.controller;

import com.appvault.dto.AppListingDto;
import com.appvault.model.AppListing;
import com.appvault.service.AppListingService;
import com.appvault.service.ReviewService;
import com.appvault.service.UserService;
import com.appvault.repository.AppListingRepository;
import com.appvault.repository.CategoryRepository;
import com.appvault.repository.ReviewRepository;
import com.appvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AppListingService appListingService;
    @Autowired private UserService userService;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AppListingRepository appListingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ReviewRepository reviewRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalApps", appListingRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalReviews", reviewRepository.count());
        model.addAttribute("featuredApps", appListingService.findFeatured().size());
        model.addAttribute("recentApps", appListingService.findRecent(5));
        return "admin/dashboard";
    }

    @GetMapping("/apps")
    public String listApps(Model model) {
        model.addAttribute("apps", appListingRepository.findAll());
        return "admin/manage-apps";
    }

    @GetMapping("/apps/new")
    public String newAppForm(Model model) {
        model.addAttribute("appDto", new AppListingDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/app-form";
    }

    @PostMapping("/apps")
    public String saveApp(@Valid @ModelAttribute("appDto") AppListingDto dto,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/app-form";
        }
        appListingService.save(dto);
        return "redirect:/admin/apps";
    }

    @GetMapping("/apps/{id}/edit")
    public String editAppForm(@PathVariable Long id, Model model) {
        AppListing app = appListingService.findById(id);
        AppListingDto dto = new AppListingDto();
        dto.setName(app.getName());
        dto.setSubtitle(app.getSubtitle());
        dto.setDescription(app.getDescription());
        dto.setDeveloper(app.getDeveloper());
        dto.setVersion(app.getVersion());
        dto.setSize(app.getSize());
        dto.setIconUrl(app.getIconUrl());
        dto.setHeaderImageUrl(app.getHeaderImageUrl());
        dto.setPrice(app.getPrice());
        dto.setFeatured(app.isFeatured());
        dto.setAgeRating(app.getAgeRating());
        dto.setCompatibility(app.getCompatibility());
        if (app.getCategory() != null) dto.setCategoryId(app.getCategory().getId());
        model.addAttribute("appDto", dto);
        model.addAttribute("appId", id);
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/app-form";
    }

    @PostMapping("/apps/{id}")
    public String updateApp(@PathVariable Long id,
                             @Valid @ModelAttribute("appDto") AppListingDto dto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("appId", id);
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/app-form";
        }
        appListingService.update(id, dto);
        return "redirect:/admin/apps";
    }

    @PostMapping("/apps/{id}/delete")
    public String deleteApp(@PathVariable Long id) {
        appListingService.delete(id);
        return "redirect:/admin/apps";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/manage-users";
    }
}
