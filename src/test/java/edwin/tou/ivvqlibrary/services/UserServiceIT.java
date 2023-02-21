package edwin.tou.ivvqlibrary.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.repository.UserRepository;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User jaune;

    void setup() {
        jaune = new User("Jaune", false);
    }

    void testSignedUpUserHasId() throws UserServiceException {
        assertNull(jaune.getId());
        User signedUpJaune = userService.signUpUser(jaune);
        assertNotNull(signedUpJaune.getId());
    }

    void testSignedUpUserHasGeneratedApiKey() throws UserServiceException {
        assertNull(jaune.getApiKey());
        User signedUpJaune = userService.signUpUser(jaune);
        assertNotNull(signedUpJaune.getApiKey());
    }

    void testSignedUpUserIsSaved() throws UserServiceException {
        long initialCount = userRepository.count();
        userService.signUpUser(jaune);
        assertEquals(initialCount + 1, userRepository.count());
    }

    void testSignUpUserFailOnExistingUsername() throws UserServiceException {
        User signedUp = userService.signUpUser(jaune);
        assertThrows(
            UserServiceException.class,
            () -> {
                userService.signUpUser(new User(signedUp.getUsername(), true));
            }
        );
    }

    void testDoesUserExistWithApiKeyFalse() {
        assertFalse(userService.doesUserExistsWithApiKey(UUID.randomUUID()));
    }

    void testDoesUserExistWithApiKeyTrueOnKnownedApiKey()
        throws UserServiceException {
        jaune = userService.signUpUser(jaune);
        assertFalse(userService.doesUserExistsWithApiKey(jaune.getApiKey()));
    }

    void testFindUserByApiKeyIsNullOnUnknownedApiKey() {
        assertNull(userService.findUserByApiKey(UUID.randomUUID()));
    }

    void testFindUserByApiKeyIsNotNullOnKnownedApiKey()
        throws UserServiceException {
        jaune = userService.signUpUser(jaune);
        assertNotNull(userService.findUserByApiKey(jaune.getApiKey()));
    }
}
