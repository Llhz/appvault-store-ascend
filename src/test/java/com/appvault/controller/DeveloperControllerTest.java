package com.appvault.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void developerDashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/developer/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    @WithMockUser(username = "developer@appvault.com", roles = {"USER", "DEVELOPER"})
    void developerDashboardAccessibleWithDeveloperRole() throws Exception {
        mockMvc.perform(get("/developer/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("developer/dashboard"));
    }

    @Test
    @WithMockUser(username = "developer@appvault.com", roles = {"USER", "DEVELOPER"})
    void submitFormPageAccessible() throws Exception {
        mockMvc.perform(get("/developer/submit"))
                .andExpect(status().isOk())
                .andExpect(view().name("developer/submit-form"));
    }

    @Test
    @WithMockUser(username = "developer@appvault.com", roles = {"USER", "DEVELOPER"})
    void submitAppCreatesSubmission() throws Exception {
        mockMvc.perform(post("/developer/submit")
                        .param("name", "Controller Test App")
                        .param("description", "A test app submission from controller test")
                        .param("developer", "TestDev")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/developer/submission/*"));
    }

    @Test
    @WithMockUser(username = "developer@appvault.com", roles = {"USER", "DEVELOPER"})
    void submitAppWithInvalidDataShowsForm() throws Exception {
        mockMvc.perform(post("/developer/submit")
                        .param("name", "")
                        .param("description", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("developer/submit-form"));
    }

    // Admin submission review tests
    @Test
    @WithMockUser(username = "admin@appvault.com", roles = {"ADMIN"})
    void adminSubmissionQueueAccessible() throws Exception {
        mockMvc.perform(get("/admin/submissions"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/submission-queue"));
    }

    @Test
    @WithMockUser(username = "user@appvault.com", roles = {"USER"})
    void regularUserCannotAccessAdminSubmissions() throws Exception {
        mockMvc.perform(get("/admin/submissions"))
                .andExpect(status().isForbidden());
    }

    @Test
    void registerAsDeveloperRequiresAuth() throws Exception {
        mockMvc.perform(get("/developer/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    @WithMockUser(username = "user@appvault.com", roles = {"USER"})
    void registerAsDeveloperRedirectsToDashboard() throws Exception {
        mockMvc.perform(get("/developer/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/developer/dashboard"));
    }
}
