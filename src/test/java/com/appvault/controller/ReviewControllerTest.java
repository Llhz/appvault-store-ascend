package com.appvault.controller;

import com.appvault.model.User;
import com.appvault.service.UserService;
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
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void submitReviewRequiresAuth() throws Exception {
        mockMvc.perform(post("/review/app/1")
                        .param("title", "Great")
                        .param("content", "Nice app")
                        .param("rating", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void submitReviewWithValidationErrors() throws Exception {
        // Empty title and content should trigger validation errors
        mockMvc.perform(post("/review/app/1")
                        .param("title", "")
                        .param("content", "")
                        .param("rating", "0")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/app/1*"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void markHelpfulReturnsJson() throws Exception {
        mockMvc.perform(post("/review/1/helpful")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void markHelpfulRequiresAuth() throws Exception {
        mockMvc.perform(post("/review/1/helpful")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void deleteReviewRedirects() throws Exception {
        // Admin can delete review 1
        mockMvc.perform(post("/review/1/delete")
                        .param("returnUrl", "/browse")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browse"));
    }

    @Test
    void deleteReviewRequiresAuth() throws Exception {
        mockMvc.perform(post("/review/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
