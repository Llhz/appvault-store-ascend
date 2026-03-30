package com.appvault.controller;

import com.appvault.service.AppListingService;
import com.appvault.service.ReviewService;
import com.appvault.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired private AppListingService appListingService;
    @Autowired private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredApps", appListingService.findFeatured());
        model.addAttribute("topFreeApps", appListingService.findTopFree(8));
        model.addAttribute("topPaidApps", appListingService.findTopPaid(8));
        model.addAttribute("recentApps", appListingService.findRecent(8));
        model.addAttribute("categories", categoryRepository.findAll());
        return "home";
    }
}
