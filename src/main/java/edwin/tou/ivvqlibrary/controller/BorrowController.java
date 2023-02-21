package edwin.tou.ivvqlibrary.controller;

import edwin.tou.ivvqlibrary.controller.inputs.BorrowInput;
import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import edwin.tou.ivvqlibrary.services.BorrowService;
import edwin.tou.ivvqlibrary.services.UserService;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "${api.basePath}/borrows",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class BorrowController {

    private BorrowService borrowService;
    private UserService userService;

    public BorrowController(
        BorrowService borrowService,
        UserService userService
    ) {
        this.borrowService = borrowService;
        this.userService = userService;
    }

    public BorrowService getBorrowService() {
        return borrowService;
    }

    public UserService getUserService() {
        return userService;
    }

    @GetMapping
    public List<Borrow> findAll() {
        return borrowService.findAllBorrow();
    }

    @GetMapping(path = "/search/{username}")
    public List<Borrow> findAllByBorrowerUsername(
        @PathVariable String username
    ) {
        return borrowService.findAllBorrowByBorrowerUsername(username);
    }

    @GetMapping(path = "/{isbn13}")
    public List<Borrow> findAllByIsbn13(@PathVariable String isbn13) {
        return borrowService.findAllBorrowByIsbn13(isbn13);
    }

    @GetMapping(path = "/current/{isbn13}")
    public Borrow findCurrentBorrowOfIsbn13(@PathVariable String isbn13)
        throws NotFoundException {
        Borrow current = borrowService.findCurrentBorrowOfIsbn13(isbn13);
        if (current == null) {
            throw new NotFoundException(
                "No borrow found for isbn13: " + isbn13
            );
        }
        return current;
    }

    @Transactional
    @PostMapping
    public Borrow borrowBook(
        @RequestBody BorrowInput input,
        @RequestHeader(name = "${api.keyHeaderName}") UUID borrowerApiKey
    ) throws BorrowServiceException {
        User borrower = userService.findUserByApiKey(borrowerApiKey);
        Borrow emprunt = new Borrow(input.getIsbn13(), borrower);
        return borrowService.saveBorrow(emprunt);
    }

    @Transactional
    @PatchMapping(path = "/{isbn13}")
    public Borrow returnBookByIsbn13(
        @PathVariable String isbn13,
        @RequestHeader(name = "${api.keyHeaderName}") UUID userApiKey
    ) throws BorrowServiceException {
        return borrowService.returnBookByIsbn13AndApiKey(isbn13, userApiKey);
    }
}
