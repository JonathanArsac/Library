package edwin.tou.ivvqlibrary.services;

import edwin.tou.ivvqlibrary.domain.Book;
import edwin.tou.ivvqlibrary.domain.BookAPIOutput;
import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.exceptions.BookServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class BookService {

    private static final String BASE_URI = "https://api.itbook.store/1.0/%s";
    private static final String SEARCH_URI = String.format(
        BASE_URI,
        "search/%s"
    );
    private static final String BOOK_BY_ISBN13_URI = String.format(
        BASE_URI,
        "books/%s"
    );

    private RestTemplate restTemplate;

    private BorrowService borrowService;

    public BookService(RestTemplate restTemplate, BorrowService borrowService) {
        this.restTemplate = restTemplate;
        this.borrowService = borrowService;
    }

    public List<Book> findBooks(String query) throws BookServiceException {
        BookAPIOutput books;
        try {
            books =
                restTemplate.getForObject(
                    String.format(SEARCH_URI, query),
                    BookAPIOutput.class
                );
        } catch (RestClientException e) {
            throw new BookServiceException(
                "An error occur while searching books",
                e
            );
        }

        return books != null
            ? tagBorrowedBooks(books)
            : Collections.emptyList();
    }

    public Book findBookByIsbn13(String isbn13)
        throws BookServiceException, NotFoundException {
        Book book;
        try {
            book =
                restTemplate.getForObject(
                    String.format(BOOK_BY_ISBN13_URI, isbn13),
                    Book.class
                );
        } catch (RestClientException e) {
            throw new BookServiceException(
                "An error occured while requesting book by isbn13",
                e
            );
        }

        if (book == null || !"0".equals(book.getError())) {
            throw new NotFoundException("Book not found");
        }
        return tagBookIfBorrowed(book);
    }

    private List<Book> tagBorrowedBooks(BookAPIOutput books) {
        // Map books by isbn13
        Map<String, Book> bookByIsbn13 = new HashMap<>();
        books
            .getBooks()
            .forEach(book -> bookByIsbn13.put(book.getIsbn13(), book));

        // find all corresponding borrows if exist
        List<Borrow> borrows = borrowService.findAllNotReturnedBorrowByIsbn13s(
            bookByIsbn13.keySet()
        );

        // tag borrowed books
        for (Borrow borrow : borrows) {
            bookByIsbn13.computeIfPresent(
                borrow.getIsbn13(),
                (isbn13, book) -> {
                    book.setBorrowed(true);
                    return book;
                }
            );
        }
        return books.getBooks();
    }

    private Book tagBookIfBorrowed(Book book) {
        book.setBorrowed(
            borrowService.isBookCurrentlyBorrowedByIsbn13(book.getIsbn13())
        );
        return book;
    }
}
