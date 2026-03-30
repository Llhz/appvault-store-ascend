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
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void browsePageLoads() throws Exception {
        mockMvc.perform(get("/browse"))
               .andExpect(status().isOk())
               .andExpect(view().name("app/browse"))
               .andExpect(model().attributeExists("apps", "categories"));
    }

    @Test
    void searchPageLoads() throws Exception {
        mockMvc.perform(get("/search").param("q", "Focus"))
               .andExpect(status().isOk())
               .andExpect(view().name("app/search-results"))
               .andExpect(model().attributeExists("apps", "query"));
    }

    @Test
    void appDetailPageLoads() throws Exception {
        mockMvc.perform(get("/app/1"))
               .andExpect(status().isOk())
               .andExpect(view().name("app/detail"));
    }
}
