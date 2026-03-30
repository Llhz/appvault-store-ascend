package com.appvault.service;

import com.appvault.model.AppListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppListingServiceTest {

    @Autowired
    private AppListingService appListingService;

    @Test
    void findAllReturnsPaginatedResults() {
        Page<AppListing> page = appListingService.findAll(PageRequest.of(0, 5));
        assertNotNull(page);
        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    void searchFindsApps() {
        Page<AppListing> results = appListingService.searchByQuery("Focus", PageRequest.of(0, 10));
        assertNotNull(results);
        assertTrue(results.getTotalElements() > 0);
    }

    @Test
    void findFeaturedReturnsApps() {
        List<AppListing> featured = appListingService.findFeatured();
        assertNotNull(featured);
        assertFalse(featured.isEmpty());
        featured.forEach(app -> assertTrue(app.isFeatured()));
    }

    @Test
    void findTopFreeApps() {
        List<AppListing> free = appListingService.findTopFree(5);
        assertNotNull(free);
        free.forEach(app -> assertEquals(0, app.getPrice().compareTo(java.math.BigDecimal.ZERO)));
    }
}
