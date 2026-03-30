package com.appvault.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Downloads stats ---

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void downloadsStatsReturnsJsonArray() throws Exception {
        mockMvc.perform(get("/admin/api/stats/downloads"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].name", is(notNullValue())))
                .andExpect(jsonPath("$[0].downloadCount", is(notNullValue())));
    }

    // --- Ratings stats ---

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void ratingsStatsReturnsRatingDistribution() throws Exception {
        mockMvc.perform(get("/admin/api/stats/ratings"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].rating", is(1)))
                .andExpect(jsonPath("$[0].count", is(notNullValue())))
                .andExpect(jsonPath("$[4].rating", is(5)))
                .andExpect(jsonPath("$[4].count", is(notNullValue())));
    }

    // --- Categories stats ---

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void categoriesStatsReturnsCategoryCounts() throws Exception {
        mockMvc.perform(get("/admin/api/stats/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].category", is(notNullValue())))
                .andExpect(jsonPath("$[0].count", is(notNullValue())));
    }

    // --- Access control ---

    @Test
    void downloadsStatsRequiresAuth() throws Exception {
        mockMvc.perform(get("/admin/api/stats/downloads"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void downloadsStatsForbiddenForRegularUser() throws Exception {
        mockMvc.perform(get("/admin/api/stats/downloads"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void ratingsStatsForbiddenForRegularUser() throws Exception {
        mockMvc.perform(get("/admin/api/stats/ratings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void categoriesStatsForbiddenForRegularUser() throws Exception {
        mockMvc.perform(get("/admin/api/stats/categories"))
                .andExpect(status().isForbidden());
    }
}
