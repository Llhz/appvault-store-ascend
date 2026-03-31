package com.appvault.service;

import com.appvault.dto.AppSubmissionDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.AppListing;
import com.appvault.model.AppSubmission;
import com.appvault.model.Category;
import com.appvault.model.SubmissionStatus;
import com.appvault.model.User;
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

    @Autowired private AppSubmissionRepository submissionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AppListingRepository appListingRepository;

    @Override
    public AppSubmission createSubmission(AppSubmissionDto dto, Long userId) {
        User submitter = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        AppSubmission submission = new AppSubmission();
        submission.setSubmitter(submitter);
        mapDtoToSubmission(dto, submission);
        submission.setStatus(SubmissionStatus.DRAFT);
        return submissionRepository.save(submission);
    }

    @Override
    public AppSubmission submitForReview(Long submissionId, Long userId) {
        AppSubmission submission = getOwnedSubmission(submissionId, userId);
        if (submission.getStatus() == SubmissionStatus.DRAFT || submission.getStatus() == SubmissionStatus.REJECTED) {
            submission.setStatus(SubmissionStatus.PENDING_REVIEW);
            submission.setReviewNotes(null);
            return submissionRepository.save(submission);
        }
        return submission;
    }

    @Override
    public AppSubmission approveSubmission(Long submissionId, String reviewNotes) {
        AppSubmission submission = getSubmissionById(submissionId);
        if (submission.getStatus() != SubmissionStatus.PENDING_REVIEW) {
            return submission;
        }

        AppListing listing = new AppListing();
        listing.setName(submission.getName());
        listing.setSubtitle(submission.getSubtitle());
        listing.setDescription(submission.getDescription());
        listing.setDeveloper(submission.getDeveloper() != null ? submission.getDeveloper() : submission.getSubmitter().getFullName());
        listing.setIconUrl(submission.getIconUrl());
        listing.setPrice(submission.getPrice() != null ? submission.getPrice() : BigDecimal.ZERO);
        listing.setFeatured(false);
        listing.setCompatibility(null);
        listing.setAgeRating(null);

        if (submission.getCategory() != null) {
            listing.setCategory(submission.getCategory());
        }

        AppListing savedListing = appListingRepository.save(listing);

        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setReviewNotes(reviewNotes);
        submission.setAppListing(savedListing);
        return submissionRepository.save(submission);
    }

    @Override
    public AppSubmission rejectSubmission(Long submissionId, String reviewNotes) {
        AppSubmission submission = getSubmissionById(submissionId);
        if (submission.getStatus() == SubmissionStatus.PENDING_REVIEW) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setReviewNotes(reviewNotes);
            return submissionRepository.save(submission);
        }
        return submission;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppSubmission> getSubmissionsByUser(Long userId) {
        return submissionRepository.findBySubmitterIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppSubmission> getPendingSubmissions() {
        return submissionRepository.findByStatusOrderByCreatedAtAsc(SubmissionStatus.PENDING_REVIEW);
    }

    @Override
    @Transactional(readOnly = true)
    public AppSubmission getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + id));
    }

    private AppSubmission getOwnedSubmission(Long submissionId, Long userId) {
        AppSubmission submission = getSubmissionById(submissionId);
        if (!submission.getSubmitter().getId().equals(userId)) {
            throw new ResourceNotFoundException("Submission not found for user");
        }
        return submission;
    }

    private void mapDtoToSubmission(AppSubmissionDto dto, AppSubmission submission) {
        submission.setName(dto.getName());
        submission.setSubtitle(dto.getSubtitle());
        submission.setDescription(dto.getDescription());
        submission.setDeveloper(dto.getDeveloper());
        submission.setIconUrl(dto.getIconUrl());
        submission.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            submission.setCategory(category);
        } else {
            submission.setCategory(null);
        }
    }
}
