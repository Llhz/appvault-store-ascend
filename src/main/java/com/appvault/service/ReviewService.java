package com.appvault.service;

import com.appvault.dto.ReviewDto;
import com.appvault.model.Review;
import com.appvault.model.User;

import java.util.List;

public interface ReviewService {
    Review save(ReviewDto dto, User user, Long appId);
    List<Review> findByApp(Long appId);
    List<Review> findByUser(Long userId);
    void delete(Long reviewId, User currentUser);
    void incrementHelpful(Long reviewId);
    boolean hasUserReviewedApp(Long userId, Long appId);
}
