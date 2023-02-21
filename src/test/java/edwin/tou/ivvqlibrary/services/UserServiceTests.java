package edwin.tou.ivvqlibrary.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private static final UUID API_KEY = UUID.randomUUID();

    private User user;

    @BeforeEach
    void setup() {
        user = new User("Jaune", false);
    }

    @Test
    void testExistsWithApiKeyFromRepoIsInvoke() {
        userService.doesUserExistsWithApiKey(API_KEY);
        verify(userRepository).existsByApiKey(API_KEY);
    }

    @Test
    void testFindByApiKeyFromRepoIsInvoke() {
        userService.findUserByApiKey(API_KEY);
        verify(userRepository).findByApiKey(API_KEY);
    }

    @Test
    void testSignUpUserChecksForUsernameUnicity() throws UserServiceException {
        userService.signUpUser(user);
        verify(userRepository).existsByUsername(user.getUsername());
    }

    @Test
    void testSignUpUserSaveFromRepoIsInvoke() throws UserServiceException {
        userService.signUpUser(user);
        verify(userRepository).save(user);
    }

    @Test
    void testSignUpUserThrowsOnNonUniqueUsername() {
        when(userRepository.existsByUsername(any())).thenReturn(true);
        assertThrows(
            UserServiceException.class,
            () -> {
                userService.signUpUser(user);
            }
        );
    }
}
