package edwin.tou.ivvqlibrary.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.repository.UserRepository;
import edwin.tou.ivvqlibrary.services.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private RestTemplate restTemplate;

    @Value("${api.basePath}")
    private String apiBasePath;

    @Value("${api.keyHeaderName}")
    private String apiKeyHeaderName;

    private String API_KEY;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUpAll() throws UserServiceException {
        User raph = new User("Raph", false);
        raph = userService.signUpUser(raph);
        API_KEY = raph.getApiKey().toString();
    }

    @AfterAll
    void tearDownAll() {
        userRepository.deleteAll();
    }

    @Test
    void testGetBookByQuery200() throws Exception {
        mockMvc
            .perform(
                get(path("/books/search/mongo"))
                    .header(apiKeyHeaderName, API_KEY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetBookByIsbn200() throws Exception {
        mockMvc
            .perform(
                get(path("/books/9781484206485"))
                    .header(apiKeyHeaderName, API_KEY)
            )
            .andExpect(status().isOk());
    }

    @Test
    void testGetBookByIsbn404() throws Exception {
        mockMvc
            .perform(
                get(path("/books/1234567891234"))
                    .header(apiKeyHeaderName, API_KEY)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetBookByQuery500() throws Exception {
        doThrow(new RestClientException("Internal server error"))
            .when(restTemplate)
            .getForObject(anyString(), any());

        mockMvc
            .perform(
                get(path("/books/search/mongo"))
                    .header(apiKeyHeaderName, API_KEY)
            )
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetBookByIsbn500() throws Exception {
        doThrow(new RestClientException("Internal server error"))
            .when(restTemplate)
            .getForObject(anyString(), any());

        mockMvc
            .perform(
                get(path("/books/1234567891234"))
                    .header(apiKeyHeaderName, API_KEY)
            )
            .andExpect(status().isInternalServerError());
    }

    private String path(String apiEndpoint) {
        return apiBasePath + apiEndpoint;
    }
}
