package edwin.tou.ivvqlibrary.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** BorrowTests */
class BorrowTests {

    private static Validator validator;

    private User jaune;

    @BeforeAll
    static void setupAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        jaune = new User("Jaune", false);
    }

    /**
     * GIVEN : une Entité Borrow emprunt avec un isbn13, un nom de lecteur et une date d'emprunt WHEN
     * : emprunt crée THEN : validé par le validator
     */
    @Test
    void borrowValid() {
        Borrow emprunt = new Borrow(
            1L,
            "abcdefghijklm",
            jaune,
            LocalDateTime.now()
        );
        assertTrue(validator.validate(emprunt).isEmpty());
    }

    /**
     * given: une Entité Borrow emprunt avec un isbn13 invalide, when: emprunt validé par le validator
     * then: emprunt est non valide
     *
     * @param isbn13 All invalid isbn13 -> null, empty, less than 13, greater than 13
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "azer", "azereeeeeeee", "azertyuiopazer" })
    void borrowIsbn13Invalid(String isbn13) {
        Borrow emprunt = new Borrow(isbn13, jaune, LocalDateTime.now());
        assertFalse(validator.validate(emprunt).isEmpty());
    }

    /**
     * GIVEN : une Entité Borrow emprunt avec une date null WHEN : emprunt crée THEN : invalidé par le
     * validator
     */
    @Test
    void borrowDateInvalid() {
        Borrow emprunt = new Borrow("abcdefghijklm", jaune, null);
        assertFalse(validator.validate(emprunt).isEmpty());
    }

    /**
     * GIVEN : une Entité Borrow emprunt avec un nom de lecteur null GIVEN : une Entité Borrow emprunt
     * avec un nom de lecteur vide WHEN : emprunt crée THEN : invalidé par le validator
     */
    @Test
    void borrowBorrowerNull() {
        Borrow empruntNull = new Borrow(
            "abcdefghijklm",
            null,
            LocalDateTime.now()
        );
        assertFalse(validator.validate(empruntNull).isEmpty());
    }
}
