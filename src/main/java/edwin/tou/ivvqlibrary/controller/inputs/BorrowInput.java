package edwin.tou.ivvqlibrary.controller.inputs;

public class BorrowInput {

    private String isbn13;

    public BorrowInput() {}

    public BorrowInput(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
}
