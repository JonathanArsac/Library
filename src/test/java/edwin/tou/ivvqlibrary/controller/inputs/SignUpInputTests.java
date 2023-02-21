package edwin.tou.ivvqlibrary.controller.inputs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SignUpInputTests {

    private static Validator validator;

    @BeforeAll
    static void setupAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSignUpInputValid() {
        SignUpInput input = new SignUpInput("Jaune", Boolean.TRUE);
        assertTrue(validator.validate(input).isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "     " })
    void testNullUsername(String username) {
        SignUpInput input = new SignUpInput(username, Boolean.FALSE);
        assertFalse(validator.validate(input).isEmpty());
    }

    @Test
    void testNullLibraire() {
        SignUpInput input = new SignUpInput("Jaune", null);
        assertFalse(validator.validate(input).isEmpty());
    }
}
