package com.example.shop.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.dtos.BookDTO;
import com.example.shop.dtos.SearchRequest;
import com.example.shop.models.Book;
import com.example.shop.models.Category;
import com.example.shop.repositories.BookRepository;
import com.example.shop.repositories.CategoryRepository;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<BookDTO> getAllBooks() {
        return bookRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id)
                .filter(book -> book.getIsActive())
                .map(this::convertToDTO);
    }
    
    public Optional<BookDTO> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbnAndIsActiveTrue(isbn)
                .map(this::convertToDTO);
    }
    
    public List<BookDTO> getBooksByGrade(Integer grade) {
        return bookRepository.findByGradeAndIsActiveTrue(grade)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> getBooksBySubject(String subject) {
        return bookRepository.findBySubjectContainingIgnoreCaseAndIsActiveTrue(subject)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> getBooksByBoard(Book.Board board) {
        return bookRepository.findByBoardAndIsActiveTrue(board)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> getBooksByCategory(Long categoryId) {
        return bookRepository.findByCategoryIdAndIsActiveTrue(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(title)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCaseAndIsActiveTrue(author)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Page<BookDTO> searchBooks(SearchRequest searchRequest) {
        Sort sort = Sort.by(Sort.Direction.fromString(searchRequest.getSortDirection()), 
                           searchRequest.getSortBy());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        return bookRepository.searchBooks(
                searchRequest.getTitle(),
                searchRequest.getAuthor(),
                searchRequest.getGrade(),
                searchRequest.getSubject(),
                searchRequest.getBoard(),
                pageable
        ).map(this::convertToDTO);
    }
    
    public List<BookDTO> getLowStockBooks(Integer threshold) {
        return bookRepository.findByQuantityLessThanAndIsActiveTrue(threshold)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookDTO> getBestSellingBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findBestSellingBooks(pageable)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public BookDTO createBook(BookDTO bookDTO) {
        if (bookDTO.getIsbn() != null && bookRepository.findByIsbnAndIsActiveTrue(bookDTO.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }
        
        Book book = convertToEntity(bookDTO);
        book.setIsActive(true);
        
        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            book.setCategory(category);
        }
        
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }
    
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Check ISBN uniqueness if changed
        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.findByIsbnAndIsActiveTrue(bookDTO.getIsbn()).isPresent()) {
                throw new RuntimeException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
            }
        }
        
        BeanUtils.copyProperties(bookDTO, existingBook, "id", "createdAt", "updatedAt");
        
        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingBook.setCategory(category);
        }
        
        Book updatedBook = bookRepository.save(existingBook);
        return convertToDTO(updatedBook);
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setIsActive(false);
        bookRepository.save(book);
    }
    
    public void updateStock(Long bookId, Integer newQuantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setQuantity(newQuantity);
        bookRepository.save(book);
    }
    
    public void reduceStock(Long bookId, Integer quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (book.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + book.getQuantity() + ", Requested: " + quantity);
        }
        
        book.setQuantity(book.getQuantity() - quantity);
        bookRepository.save(book);
    }
    
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .image(book.getImage())
                .price(book.getPrice())
                .mrp(book.getMrp())
                .discount(book.getDiscount())
                .quantity(book.getQuantity())
                .grade(book.getGrade())
                .subject(book.getSubject())
                .board(book.getBoard())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .edition(book.getEdition())
                .language(book.getLanguage())
                .isActive(book.getIsActive())
                .build();
        
        if (book.getCategory() != null) {
            dto.setCategoryId(book.getCategory().getId());
            dto.setCategoryName(book.getCategory().getName());
        }
        
        return dto;
    }
    
    private Book convertToEntity(BookDTO dto) {
        return Book.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .description(dto.getDescription())
                .image(dto.getImage())
                .price(dto.getPrice())
                .mrp(dto.getMrp())
                .discount(dto.getDiscount())
                .quantity(dto.getQuantity())
                .grade(dto.getGrade())
                .subject(dto.getSubject())
                .board(dto.getBoard())
                .isbn(dto.getIsbn())
                .publisher(dto.getPublisher())
                .edition(dto.getEdition())
                .language(dto.getLanguage())
                .isActive(dto.getIsActive())
                .build();
    }
}
