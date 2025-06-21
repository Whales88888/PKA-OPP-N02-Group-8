package com.example.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void testBookConstructorAndGetters() {
        Book book = new Book("Title", "Author");
        assertEquals("Title", book.getTitle());
        assertEquals("Author", book.getAuthor());
        assertTrue(book.isAvailable());
    }

    @Test
    void testSetters() {
        Book book = new Book();
        book.setTitle("Lão Hạc");
        book.setAuthor("Nam Cao");
        book.setAvailable(false);

        assertEquals("Lão Hạc", book.getTitle());
        assertEquals("Nam Cao", book.getAuthor());
        assertFalse(book.isAvailable());
    }
}
