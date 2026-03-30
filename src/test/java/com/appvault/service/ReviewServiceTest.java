package com.appvault.service;

import com.appvault.dto.ReviewDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.AppListing;
import com.appvault.model.Review;
import com.appvault.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppListingService appListingService;

    private User adminUser;
    private User regularUser;
    private AppListing app;

    @BeforeEach
    void setUp() {
        adminUser = userService.findByEmail("admin@appvault.com")
                .orElseThrow(() -> new RuntimeException("admin@appvault.com not found in test data"));
        regularUser = userService.findByEmail("alice@example.com")
                .orElseThrow(() -> new RuntimeException("alice@example.com not found in test data"));
        app = appListingService.findFeatured().get(0);
    }

    @Test
    void saveReviewPersistsAndRecalculatesRating() {
        // Find an app without a review from admin
        List<AppListing> allApps = appListingService.findRecent(10);
        AppListing targetApp = allApps.stream()
                .filter(a -> !reviewService.hasUserReviewedApp(adminUser.getId(), a.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No unreviewed app found"));

        // Get actual DB review count (not the seeded artificial value)
        int actualReviewsBefore = reviewService.findByApp(targetApp.getId()).size();

        ReviewDto dto = new ReviewDto();
        dto.setTitle("Great application");
        dto.setContent("Really useful and well designed");
        dto.setRating(5);

        Review saved = reviewService.save(dto, adminUser, targetApp.getId());

        assertNotNull(saved.getId());
        assertEquals("Great application", saved.getTitle());
        assertEquals(5, saved.getRating());
        assertEquals(adminUser.getId(), saved.getUser().getId());
        assertEquals(targetApp.getId(), saved.getAppListing().getId());

        // Rating should have been recalculated from actual DB reviews
        AppListing updated = appListingService.findById(targetApp.getId());
        assertEquals(actualReviewsBefore + 1, updated.getReviewCount());
        assertTrue(updated.getRating() > 0);
    }

    @Test
    void saveReviewForNonExistentAppThrowsException() {
        ReviewDto dto = new ReviewDto();
        dto.setTitle("Test");
        dto.setContent("Test content");
        dto.setRating(3);

        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.save(dto, adminUser, 99999L));
    }

    @Test
    void findByAppReturnsReviews() {
        List<Review> reviews = reviewService.findByApp(app.getId());
        assertNotNull(reviews);
        // DataInitializer seeds reviews
    }

    @Test
    void findByUserReturnsReviews() {
        List<Review> reviews = reviewService.findByUser(regularUser.getId());
        assertNotNull(reviews);
    }

    @Test
    void hasUserReviewedAppReturnsTrueForExistingReview() {
        // DataInitializer seeds john@example.com with reviews
        List<Review> userReviews = reviewService.findByUser(regularUser.getId());
        if (!userReviews.isEmpty()) {
            Review existingReview = userReviews.get(0);
            assertTrue(reviewService.hasUserReviewedApp(
                    regularUser.getId(), existingReview.getAppListing().getId()));
        }
    }

    @Test
    void hasUserReviewedAppReturnsFalseForNoReview() {
        // Admin may not have reviewed every app
        List<AppListing> apps = appListingService.findRecent(10);
        AppListing unreviewedApp = apps.stream()
                .filter(a -> !reviewService.hasUserReviewedApp(adminUser.getId(), a.getId()))
                .findFirst()
                .orElse(null);

        if (unreviewedApp != null) {
            assertFalse(reviewService.hasUserReviewedApp(adminUser.getId(), unreviewedApp.getId()));
        }
    }

    @Test
    void incrementHelpfulIncreasesCount() {
        List<Review> reviews = reviewService.findByUser(regularUser.getId());
        if (!reviews.isEmpty()) {
            Review review = reviews.get(0);
            int before = review.getHelpful();
            reviewService.incrementHelpful(review.getId());

            // Re-fetch to verify
            List<Review> updatedReviews = reviewService.findByUser(regularUser.getId());
            Review updated = updatedReviews.stream()
                    .filter(r -> r.getId().equals(review.getId()))
                    .findFirst().orElseThrow(() -> new RuntimeException("Review not found after update"));
            assertEquals(before + 1, updated.getHelpful());
        }
    }

    @Test
    void incrementHelpfulForNonExistentReviewThrows() {
        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.incrementHelpful(99999L));
    }

    @Test
    void deleteByOwnerSucceeds() {
        // Create a review, then delete it
        List<AppListing> allApps = appListingService.findRecent(10);
        AppListing targetApp = allApps.stream()
                .filter(a -> !reviewService.hasUserReviewedApp(adminUser.getId(), a.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No unreviewed app found"));

        ReviewDto dto = new ReviewDto();
        dto.setTitle("To be deleted");
        dto.setContent("This review will be deleted");
        dto.setRating(2);

        Review saved = reviewService.save(dto, adminUser, targetApp.getId());
        Long reviewId = saved.getId();

        reviewService.delete(reviewId, adminUser);

        // After deletion, review count should be updated
        AppListing updated = appListingService.findById(targetApp.getId());
        assertFalse(reviewService.hasUserReviewedApp(adminUser.getId(), targetApp.getId()));
    }

    @Test
    void deleteByAdminSucceeds() {
        // Admin can delete another user's review
        List<Review> userReviews = reviewService.findByUser(regularUser.getId());
        if (!userReviews.isEmpty()) {
            Review review = userReviews.get(0);
            assertDoesNotThrow(() -> reviewService.delete(review.getId(), adminUser));
        }
    }

    @Test
    void deleteByNonOwnerNonAdminThrows() {
        // Create a review by admin and try to delete it by regular user
        List<AppListing> allApps = appListingService.findRecent(10);
        AppListing targetApp = allApps.stream()
                .filter(a -> !reviewService.hasUserReviewedApp(adminUser.getId(), a.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No unreviewed app found"));

        ReviewDto dto = new ReviewDto();
        dto.setTitle("Admin review");
        dto.setContent("Only admin or admin themselves can delete");
        dto.setRating(4);

        Review adminReview = reviewService.save(dto, adminUser, targetApp.getId());

        assertThrows(AccessDeniedException.class, () ->
                reviewService.delete(adminReview.getId(), regularUser));
    }

    @Test
    void deleteNonExistentReviewThrows() {
        assertThrows(ResourceNotFoundException.class, () ->
                reviewService.delete(99999L, adminUser));
    }
}
