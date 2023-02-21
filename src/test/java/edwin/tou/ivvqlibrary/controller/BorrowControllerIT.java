package edwin.tou.ivvqlibrary.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edwin.tou.ivvqlibrary.controller.inputs.BorrowInput;
import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.repository.BorrowRepository;
import edwin.tou.ivvqlibrary.repository.UserRepository;
import edwin.tou.ivvqlibrary.services.BorrowService;
import edwin.tou.ivvqlibrary.services.UserService;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class BorrowControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private ObjectMapper mapper;

    private Borrow empruntJaune;

    private User jaune;

    private User raph;

    private User libraire;

    private static final String ISBN_13 = "9781484206483";

    @Value("${api.basePath}")
    private String apiBasePath;

    @Value("${api.keyHeaderName}")
    private String apiKeyHeaderName;

    private String JAUNE_API_KEY;
    private String RAPH_API_KEY;
    private String LIBRAIRE_API_KEY;

    @BeforeAll
    void setupAll() throws UserServiceException {
        jaune = new User("Jaune", false);
        jaune = userService.signUpUser(jaune);
        JAUNE_API_KEY = jaune.getApiKey().toString();
        raph = new User("Raph", false);
        raph = userService.signUpUser(raph);
        RAPH_API_KEY = raph.getApiKey().toString();
        libraire = new User("Jhon", true);
        libraire = userService.signUpUser(libraire);
        LIBRAIRE_API_KEY = libraire.getApiKey().toString();
    }

    @BeforeEach
    void setup() throws BorrowServiceException {
        empruntJaune = new Borrow("9781484206485", jaune);
        empruntJaune = borrowService.saveBorrow(empruntJaune);
    }

    @AfterEach
    void tearDown() {
        borrowRepository.deleteAll();
    }

    @AfterAll
    void tearDownAll() {
        userRepository.deleteAll();
    }

    @Test
    void testFindAll200() throws Exception {
        mockMvc
            .perform(
                get(path("/borrows")).header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(mapper.writeValueAsString(List.of(empruntJaune)))
            );
    }

    @Test
    void testFindAllByUsername200() throws Exception {
        Borrow empruntJaune2 = new Borrow(ISBN_13, jaune);
        empruntJaune2 = borrowService.saveBorrow(empruntJaune2);
        mockMvc
            .perform(
                get(path("/borrows/search/Jaune"))
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content()
                    .json(
                        mapper.writeValueAsString(
                            List.of(empruntJaune, empruntJaune2)
                        )
                    )
            );
    }

    @Test
    void testFindAllByUsername200Filter() throws Exception {
        Borrow empruntJaune2 = new Borrow(ISBN_13, jaune);
        empruntJaune2 = borrowService.saveBorrow(empruntJaune2);

        Borrow empruntRaph = new Borrow("9781484245475", raph);
        empruntRaph = borrowService.saveBorrow(empruntRaph);
        mockMvc
            .perform(
                get(path("/borrows/search/Raph"))
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(mapper.writeValueAsString(List.of(empruntRaph)))
            );
    }

    @Test
    void testFindAllByUsername200Empty() throws Exception {
        mockMvc
            .perform(
                get(path("/borrows/search/Pierre"))
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content()
                    .json(mapper.writeValueAsString(Collections.emptyList()))
            );
    }

    @Test
    void testBorrowABook200() throws Exception {
        BorrowInput empruntInput = new BorrowInput(ISBN_13);
        String jsonInput = mapper.writeValueAsString(empruntInput);
        mockMvc
            .perform(
                post(path("/borrows"))
                    .header(apiKeyHeaderName, RAPH_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content()
                    .json(
                        mapper.writeValueAsString(
                            borrowService.findCurrentBorrowOfIsbn13(ISBN_13)
                        )
                    )
            );
    }

    @Test
    void testBorrowBookExistingIsbn13IsBadRequest() throws Exception {
        BorrowInput empruntInput = new BorrowInput(empruntJaune.getIsbn13());
        String jsonInput = mapper.writeValueAsString(empruntInput);
        mockMvc
            .perform(
                post(path("/borrows"))
                    .header(apiKeyHeaderName, RAPH_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void testFindCurrentByIsbn13200() throws Exception {
        mockMvc
            .perform(
                get(path("/borrows/current/{isbn13}"), empruntJaune.getIsbn13())
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(mapper.writeValueAsString(empruntJaune)));
    }

    @Test
    void testFindCurrentByIsbn13NotFound404() throws Exception {
        mockMvc
            .perform(
                get(path("/borrows/current/{isbn13}"), ISBN_13)
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void testReturnBook200() throws Exception {
        System.out.println(empruntJaune.getBorrowDate().toString());
        mockMvc
            .perform(
                patch(path("/borrows/{isbn13}"), empruntJaune.getIsbn13())
                    .header(apiKeyHeaderName, JAUNE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(empruntJaune.getId()))
            .andExpect(jsonPath("$.isbn13").value(empruntJaune.getIsbn13()))
            .andExpect(
                jsonPath("$.borrowDate")
                    .value(
                        empruntJaune
                            .getBorrowDate()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
            )
            .andExpect(jsonPath("$.returnDate").hasJsonPath())
            .andExpect(jsonPath("$.borrower.id").value(jaune.getId()))
            .andExpect(
                jsonPath("$.borrower.username").value(jaune.getUsername())
            )
            .andExpect(
                jsonPath("$.borrower.libraire").value(jaune.isLibraire())
            );
    }

    @Test
    void testReturnBookNotBorrowed() throws Exception {
        mockMvc
            .perform(
                patch(path("/borrows/{isbn13}"), ISBN_13)
                    .header(apiKeyHeaderName, JAUNE_API_KEY)
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllBorrowByIsbn13() throws Exception {
        mockMvc
            .perform(
                get(path("/borrows/{isbn13}"), empruntJaune.getIsbn13())
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(mapper.writeValueAsString(List.of(empruntJaune)))
            );
    }

    private String path(String apiEndpoint) {
        return apiBasePath + apiEndpoint;
    }
}
