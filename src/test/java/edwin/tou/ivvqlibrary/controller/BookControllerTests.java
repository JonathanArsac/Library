package edwin.tou.ivvqlibrary.controller;

import static org.mockito.Mockito.verify;

import edwin.tou.ivvqlibrary.exceptions.BookServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import edwin.tou.ivvqlibrary.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class BookControllerTests {

    private static final String QUERY = "mongo";

    private static final String ISBN_13 = "qsdlkfjazognazog";
    private BookController bookController;

    @MockBean
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        bookController = new BookController(bookService);
    }

    @Test
    void testGetBookByQueryFromServiceIsInvoke() throws BookServiceException {
        // when: on récupère dans le contrôleur la liste des books à partir d'une query
        bookController.searchBook(QUERY);

        // then: la requête est traitée par le service correspondant
        verify(bookController.getBookService()).findBooks(QUERY);
    }

    @Test
    void testGetBookByIsbnFromServiceIsInvoke()
        throws BookServiceException, NotFoundException {
        // when: on récupère dans le contrôleur le book par son isbn
        bookController.bookByIsbn13(ISBN_13);

        // then: la requête est traitée par le service correspondant
        verify(bookController.getBookService()).findBookByIsbn13(ISBN_13);
    }
}
