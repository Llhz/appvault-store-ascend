package com.appvault.service;

import com.appvault.dto.ReviewDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.AppListing;
import com.appvault.model.Review;
import com.appvault.model.User;
import com.appvault.repository.AppListingRepository;
import com.appvault.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppListingRepository appListingRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Review save(ReviewDto dto, User user, Long appId) {
        AppListing app = appListingRepository.findById(appId)
                .orElseThrow(() -> new ResourceNotFoundException("App not found: " + appId));

        Review review = new Review();
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());
        review.setRating(dto.getRating());
        review.setUser(user);
        review.setAppListing(app);

        Review saved = reviewRepository.save(review);
        recalculateRating(app);
        notifyOtherReviewers(app, user);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByApp(Long appId) {
        return reviewRepository.findByAppListingIdOrderByCreatedAtDesc(appId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void delete(Long reviewId, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
        boolean isOwner = review.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You cannot delete this review");
        }

        AppListing app = review.getAppListing();
        reviewRepository.delete(review);
        recalculateRating(app);
    }

    @Override
    public void incrementHelpful(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        review.setHelpful(review.getHelpful() + 1);
        reviewRepository.save(review);

        Long authorId = review.getUser().getId();
        String appName = review.getAppListing().getName();
        Long appId = review.getAppListing().getId();
        notificationService.createNotification(authorId,
                "Your review on " + appName + " was marked helpful",
                "/app/" + appId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedApp(Long userId, Long appId) {
        return reviewRepository.existsByUserIdAndAppListingId(userId, appId);
    }

    private void notifyOtherReviewers(AppListing app, User currentUser) {
        List<Review> existingReviews = reviewRepository.findByAppListingIdOrderByCreatedAtDesc(app.getId());
        Set<Long> notified = new HashSet<>();
        for (Review r : existingReviews) {
            Long uid = r.getUser().getId();
            if (!uid.equals(currentUser.getId()) && notified.add(uid)) {
                notificationService.createNotification(uid,
                        "Someone also reviewed " + app.getName(),
                        "/app/" + app.getId());
            }
        }
    }

    private void recalculateRating(AppListing app) {
        Double avg = reviewRepository.findAverageRatingByAppId(app.getId());
        Long count = reviewRepository.countByAppListingId(app.getId());
        app.setRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        app.setReviewCount(count != null ? count.intValue() : 0);
        appListingRepository.save(app);
    }
}
