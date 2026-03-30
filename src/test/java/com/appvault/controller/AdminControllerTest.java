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
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Access control tests ---

    @Test
    void dashboardRequiresAuth() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void dashboardForbiddenForRegularUser() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void dashboardLoadsForAdmin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("totalApps", "totalUsers", "totalReviews", "featuredApps", "recentApps"));
    }

    // --- App management tests ---

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void listAppsPageLoads() throws Exception {
        mockMvc.perform(get("/admin/apps"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/manage-apps"))
                .andExpect(model().attributeExists("apps"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void newAppFormLoads() throws Exception {
        mockMvc.perform(get("/admin/apps/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/app-form"))
                .andExpect(model().attributeExists("appDto", "categories"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void createAppWithValidData() throws Exception {
        mockMvc.perform(post("/admin/apps")
                        .param("name", "Test App")
                        .param("description", "A test application for unit testing purposes")
                        .param("developer", "Test Developer")
                        .param("version", "1.0")
                        .param("size", "10 MB")
                        .param("price", "0.00")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apps"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void createAppWithInvalidDataShowsForm() throws Exception {
        mockMvc.perform(post("/admin/apps")
                        .param("name", "")
                        .param("description", "")
                        .param("developer", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/app-form"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void editAppFormLoads() throws Exception {
        mockMvc.perform(get("/admin/apps/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/app-form"))
                .andExpect(model().attributeExists("appDto", "appId", "categories"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void updateAppWithValidData() throws Exception {
        mockMvc.perform(post("/admin/apps/1")
                        .param("name", "Updated App")
                        .param("description", "Updated description for the application")
                        .param("developer", "Updated Developer")
                        .param("version", "2.0")
                        .param("size", "20 MB")
                        .param("price", "1.99")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apps"));
    }

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void deleteApp() throws Exception {
        mockMvc.perform(post("/admin/apps/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/apps"));
    }

    // --- User management tests ---

    @Test
    @WithMockUser(username = "admin@appvault.com", roles = "ADMIN")
    void listUsersPageLoads() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/manage-users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void regularUserCannotAccessApps() throws Exception {
        mockMvc.perform(get("/admin/apps"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void regularUserCannotCreateApps() throws Exception {
        mockMvc.perform(post("/admin/apps")
                        .param("name", "Sneaky App")
                        .param("description", "Should not be allowed")
                        .param("developer", "Hacker")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void regularUserCannotDeleteApps() throws Exception {
        mockMvc.perform(post("/admin/apps/1/delete")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
