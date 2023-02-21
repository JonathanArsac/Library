package edwin.tou.ivvqlibrary.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import edwin.tou.ivvqlibrary.domain.Book;
import edwin.tou.ivvqlibrary.domain.BookAPIOutput;
import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BookServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BorrowService borrowService;

    @InjectMocks
    private BookService bookService;

    private BookAPIOutput books;

    private Book book;

    @BeforeEach
    void setup() {
        book = new Book();
        book.setIsbn13("9781617294136");
        books = new BookAPIOutput();
        books.getBooks().add(book);
        book.setError("0");
    }

    @Test
    void testFindBooks() throws BookServiceException {
        String query = "https://api.itbook.store/1.0/search/mongo";
        when(restTemplate.getForObject(query, BookAPIOutput.class))
            .thenReturn(books);
        assertEquals(books.getBooks(), bookService.findBooks("mongo"));
    }

    @Test
    void testTagBorrowedBooks() throws BookServiceException {
        String query = "https://api.itbook.store/1.0/search/mongo";
        Book book2 = new Book();
        book2.setIsbn13("1234567890123");
        books.getBooks().add(book2);

        when(restTemplate.getForObject(query, BookAPIOutput.class))
            .thenReturn(books);
        when(
            borrowService.findAllNotReturnedBorrowByIsbn13s(
                Set.of(book.getIsbn13(), book2.getIsbn13())
            )
        )
            .thenReturn(
                List.of(new Borrow("9781617294136", new User("Lucas", false)))
            );

        List<Book> result = bookService.findBooks("mongo");
        assertTrue(result.get(0).isBorrowed());
        assertFalse(result.get(1).isBorrowed());
    }

    @Test
    void testFindBookByIsbn() throws BookServiceException, NotFoundException {
        String query = "https://api.itbook.store/1.0/books/9781617294136";
        when(restTemplate.getForObject(query, Book.class)).thenReturn(book);
        assertEquals(book, bookService.findBookByIsbn13("9781617294136"));
    }

    @Test
    void testTagBookIfBorrowed()
        throws BookServiceException, NotFoundException {
        String query = "https://api.itbook.store/1.0/books/9781617294136";
        when(restTemplate.getForObject(query, Book.class)).thenReturn(book);
        when(borrowService.isBookCurrentlyBorrowedByIsbn13(book.getIsbn13()))
            .thenReturn(true);

        Book result = bookService.findBookByIsbn13("9781617294136");
        assertTrue(result.isBorrowed());
    }

    @Test
    void testFindBooksEmptyOnNullFromRestTemplate()
        throws BookServiceException {
        doReturn(null).when(restTemplate).getForObject(anyString(), any());
        assertEquals(Collections.emptyList(), bookService.findBooks("mongo"));
    }

    @Test
    void testFindBooksThrowsOnRequestError() {
        doThrow(RestClientException.class)
            .when(restTemplate)
            .getForObject(anyString(), any());
        assertThrows(
            BookServiceException.class,
            () -> {
                bookService.findBooks("mongo");
            }
        );
    }

    @Test
    void testFindBookByisbn13ThrowsOnNullFromRestTemplate() {
        doReturn(null).when(restTemplate).getForObject(anyString(), any());
        assertThrows(
            NotFoundException.class,
            () -> bookService.findBookByIsbn13("9781617294136")
        );
    }

    @Test
    void testFindBookByisbn13ThrowsOnErrorFromApi() {
        book.setError("Not Found");
        doReturn(book).when(restTemplate).getForObject(anyString(), any());
        assertThrows(
            NotFoundException.class,
            () -> bookService.findBookByIsbn13("9781617294136")
        );
    }

    @Test
    void testFindBookByIsbn13ThrowsOnRequestError() {
        doThrow(RestClientException.class)
            .when(restTemplate)
            .getForObject(anyString(), any());
        assertThrows(
            BookServiceException.class,
            () -> {
                bookService.findBookByIsbn13("9781617294136");
            }
        );
    }
}
