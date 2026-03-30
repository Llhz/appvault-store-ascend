package com.appvault.service;

import com.appvault.dto.AppListingDto;
import com.appvault.dto.AppSuggestDto;
import com.appvault.exception.ResourceNotFoundException;
import com.appvault.model.AppListing;
import com.appvault.model.Category;
import com.appvault.model.Screenshot;
import com.appvault.repository.AppListingRepository;
import com.appvault.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppListingServiceImpl implements AppListingService {

    @Autowired
    private AppListingRepository appListingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AppListing> findAll(Pageable pageable) {
        return appListingRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppListing> findByCategory(Long categoryId, Pageable pageable) {
        return appListingRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public AppListing findById(Long id) {
        return appListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("App not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppListing> searchByQuery(String query, Pageable pageable) {
        return appListingRepository.searchByQuery(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppSuggestDto> searchSuggestions(String query, int limit) {
        Page<AppListing> results = appListingRepository.searchByQuery(query, PageRequest.of(0, limit));
        return results.getContent().stream()
                .map(app -> new AppSuggestDto(
                        app.getId(),
                        app.getName(),
                        app.getIconUrl(),
                        app.getCategory() != null ? app.getCategory().getName() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppListing> findFeatured() {
        return appListingRepository.findByFeaturedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppListing> findTopFree(int limit) {
        return appListingRepository.findByPriceOrderByDownloadCountDesc(BigDecimal.ZERO, PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppListing> findTopPaid(int limit) {
        return appListingRepository.findByPriceGreaterThanOrderByDownloadCountDesc(BigDecimal.ZERO, PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppListing> findRecent(int limit) {
        return appListingRepository.findTop8ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppListing> findRelated(AppListing app, int limit) {
        if (app.getCategory() == null) return new ArrayList<>();
        return appListingRepository.findTop4ByCategoryAndIdNot(app.getCategory(), app.getId());
    }

    @Override
    public AppListing save(AppListingDto dto) {
        AppListing app = new AppListing();
        mapDtoToEntity(dto, app);
        return appListingRepository.save(app);
    }

    @Override
    public AppListing update(Long id, AppListingDto dto) {
        AppListing app = findById(id);
        mapDtoToEntity(dto, app);
        return appListingRepository.save(app);
    }

    @Override
    public void delete(Long id) {
        AppListing app = findById(id);
        appListingRepository.delete(app);
    }

    @Override
    public void incrementDownloadCount(Long id) {
        AppListing app = findById(id);
        app.setDownloadCount(app.getDownloadCount() + 1);
        appListingRepository.save(app);
    }

    private void mapDtoToEntity(AppListingDto dto, AppListing app) {
        app.setName(dto.getName());
        app.setSubtitle(dto.getSubtitle());
        app.setDescription(dto.getDescription());
        app.setDeveloper(dto.getDeveloper());
        app.setVersion(dto.getVersion());
        app.setSize(dto.getSize());
        app.setIconUrl(dto.getIconUrl());
        app.setHeaderImageUrl(dto.getHeaderImageUrl());
        app.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        app.setFeatured(dto.isFeatured());
        app.setAgeRating(dto.getAgeRating());
        app.setCompatibility(dto.getCompatibility());

        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId())
                    .orElse(null);
            app.setCategory(cat);
        }

        // Update screenshots
        app.getScreenshots().clear();
        List<String> urls = dto.getScreenshotUrls();
        List<String> captions = dto.getScreenshotCaptions();
        if (urls != null) {
            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                if (url != null && !url.trim().isEmpty()) {
                    Screenshot ss = new Screenshot();
                    ss.setImageUrl(url.trim());
                    ss.setDisplayOrder(i);
                    if (captions != null && i < captions.size()) {
                        ss.setCaption(captions.get(i));
                    }
                    ss.setAppListing(app);
                    app.getScreenshots().add(ss);
                }
            }
        }
    }
}
