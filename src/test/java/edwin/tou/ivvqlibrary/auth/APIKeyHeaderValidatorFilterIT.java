package edwin.tou.ivvqlibrary.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edwin.tou.ivvqlibrary.controller.inputs.BorrowInput;
import edwin.tou.ivvqlibrary.controller.inputs.SignUpInput;
import edwin.tou.ivvqlibrary.domain.Book;
import edwin.tou.ivvqlibrary.domain.Borrow;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.BookServiceException;
import edwin.tou.ivvqlibrary.exceptions.BorrowServiceException;
import edwin.tou.ivvqlibrary.exceptions.NotFoundException;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.services.BookService;
import edwin.tou.ivvqlibrary.services.BorrowService;
import edwin.tou.ivvqlibrary.services.UserService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class APIKeyHeaderValidatorFilterIT {

    @Value("${api.basePath}")
    private String apiBasePath;

    @Value("${api.keyHeaderName}")
    private String apiKeyHeaderName;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User jaune;
    private User libraire;
    private String JAUNE_API_KEY;
    private String LIBRAIRE_API_KEY;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private ObjectMapper mapper;

    @SpyBean
    private APIKeyHeaderValidatorFilter spyFilter;

    @SpyBean
    private BookService bookService;

    @BeforeAll
    void setupAll()
        throws UserServiceException, BorrowServiceException, BookServiceException, NotFoundException {
        jaune = new User("Jaune", false);
        jaune = userService.signUpUser(jaune);
        JAUNE_API_KEY = jaune.getApiKey().toString();
        libraire = new User("Jhon", true);
        libraire = userService.signUpUser(libraire);
        LIBRAIRE_API_KEY = libraire.getApiKey().toString();

        borrowService.saveBorrow(new Borrow("9781484206485", jaune));

        // mock book service to prevent from spamming book api
        Book book = new Book();
        book.setIsbn13("9781484206485");
        book.setTitle("bonsoir je suis un spy mongo");
        doReturn(book).when(bookService).findBookByIsbn13(any());
        doReturn(List.of(book)).when(bookService).findBooks(any());
    }

    Stream<String> borrowGetPath() {
        return Stream.of(
            "/borrows",
            "/borrows/9781484206485",
            "/borrows/search/Jaune",
            "/borrows/current/9781484206485"
        );
    }

    Stream<String> bookGetPath() {
        return Stream.of("/books/search/azerazer", "/books/9781484206485");
    }

    @Test
    void testNoFilteringOnUserSignUpPath() throws Exception {
        String input = mapper.writeValueAsString(
            new SignUpInput("toto", Boolean.FALSE)
        );
        mockMvc
            .perform(
                post(path("/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(input)
            )
            .andExpect(status().isOk());
        verify(spyFilter, never()).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "borrowGetPath", "bookGetPath" })
    void testNoApiKeyOnApiPath400(String apiPath) throws Exception {
        mockMvc.perform(get(path(apiPath))).andExpect(status().isBadRequest());
        verify(spyFilter).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "borrowGetPath", "bookGetPath" })
    void testWrongFormatApiKeyOnApiPath400(String apiPath) throws Exception {
        mockMvc
            .perform(get(path(apiPath)).header(apiKeyHeaderName, "invalid"))
            .andExpect(status().isBadRequest());
        verify(spyFilter).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "borrowGetPath", "bookGetPath" })
    void testInvalidApiKeyOnApiPath401(String apiPath) throws Exception {
        mockMvc
            .perform(
                get(path(apiPath))
                    .header(apiKeyHeaderName, UUID.randomUUID().toString())
            )
            .andExpect(status().isUnauthorized());
        verify(spyFilter).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "borrowGetPath", "bookGetPath" })
    void testValidApiKeyOnApiPath200(String apiPath) throws Exception {
        mockMvc
            .perform(
                get(path("/books/search/mongo"))
                    .header(apiKeyHeaderName, LIBRAIRE_API_KEY)
            )
            .andExpect(status().isOk());
        verify(spyFilter).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "bookGetPath" })
    void testValidApiKeyOnBookPathForNotLibraireUser200(String apiPath)
        throws Exception {
        mockMvc
            .perform(get(path(apiPath)).header(apiKeyHeaderName, JAUNE_API_KEY))
            .andExpect(status().isOk());
        verify(spyFilter).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "borrowGetPath" })
    void testNonLibraireApiKeyOnBorrowGetPath403(String apiPath)
        throws Exception {
        mockMvc
            .perform(get(path(apiPath)).header(apiKeyHeaderName, JAUNE_API_KEY))
            .andExpect(status().isForbidden());
        verify(spyFilter).doFilterInternal(any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource({ "apiKeys" })
    void testBorrowPostAndPatchPathWithAllApiKey200(String apiKey)
        throws Exception {
        String isbn13 = randomIsbn13();
        String empruntInput = mapper.writeValueAsString(
            new BorrowInput(isbn13)
        );
        mockMvc
            .perform(
                post(path("/borrows"))
                    .header(apiKeyHeaderName, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(empruntInput)
            )
            .andExpect(status().isOk());
        mockMvc
            .perform(
                patch(path("/borrows/{isbn13}"), isbn13)
                    .header(apiKeyHeaderName, apiKey)
            )
            .andExpect(status().isOk());
        verify(spyFilter, times(2)).doFilterInternal(any(), any(), any());
    }

    Stream<String> apiKeys() {
        return Stream.of(JAUNE_API_KEY, LIBRAIRE_API_KEY);
    }

    private String path(String apiEndpoint) {
        return apiBasePath + apiEndpoint;
    }

    private String randomIsbn13() {
        return RandomStringUtils.random(13, false, true);
    }
}
