package com.appvault.repository;

import com.appvault.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAppListingIdOrderByCreatedAtDesc(Long appId);

    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndAppListingId(Long userId, Long appId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.appListing.id = :appId")
    Double findAverageRatingByAppId(@Param("appId") Long appId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.appListing.id = :appId")
    Long countByAppListingId(@Param("appId") Long appId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> countByRatingGrouped();
}
