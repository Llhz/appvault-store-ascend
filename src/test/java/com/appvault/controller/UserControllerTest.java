package com.appvault.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profilePageRequiresAuth() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void profilePageLoadsForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("user", "profileDto"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void updateProfileWithValidData() throws Exception {
        mockMvc.perform(post("/user/profile")
                        .param("firstName", "Updated")
                        .param("lastName", "Admin")
                        .param("avatarUrl", "https://example.com/avatar.png")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile?saved"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void updateProfileWithBlankNameReturnsForm() throws Exception {
        mockMvc.perform(post("/user/profile")
                        .param("firstName", "")
                        .param("lastName", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"));
    }

    @Test
    void myReviewsRequiresAuth() throws Exception {
        mockMvc.perform(get("/user/my-reviews"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void myReviewsPageLoads() throws Exception {
        mockMvc.perform(get("/user/my-reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/my-reviews"))
                .andExpect(model().attributeExists("user", "reviews"));
    }
}
