package edwin.tou.ivvqlibrary.domain;

import java.util.ArrayList;
import java.util.List;

public class BookAPIOutput {

    private List<Book> books;

    public BookAPIOutput() {
        this.setBooks(new ArrayList<>());
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Book> getBooks() {
        return books;
    }
}
