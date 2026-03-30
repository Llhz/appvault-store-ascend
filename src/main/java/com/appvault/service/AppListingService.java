package com.appvault.service;

import com.appvault.dto.AppListingDto;
import com.appvault.dto.AppSuggestDto;
import com.appvault.model.AppListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppListingService {
    Page<AppListing> findAll(Pageable pageable);
    Page<AppListing> findByCategory(Long categoryId, Pageable pageable);
    AppListing findById(Long id);
    Page<AppListing> searchByQuery(String query, Pageable pageable);
    List<AppSuggestDto> searchSuggestions(String query, int limit);
    List<AppListing> findFeatured();
    List<AppListing> findTopFree(int limit);
    List<AppListing> findTopPaid(int limit);
    List<AppListing> findRecent(int limit);
    List<AppListing> findRelated(AppListing app, int limit);
    AppListing save(AppListingDto dto);
    AppListing update(Long id, AppListingDto dto);
    void delete(Long id);
    void incrementDownloadCount(Long id);
}
