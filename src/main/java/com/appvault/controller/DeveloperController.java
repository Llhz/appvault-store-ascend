package com.appvault.controller;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.model.AppSubmission;
import com.appvault.model.SubmissionStatus;
import com.appvault.model.User;
import com.appvault.repository.CategoryRepository;
import com.appvault.service.AppSubmissionService;
import com.appvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/developer")
public class DeveloperController {

    @Autowired
    private AppSubmissionService appSubmissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AppSubmission> submissions = appSubmissionService.getSubmissionsByUser(user.getId());

        // Group submissions by status
        Map<SubmissionStatus, List<AppSubmission>> groupedSubmissions = submissions.stream()
                .collect(Collectors.groupingBy(AppSubmission::getStatus));

        model.addAttribute("submissions", submissions);
        model.addAttribute("draftSubmissions", groupedSubmissions.getOrDefault(SubmissionStatus.DRAFT, List.of()));
        model.addAttribute("pendingSubmissions", groupedSubmissions.getOrDefault(SubmissionStatus.PENDING_REVIEW, List.of()));
        model.addAttribute("approvedSubmissions", groupedSubmissions.getOrDefault(SubmissionStatus.APPROVED, List.of()));
        model.addAttribute("rejectedSubmissions", groupedSubmissions.getOrDefault(SubmissionStatus.REJECTED, List.of()));

        // Stats
        model.addAttribute("totalSubmissions", submissions.size());
        model.addAttribute("pendingCount", groupedSubmissions.getOrDefault(SubmissionStatus.PENDING_REVIEW, List.of()).size());
        model.addAttribute("approvedCount", groupedSubmissions.getOrDefault(SubmissionStatus.APPROVED, List.of()).size());
        model.addAttribute("rejectedCount", groupedSubmissions.getOrDefault(SubmissionStatus.REJECTED, List.of()).size());

        return "developer/dashboard";
    }

    @GetMapping("/submit")
    public String submitForm(Model model) {
        model.addAttribute("submissionDto", new AppSubmissionDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "developer/submit-form";
    }

    @PostMapping("/submit")
    public String submitApp(@Valid @ModelAttribute("submissionDto") AppSubmissionDto dto,
                             BindingResult result, Authentication auth, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "developer/submit-form";
        }

        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        appSubmissionService.createSubmission(dto, user.getId());
        return "redirect:/developer/dashboard";
    }

    @PostMapping("/submit/{id}/review")
    public String submitForReview(@PathVariable Long id, Authentication auth) {
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        appSubmissionService.submitForReview(id, user.getId());
        return "redirect:/developer/dashboard";
    }

    @GetMapping("/submission/{id}")
    public String submissionDetail(@PathVariable Long id, Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppSubmission submission = appSubmissionService.getSubmissionById(id);

        // Verify the user owns this submission
        if (!submission.getSubmitter().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You can only view your own submissions");
        }

        model.addAttribute("submission", submission);
        return "developer/submission-detail";
    }
}
