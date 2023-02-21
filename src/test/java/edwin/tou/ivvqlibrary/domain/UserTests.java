package edwin.tou.ivvqlibrary.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTests {

    private static Validator validator;

    private User user;

    @BeforeAll
    static void setupAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        user = new User("Jaune", false);
        user.setId(1L);
        user.onPrePersistGenerateApiKey();
        user.getBorrows().add(new Borrow("1111111111111"));
    }

    @Test
    void testUserValid() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "     " })
    void testUserUsernameInvalid(String username) {
        user.setUsername(username);
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void testUserApiKeyNullInvalid() {
        user.setApiKey(null);
        assertFalse(validator.validate(user).isEmpty());
    }
}
