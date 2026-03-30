package com.appvault.repository;

import com.appvault.model.AppListing;
import com.appvault.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AppListingRepository extends JpaRepository<AppListing, Long> {

    Page<AppListing> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT a FROM AppListing a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<AppListing> searchByQuery(@Param("q") String query, Pageable pageable);

    List<AppListing> findByFeaturedTrue();

    List<AppListing> findByPriceOrderByDownloadCountDesc(BigDecimal price, Pageable pageable);

    List<AppListing> findByPriceGreaterThanOrderByDownloadCountDesc(BigDecimal price, Pageable pageable);

    List<AppListing> findTop8ByOrderByCreatedAtDesc();

    List<AppListing> findTop4ByCategoryAndIdNot(Category category, Long id);
}
