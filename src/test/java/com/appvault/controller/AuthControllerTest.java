package com.appvault.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/auth/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("auth/login"));
    }

    @Test
    void registerPageLoads() throws Exception {
        mockMvc.perform(get("/auth/register"))
               .andExpect(status().isOk())
               .andExpect(view().name("auth/register"))
               .andExpect(model().attributeExists("user"));
    }

    @Test
    void adminRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
               .andExpect(status().is3xxRedirection());
    }
}
