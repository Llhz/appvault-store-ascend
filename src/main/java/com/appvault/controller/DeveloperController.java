package com.appvault.controller;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/developer")
public class DeveloperController {

    @Autowired private AppSubmissionService submissionService;
    @Autowired private UserService userService;
    @Autowired private CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = getUser(auth);
        List<AppSubmission> submissions = submissionService.getSubmissionsByUser(user.getId());

        model.addAttribute("drafts", filterByStatus(submissions, SubmissionStatus.DRAFT));
        model.addAttribute("pending", filterByStatus(submissions, SubmissionStatus.PENDING_REVIEW));
        model.addAttribute("approved", filterByStatus(submissions, SubmissionStatus.APPROVED));
        model.addAttribute("rejected", filterByStatus(submissions, SubmissionStatus.REJECTED));
        model.addAttribute("totalSubmissions", submissions.size());
        model.addAttribute("pendingCount", submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.PENDING_REVIEW).count());
        model.addAttribute("approvedCount", submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.APPROVED).count());
        model.addAttribute("rejectedCount", submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.REJECTED).count());
        return "developer/dashboard";
    }

    @GetMapping("/submit")
    public String submitForm(Model model) {
        model.addAttribute("submissionDto", new AppSubmissionDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "developer/submit-form";
    }

    @PostMapping("/submit")
    public String createSubmission(@Valid @ModelAttribute("submissionDto") AppSubmissionDto dto,
                                   BindingResult result,
                                   @RequestParam(value = "action", required = false) String action,
                                   Authentication auth, Model model) {
        User user = getUser(auth);
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "developer/submit-form";
        }
        AppSubmission submission = submissionService.createSubmission(dto, user.getId());
        if ("submit".equalsIgnoreCase(action)) {
            submissionService.submitForReview(submission.getId(), user.getId());
        }
        return "redirect:/developer/submission/" + submission.getId();
    }

    @PostMapping("/submit/{id}/review")
    public String submitForReview(@PathVariable Long id, Authentication auth) {
        User user = getUser(auth);
        submissionService.submitForReview(id, user.getId());
        return "redirect:/developer/submission/" + id;
    }

    @GetMapping("/submission/{id}")
    public String viewSubmission(@PathVariable Long id, Authentication auth, Model model) {
        User user = getUser(auth);
        AppSubmission submission = submissionService.getSubmissionById(id);
        if (!submission.getSubmitter().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Submission not found");
        }
        model.addAttribute("submission", submission);
        return "developer/submission-detail";
    }

    private User getUser(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private List<AppSubmission> filterByStatus(List<AppSubmission> submissions, SubmissionStatus status) {
        return submissions.stream()
                .filter(sub -> sub.getStatus() == status)
                .collect(Collectors.toList());
    }
}
