package edwin.tou.ivvqlibrary.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edwin.tou.ivvqlibrary.controller.inputs.SignUpInput;
import edwin.tou.ivvqlibrary.controller.outputs.SignUpOutput;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private User jaune;

    @BeforeEach
    void setup() throws UserServiceException {
        jaune = new User(1L, "Jaune", false);
        jaune.onPrePersistGenerateApiKey();
    }

    @Test
    void testSignUpUserFromRepoInvoke() throws UserServiceException {
        when(userService.signUpUser(any())).thenReturn(new User());
        controller.signUp(new SignUpInput("Raph", Boolean.FALSE));
        verify(userService).signUpUser(new User("Raph", Boolean.FALSE));
    }

    @Test
    void testSignUpReturnGeneratedApiKeyFromService()
        throws UserServiceException {
        when(userService.signUpUser(any())).thenReturn(jaune);
        SignUpInput input = new SignUpInput(
            jaune.getUsername(),
            jaune.isLibraire()
        );
        SignUpOutput output = controller.signUp(input);

        assertEquals(jaune.getApiKey(), output.getApiKey());
    }
}
