package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Test
    void getAllBooksReturnsList() {
        BookRepository repo = mock(BookRepository.class);
        when(repo.findAll()).thenReturn(Collections.emptyList());

        BookService service = new BookService();
        service.bookRepository = repo;

        List<Book> result = service.getAllBooks();
        assertNotNull(result);
    }
}
