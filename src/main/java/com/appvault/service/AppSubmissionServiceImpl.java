package com.appvault.service;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.*;
import com.appvault.repository.AppListingRepository;
import com.appvault.repository.AppSubmissionRepository;
import com.appvault.repository.CategoryRepository;
import com.appvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AppSubmissionServiceImpl implements AppSubmissionService {

    @Autowired
    private AppSubmissionRepository appSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AppListingRepository appListingRepository;

    @Override
    public AppSubmission createSubmission(AppSubmissionDto dto, Long userId) {
        User submitter = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        AppSubmission submission = new AppSubmission();
        submission.setSubmitter(submitter);
        submission.setName(dto.getName());
        submission.setSubtitle(dto.getSubtitle());
        submission.setDescription(dto.getDescription());
        submission.setDeveloper(dto.getDeveloper());
        submission.setIconUrl(dto.getIconUrl());
        submission.setPrice(dto.getPrice() != null ? dto.getPrice() : java.math.BigDecimal.ZERO);
        submission.setStatus(SubmissionStatus.DRAFT);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));
            submission.setCategory(category);
        }

        return appSubmissionRepository.save(submission);
    }

    @Override
    public AppSubmission submitForReview(Long submissionId, Long userId) {
        AppSubmission submission = appSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

        if (!submission.getSubmitter().getId().equals(userId)) {
            throw new AccessDeniedException("You can only submit your own submissions");
        }

        if (submission.getStatus() != SubmissionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT submissions can be submitted for review");
        }

        submission.setStatus(SubmissionStatus.PENDING_REVIEW);
        return appSubmissionRepository.save(submission);
    }

    @Override
    public AppSubmission approveSubmission(Long submissionId, String reviewNotes) {
        AppSubmission submission = appSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

        if (submission.getStatus() != SubmissionStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Only PENDING_REVIEW submissions can be approved");
        }

        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setReviewNotes(reviewNotes);
        appSubmissionRepository.save(submission);

        // Create the actual AppListing from the submission
        createAppListingFromSubmission(submission);

        return submission;
    }

    @Override
    public AppSubmission rejectSubmission(Long submissionId, String reviewNotes) {
        AppSubmission submission = appSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

        if (submission.getStatus() != SubmissionStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Only PENDING_REVIEW submissions can be rejected");
        }

        submission.setStatus(SubmissionStatus.REJECTED);
        submission.setReviewNotes(reviewNotes);
        return appSubmissionRepository.save(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppSubmission> getSubmissionsByUser(Long userId) {
        return appSubmissionRepository.findBySubmitterIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppSubmission> getPendingSubmissions() {
        return appSubmissionRepository.findByStatusOrderByCreatedAtAsc(SubmissionStatus.PENDING_REVIEW);
    }

    @Override
    @Transactional(readOnly = true)
    public AppSubmission getSubmissionById(Long id) {
        return appSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + id));
    }

    private void createAppListingFromSubmission(AppSubmission submission) {
        AppListing app = new AppListing();
        app.setName(submission.getName());
        app.setSubtitle(submission.getSubtitle());
        app.setDescription(submission.getDescription());
        app.setDeveloper(submission.getDeveloper());
        app.setIconUrl(submission.getIconUrl());
        app.setPrice(submission.getPrice());
        app.setCategory(submission.getCategory());
        app.setVersion("1.0.0");
        app.setSize("0 MB");
        app.setFeatured(false);
        app.setRating(0.0);
        app.setReviewCount(0);
        app.setDownloadCount(0L);

        appListingRepository.save(app);
    }
}
