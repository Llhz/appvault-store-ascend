package com.appvault.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Public routes should be accessible ---

    @Test
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void browsePageIsPublic() throws Exception {
        mockMvc.perform(get("/browse"))
                .andExpect(status().isOk());
    }

    @Test
    void appDetailIsPublic() throws Exception {
        mockMvc.perform(get("/app/1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchIsPublic() throws Exception {
        mockMvc.perform(get("/search").param("q", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void registerPageIsPublic() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk());
    }

    // --- Authenticated routes redirect to login ---

    @Test
    void userProfileRequiresLogin() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    void myReviewsRequiresLogin() throws Exception {
        mockMvc.perform(get("/user/my-reviews"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    void reviewSubmitRequiresLogin() throws Exception {
        mockMvc.perform(post("/review/app/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    // --- Admin routes redirect to login for anonymous ---

    @Test
    void adminDashboardRequiresLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    void adminAppsRequiresLogin() throws Exception {
        mockMvc.perform(get("/admin/apps"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    // --- Registration flow ---

    @Test
    void registerNewUserSuccessfully() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("email", "newuser@test.com")
                        .param("password", "Password1!")
                        .param("confirmPassword", "Password1!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registered"));
    }

    @Test
    void registerWithMismatchedPasswordsShowsErrors() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("email", "anotheruser@test.com")
                        .param("password", "Password1!")
                        .param("confirmPassword", "DifferentPassword!")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void registerWithExistingEmailShowsError() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("firstName", "Duplicate")
                        .param("lastName", "User")
                        .param("email", "admin@appvault.com")
                        .param("password", "Password1!")
                        .param("confirmPassword", "Password1!")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }
}
