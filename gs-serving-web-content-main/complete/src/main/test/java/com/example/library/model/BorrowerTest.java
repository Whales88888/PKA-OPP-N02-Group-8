package com.example.library.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BorrowerTest {

    @Test
    void testBorrowerConstructorAndGetters() {
        Borrower borrower = new Borrower("Name");
        assertEquals("Name", borrower.getName());
    }
}
