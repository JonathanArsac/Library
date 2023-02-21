package edwin.tou.ivvqlibrary.controller;

import edwin.tou.ivvqlibrary.domain.Book;
import edwin.tou.ivvqlibrary.exceptions.BookServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import edwin.tou.ivvqlibrary.services.BookService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "${api.basePath}/books",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Pouvoir consulter les livres disponibles (borrowed = true, false).
    @GetMapping("/search/{query}")
    public List<Book> searchBook(@PathVariable String query)
        throws BookServiceException {
        return bookService.findBooks(query);
    }

    // Pouvoir consulter les détail d'un livre via son isbn13.
    @GetMapping("/{isbn13}")
    public Book bookByIsbn13(@PathVariable String isbn13)
        throws BookServiceException, NotFoundException {
        return bookService.findBookByIsbn13(isbn13);
    }

    public BookService getBookService() {
        return this.bookService;
    }
}
