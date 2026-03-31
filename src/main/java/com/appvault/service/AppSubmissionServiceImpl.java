package com.appvault.service;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.*;
import com.appvault.repository.AppListingRepository;
import com.appvault.repository.AppSubmissionRepository;
import com.appvault.repository.CategoryRepository;
import com.appvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        AppSubmission submission = new AppSubmission();
        submission.setSubmitter(user);
        mapDtoToSubmission(dto, submission);
        submission.setStatus(SubmissionStatus.DRAFT);

        return appSubmissionRepository.save(submission);
    }

    @Override
    public AppSubmission submitForReview(Long submissionId, Long userId) {
        AppSubmission submission = getSubmissionById(submissionId);

        if (!submission.getSubmitter().getId().equals(userId)) {
            throw new IllegalStateException("You can only submit your own submissions for review");
        }
        if (submission.getStatus() != SubmissionStatus.DRAFT
                && submission.getStatus() != SubmissionStatus.REJECTED) {
            throw new IllegalStateException("Only DRAFT or REJECTED submissions can be submitted for review");
        }

        submission.setStatus(SubmissionStatus.PENDING_REVIEW);
        submission.setReviewNotes(null);
        return appSubmissionRepository.save(submission);
    }

    @Override
    public AppSubmission approveSubmission(Long submissionId, String reviewNotes) {
        AppSubmission submission = getSubmissionById(submissionId);

        if (submission.getStatus() != SubmissionStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Only PENDING_REVIEW submissions can be approved");
        }

        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setReviewNotes(reviewNotes);

        // Create a real AppListing from the submission
        AppListing appListing = new AppListing();
        appListing.setName(submission.getName());
        appListing.setSubtitle(submission.getSubtitle());
        appListing.setDescription(submission.getDescription());
        appListing.setDeveloper(submission.getDeveloper());
        appListing.setIconUrl(submission.getIconUrl());
        appListing.setPrice(submission.getPrice() != null ? submission.getPrice() : BigDecimal.ZERO);
        appListing.setCategory(submission.getCategory());
        appListingRepository.save(appListing);

        return appSubmissionRepository.save(submission);
    }

    @Override
    public AppSubmission rejectSubmission(Long submissionId, String reviewNotes) {
        AppSubmission submission = getSubmissionById(submissionId);

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

    private void mapDtoToSubmission(AppSubmissionDto dto, AppSubmission submission) {
        submission.setName(dto.getName());
        submission.setSubtitle(dto.getSubtitle());
        submission.setDescription(dto.getDescription());
        submission.setDeveloper(dto.getDeveloper());
        submission.setIconUrl(dto.getIconUrl());
        submission.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);

        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            submission.setCategory(cat);
        }
    }
}
