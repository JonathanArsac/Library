package edwin.tou.ivvqlibrary.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edwin.tou.ivvqlibrary.controller.inputs.BorrowInput;
import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.services.BorrowService;
import edwin.tou.ivvqlibrary.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BorrowControllerTests {

    private static final String ISBN_13 = "qsdlkfjazognazog";

    @Mock
    private BorrowService borrowService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BorrowController controller;

    private User jaune;

    @BeforeEach
    void setup() {
        jaune = new User("Jaune", false);
        jaune.onPrePersistGenerateApiKey();
    }

    @Test
    void testFindAllFromServiceIsInvoke() {
        controller.findAll();
        verify(controller.getBorrowService()).findAllBorrow();
    }

    @Test
    void testFindCurrentBorrowOfIsbn13FromServiceIsInvoke()
        throws NotFoundException {
        doReturn(new Borrow(ISBN_13))
            .when(controller.getBorrowService())
            .findCurrentBorrowOfIsbn13(any());
        controller.findCurrentBorrowOfIsbn13(ISBN_13);
        verify(controller.getBorrowService())
            .findCurrentBorrowOfIsbn13(ISBN_13);
    }

    @Test
    void testFindCurrentBorrowOfIsbn13ExistingIsbn13()
        throws NotFoundException {
        when(controller.getBorrowService().findCurrentBorrowOfIsbn13(ISBN_13))
            .thenReturn(new Borrow(ISBN_13));
        Borrow response = controller.findCurrentBorrowOfIsbn13(ISBN_13);
        assertNotNull(response);
        assertEquals(ISBN_13, response.getIsbn13());
    }

    @Test
    void testFindCurrentBorrowOfIsbn13UnexistingIsbn13Throws() {
        doReturn(null)
            .when(controller.getBorrowService())
            .findCurrentBorrowOfIsbn13(any());
        assertThrows(
            NotFoundException.class,
            () -> {
                controller.findCurrentBorrowOfIsbn13(ISBN_13);
            }
        );
    }

    @Test
    void testBorrowBookInvokeSaveFromService()
        throws BorrowServiceException, UserServiceException {
        BorrowInput input = new BorrowInput();
        input.setIsbn13(ISBN_13);
        doReturn(jaune)
            .when(controller.getUserService())
            .findUserByApiKey(jaune.getApiKey());

        controller.borrowBook(input, jaune.getApiKey());

        Borrow expected = new Borrow(ISBN_13, jaune);
        verify(controller.getBorrowService()).saveBorrow(expected);
    }

    @Test
    void testReturnBookByIsbn13InvokeDeleteByIsbn13FromService()
        throws BorrowServiceException {
        controller.returnBookByIsbn13(ISBN_13, jaune.getApiKey());
        verify(controller.getBorrowService())
            .returnBookByIsbn13AndApiKey(ISBN_13, jaune.getApiKey());
    }

    @Test
    void testFindAllByNomLecteurFromServiceIsInvoke() {
        controller.findAllByBorrowerUsername("Jaune");
        verify(controller.getBorrowService())
            .findAllBorrowByBorrowerUsername("Jaune");
    }
}
