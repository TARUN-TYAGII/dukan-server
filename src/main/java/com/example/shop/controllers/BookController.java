package com.example.shop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shop.dtos.ApiResponse;
import com.example.shop.dtos.BookDTO;
import com.example.shop.dtos.SearchRequest;
import com.example.shop.models.Book;
import com.example.shop.services.BookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        try {
            List<BookDTO> books = bookService.getAllBooks();
            return ResponseEntity.ok(ApiResponse.success(books, "Books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve books: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookById(@PathVariable Long id) {
        try {
            return bookService.getBookById(id)
                    .map(book -> ResponseEntity.ok(ApiResponse.success(book, "Book found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Book not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve book: " + e.getMessage()));
        }
    }
    
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookByIsbn(@PathVariable String isbn) {
        try {
            return bookService.getBookByIsbn(isbn)
                    .map(book -> ResponseEntity.ok(ApiResponse.success(book, "Book found")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Book not found")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve book: " + e.getMessage()));
        }
    }
    
    @GetMapping("/grade/{grade}")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByGrade(@PathVariable Integer grade) {
        try {
            List<BookDTO> books = bookService.getBooksByGrade(grade);
            return ResponseEntity.ok(ApiResponse.success(books, "Books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve books: " + e.getMessage()));
        }
    }
    
    @GetMapping("/subject/{subject}")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksBySubject(@PathVariable String subject) {
        try {
            List<BookDTO> books = bookService.getBooksBySubject(subject);
            return ResponseEntity.ok(ApiResponse.success(books, "Books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve books: " + e.getMessage()));
        }
    }
    
    @GetMapping("/board/{board}")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByBoard(@PathVariable Book.Board board) {
        try {
            List<BookDTO> books = bookService.getBooksByBoard(board);
            return ResponseEntity.ok(ApiResponse.success(books, "Books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve books: " + e.getMessage()));
        }
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByCategory(@PathVariable Long categoryId) {
        try {
            List<BookDTO> books = bookService.getBooksByCategory(categoryId);
            return ResponseEntity.ok(ApiResponse.success(books, "Books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve books: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BookDTO>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Book.Board board,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .title(title)
                    .author(author)
                    .grade(grade)
                    .subject(subject)
                    .board(board)
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
            
            Page<BookDTO> books = bookService.searchBooks(searchRequest);
            return ResponseEntity.ok(ApiResponse.success(books, "Search completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getLowStockBooks(
            @RequestParam(defaultValue = "10") Integer threshold) {
        try {
            List<BookDTO> books = bookService.getLowStockBooks(threshold);
            return ResponseEntity.ok(ApiResponse.success(books, "Low stock books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve low stock books: " + e.getMessage()));
        }
    }
    
    @GetMapping("/bestsellers")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBestSellingBooks(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<BookDTO> books = bookService.getBestSellingBooks(limit);
            return ResponseEntity.ok(ApiResponse.success(books, "Best selling books retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve best selling books: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@Valid @RequestBody BookDTO bookDTO) {
        try {
            BookDTO createdBook = bookService.createBook(bookDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdBook, "Book created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create book: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(
            @PathVariable Long id, 
            @Valid @RequestBody BookDTO bookDTO) {
        try {
            BookDTO updatedBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedBook, "Book updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update book: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Book deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete book: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @PathVariable Long id, 
            @RequestParam Integer quantity) {
        try {
            bookService.updateStock(id, quantity);
            return ResponseEntity.ok(ApiResponse.success(null, "Stock updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update stock: " + e.getMessage()));
        }
    }
}
