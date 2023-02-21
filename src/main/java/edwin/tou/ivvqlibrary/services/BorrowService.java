package edwin.tou.ivvqlibrary.services;

import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.repository.BorrowRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BorrowService {

    private BorrowRepository borrowRepository;

    public BorrowService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    public BorrowRepository getBorrowRepository() {
        return borrowRepository;
    }

    public Borrow saveBorrow(Borrow borrow) throws BorrowServiceException {
        // if null, runtime IllArgException
        if (borrow == null) {
            throw new IllegalArgumentException("Borrow cannot be null");
        }
        // if borrow exists with no return date, checked BorrowServiceException
        if (isBookCurrentlyBorrowedByIsbn13(borrow.getIsbn13())) {
            String errorMessage =
                "Book of isbn13: " + borrow.getIsbn13() + " unavailable.";
            throw new BorrowServiceException(errorMessage);
        }

        return borrowRepository.save(borrow);
    }

    public List<Borrow> findAllBorrowByIsbn13(String isbn13) {
        return borrowRepository.findAllByIsbn13(isbn13);
    }

    public Borrow findCurrentBorrowOfIsbn13(String isbn13) {
        return borrowRepository.findByIsbn13AndReturnDateIsNull(isbn13);
    }

    public List<Borrow> findAllBorrow() {
        List<Borrow> borrows = new ArrayList<>();
        borrowRepository.findAll().forEach(borrows::add);
        return borrows;
    }

    public List<Borrow> findAllNotReturnedBorrowByIsbn13s(
        Iterable<String> isbn13s
    ) {
        return borrowRepository.findAllByIsbn13InAndReturnDateIsNull(isbn13s);
    }

    public List<Borrow> findAllBorrowByBorrowerUsername(String username) {
        return borrowRepository.findAllByBorrowerUsername(username);
    }

    public boolean isBookCurrentlyBorrowedByIsbn13(String isbn13) {
        return borrowRepository.existsByIsbn13AndReturnDateIsNull(isbn13);
    }

    public long count() {
        return borrowRepository.count();
    }

    public Borrow returnBookByIsbn13AndApiKey(String isbn13, UUID userApiKey)
        throws BorrowServiceException {
        if (!isBookCurrentlyBorrowedByIsbn13(isbn13)) {
            throw new BorrowServiceException(
                "Borrow of isbn13 '" + isbn13 + "' not found."
            );
        }
        Borrow borrow = borrowRepository.findByIsbn13AndReturnDateIsNull(
            isbn13
        );
        if (borrow == null) {
            throw new BorrowServiceException(
                "Borrow of isbn13 '" + isbn13 + "' not found."
            );
        }
        if (
            !borrow
                .getBorrower()
                .getApiKey()
                .toString()
                .equals(userApiKey.toString())
        ) {
            throw new BorrowServiceException(
                "Borrower and returning user does not match."
            );
        }

        // return book by setting the return date.
        borrow.updateReturnDate();
        return borrowRepository.save(borrow);
    }
}
