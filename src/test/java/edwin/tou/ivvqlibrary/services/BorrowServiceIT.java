package edwin.tou.ivvqlibrary.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
class BorrowServiceIT {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    UserService userService;

    private Borrow empruntJaune;

    @BeforeEach
    void setup() throws UserServiceException {
        User jaune = new User("Jaune", false);
        userService.signUpUser(jaune);
        empruntJaune = new Borrow("9781484206485", jaune);
    }

    @Test
    void testSavedBorrowHasId() throws BorrowServiceException {
        assertNull(empruntJaune.getId());
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        assertNotNull(saved.getId());
    }

    @Test
    void testSavedBorrowHasDate() throws BorrowServiceException {
        assertNull(empruntJaune.getBorrowDate());
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        assertNotNull(saved.getBorrowDate());
    }

    @Test
    void testSavedBorrowHasDateNow() throws BorrowServiceException {
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        Duration offset = Duration.between(
            saved.getBorrowDate(),
            LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        );
        assertEquals(0, offset.abs().toMinutes());
    }

    @Test
    void testSaveBorrowIsSaved() throws BorrowServiceException {
        long initialCount = borrowService.count();
        borrowService.saveBorrow(empruntJaune);
        assertEquals(initialCount + 1, borrowService.count());
    }

    @Test
    void testSaveBorrowFailedOnExistingIsbn13() throws BorrowServiceException {
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        Borrow empruntRaph = new Borrow(
            saved.getIsbn13(),
            new User("Raph", false)
        );
        assertThrows(
            BorrowServiceException.class,
            () -> {
                borrowService.saveBorrow(empruntRaph);
            }
        );
    }

    @Test
    void testFindAllIsCount() {
        assertEquals(
            borrowService.count(),
            borrowService.findAllBorrow().size()
        );
    }

    @Test
    void testFindAllContainsSaved() throws BorrowServiceException {
        assertFalse(borrowService.findAllBorrow().contains(empruntJaune));
        borrowService.saveBorrow(empruntJaune);
        assertTrue(borrowService.findAllBorrow().contains(empruntJaune));
    }

    @Test
    void testFetchedCurrentByIsbn13IsNotNull() throws BorrowServiceException {
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        assertNotNull(
            borrowService.findCurrentBorrowOfIsbn13(saved.getIsbn13())
        );
    }

    @Test
    void testFetchedCurrentByIsbn13HasGoodIsbn13()
        throws BorrowServiceException {
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        Borrow fetched = borrowService.findCurrentBorrowOfIsbn13(
            saved.getIsbn13()
        );
        assertEquals(saved.getIsbn13(), fetched.getIsbn13());
    }

    @Test
    void testFetchedCurrentByIsbn13IsSameAsSaved()
        throws BorrowServiceException {
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        Borrow fetched = borrowService.findCurrentBorrowOfIsbn13(
            saved.getIsbn13()
        );
        assertNotNull(fetched);
        assertEquals(saved.getId(), fetched.getId());
        assertEquals(saved.getIsbn13(), fetched.getIsbn13());
        assertEquals(saved.getBorrower(), fetched.getBorrower());
        assertEquals(saved.getBorrowDate(), fetched.getBorrowDate());
        assertEquals(saved.getReturnDate(), fetched.getReturnDate());
    }

    @Test
    void testFetchedByIsbn13WithUnexistingIsbn13() {
        assertTrue(
            borrowService
                .findAllBorrowByIsbn13(empruntJaune.getIsbn13())
                .isEmpty()
        );
    }

    @Test
    void testReturnBookByIsbn13AndApiKeyReturnDateUpdated()
        throws BorrowServiceException {
        Borrow saved = borrowService.saveBorrow(empruntJaune);
        assertNull(saved.getReturnDate());
        Borrow returned = borrowService.returnBookByIsbn13AndApiKey(
            saved.getIsbn13(),
            empruntJaune.getBorrower().getApiKey()
        );
        assertNotNull(returned.getBorrowDate());
        assertEquals(saved.getId(), returned.getId());
        assertEquals(saved.getIsbn13(), returned.getIsbn13());
        assertEquals(saved.getBorrower(), returned.getBorrower());
    }

    @Test
    void testIsBookBorrowedByIsbn13() throws BorrowServiceException {
        assertFalse(
            borrowService.isBookCurrentlyBorrowedByIsbn13(
                empruntJaune.getIsbn13()
            )
        );
        borrowService.saveBorrow(empruntJaune);
        assertTrue(
            borrowService.isBookCurrentlyBorrowedByIsbn13(
                empruntJaune.getIsbn13()
            )
        );
    }

    @Test
    void testFindAllByNomLecteurContainsSaved() throws BorrowServiceException {
        assertFalse(
            borrowService
                .findAllBorrowByBorrowerUsername(
                    empruntJaune.getBorrower().getUsername()
                )
                .contains(empruntJaune)
        );
        borrowService.saveBorrow(empruntJaune);
        assertTrue(
            borrowService
                .findAllBorrowByBorrowerUsername(
                    empruntJaune.getBorrower().getUsername()
                )
                .contains(empruntJaune)
        );
    }

    @Test
    void testFindAllByNomLecteurCorrectName()
        throws BorrowServiceException, UserServiceException {
        User raph = new User("Raph", false);
        raph = userService.signUpUser(raph);
        Borrow empruntJaune2 = new Borrow(
            "9781484206484",
            empruntJaune.getBorrower()
        );
        Borrow empruntRaph = new Borrow("9781484206584", raph);
        borrowService.saveBorrow(empruntJaune);
        borrowService.saveBorrow(empruntJaune2);
        borrowService.saveBorrow(empruntRaph);
        List<Borrow> result = borrowService.findAllBorrowByBorrowerUsername(
            "Jaune"
        );
        result.forEach(
            borrow -> assertEquals("Jaune", borrow.getBorrower().getUsername())
        );
    }
}
