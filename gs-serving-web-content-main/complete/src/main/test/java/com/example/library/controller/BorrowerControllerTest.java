package com.example.library.controller;

import com.example.library.model.Borrower;
import com.example.library.service.BorrowerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowerController.class)
public class BorrowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowerService borrowerService;

    @Test
    void listBorrowersPageLoads() throws Exception {
        when(borrowerService.getAllBorrowers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/borrowers"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("borrowers"));
    }
}
