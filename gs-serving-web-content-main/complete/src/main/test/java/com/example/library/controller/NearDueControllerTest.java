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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(NearDueController.class)
public class NearDueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingSlipService borrowingSlipService;

    @Test
    void nearDueBooksPageLoads() throws Exception {
        when(borrowingSlipService.getSlipsNearDueDate(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/near-due"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("slips"));
    }
}
