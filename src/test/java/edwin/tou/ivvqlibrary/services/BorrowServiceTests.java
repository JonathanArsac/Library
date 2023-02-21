package edwin.tou.ivvqlibrary.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.repository.BorrowRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTests {

    @Mock
    private BorrowRepository borrowRepository;

    @InjectMocks
    private BorrowService borrowService;

    /**
     * GIVEN: un borrow service
     *
     * <p>WHEN: la méthode findAll du service est appelé
     *
     * <p>THEN: la méthode findAll du repo associé est invoqué
     */
    @Test
    void testFindAllFromRepoIsInvoke() {
        borrowService.findAllBorrow();
        Mockito.verify(borrowService.getBorrowRepository()).findAll();
    }

    /**
     * GIVEN: un borrow service
     *
     * <p>WHEN: la méthode save du service est appelé
     *
     * <p>THEN: la méthode save du repo associé est invoqué avec les bon params
     */
    @Test
    void testSaveFromRepoIsInvoke() throws BorrowServiceException {
        Borrow b = new Borrow();
        borrowService.saveBorrow(b);
        Mockito.verify(borrowService.getBorrowRepository()).save(b);
    }

    /**
     * GIVEN: un borrow service
     *
     * <p>WHEN: la méthode findCurrentBorrowOfIsbn13 du service est appelé
     *
     * <p>THEN: la méthode findByIsbn13AndReturnDateIsNull du repo associé est invoqué avec les bon
     * params
     */
    @Test
    void testFindByIsbn13FromRepoIsInvoke() {
        borrowService.findCurrentBorrowOfIsbn13("isbn13");
        Mockito
            .verify(borrowService.getBorrowRepository())
            .findByIsbn13AndReturnDateIsNull("isbn13");
    }

    /**
     * GIVEN: un borrow service
     *
     * <p>WHEN: la méthode returnBookByIsbn13AndApiKey du service est appelé
     *
     * <p>THEN: - la méthode existsByIsbn13AndReturnDateIsNull du repo associé est invoqué avec les
     * bon params - la méthode findByIsbn13AndReturnDateIsNull du repo associé est invoqué avec les
     * bon params - la méthode save du repo associé est invoqué avec les bon params
     */
    @Test
    void testReturnBookByIsbn13AndApiKeyFromRepoIsInvoke()
        throws BorrowServiceException {
        User borrower = new User("jaune", false);
        borrower.onPrePersistGenerateApiKey();
        Borrow b = new Borrow("isbn13");
        b.setBorrower(borrower);

        doReturn(true)
            .when(borrowService.getBorrowRepository())
            .existsByIsbn13AndReturnDateIsNull("isbn13");
        doReturn(b)
            .when(borrowService.getBorrowRepository())
            .findByIsbn13AndReturnDateIsNull("isbn13");

        borrowService.returnBookByIsbn13AndApiKey(
            "isbn13",
            borrower.getApiKey()
        );

        verify(borrowService.getBorrowRepository())
            .existsByIsbn13AndReturnDateIsNull("isbn13");
        verify(borrowService.getBorrowRepository())
            .findByIsbn13AndReturnDateIsNull("isbn13");
        verify(borrowService.getBorrowRepository()).save(b);
    }

    @Test
    void testReturnBookByIsbn13AndApiKeyThrowsWhenUnexistingIsbn13() {
        doReturn(true)
            .when(borrowService.getBorrowRepository())
            .existsByIsbn13AndReturnDateIsNull("isbn13");
        doReturn(null)
            .when(borrowService.getBorrowRepository())
            .findByIsbn13AndReturnDateIsNull("isbn13");
        assertThrows(
            BorrowServiceException.class,
            () -> {
                borrowService.returnBookByIsbn13AndApiKey(
                    "isbn13",
                    UUID.randomUUID()
                );
            }
        );
    }

    @Test
    void testReturnBookByIsbn13AndApiKeyThrowsWhenRepoNull() {
        doReturn(false)
            .when(borrowService.getBorrowRepository())
            .existsByIsbn13AndReturnDateIsNull("isbn13");
        assertThrows(
            BorrowServiceException.class,
            () -> {
                borrowService.returnBookByIsbn13AndApiKey(
                    "isbn13",
                    UUID.randomUUID()
                );
            }
        );
    }

    @Test
    void testReturnBookByIsbn13AndApiKeyThrowsWhenBorrowerApiKeyDifferent() {
        User borrower = new User("jaune", false);
        borrower.onPrePersistGenerateApiKey();
        Borrow b = new Borrow("isbn13");
        b.setBorrower(borrower);

        doReturn(true)
            .when(borrowService.getBorrowRepository())
            .existsByIsbn13AndReturnDateIsNull("isbn13");
        doReturn(b)
            .when(borrowService.getBorrowRepository())
            .findByIsbn13AndReturnDateIsNull("isbn13");
        assertThrows(
            BorrowServiceException.class,
            () -> {
                borrowService.returnBookByIsbn13AndApiKey(
                    "isbn13",
                    UUID.randomUUID()
                );
            }
        );
    }

    /**
     * GIVEN: un borrow service
     *
     * <p>WHEN: la méthode count du service est appelée
     *
     * <p>THEN: la méthode count du repo associé est invoqué
     */
    @Test
    void testCountFromRepoIsInvoke() {
        borrowService.count();
        Mockito.verify(borrowService.getBorrowRepository()).count();
    }

    /**
     * GIVEN: un borrow service
     *
     * <p>WHEN: la méthode save du service est appelée avec null
     *
     * <p>THEN: une IllegalArgumentException est levée
     */
    @Test
    void testSaveBorrowNullIllegalArgument() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                borrowService.saveBorrow(null);
            }
        );
    }

    @Test
    void testFindAllByIsbn13InFromRepoIsInvoke() {
        borrowService.findAllNotReturnedBorrowByIsbn13s(
            List.of("1111111111111")
        );
        verify(borrowService.getBorrowRepository())
            .findAllByIsbn13InAndReturnDateIsNull(List.of("1111111111111"));
    }

    @Test
    void testFindAllByNomLecteurFromRepoIsInvoke() {
        borrowService.findAllBorrowByBorrowerUsername("Jaune");
        verify(borrowService.getBorrowRepository())
            .findAllByBorrowerUsername("Jaune");
    }

    @Test
    void testFindAllByIsbn13FromRepoIsInvoke() {
        borrowService.findAllBorrowByIsbn13("isbn13");
        verify(borrowService.getBorrowRepository()).findAllByIsbn13("isbn13");
    }
}
