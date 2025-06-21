package com.example.library.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import com.example.library.service.BorrowingSlipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NearDueController.class)
class NearDueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingSlipService borrowingSlipService;

    @Test
    void nearDueBooksPageLoads() throws Exception {
        mockMvc.perform(get("/near-due"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("slips"));
    }
}