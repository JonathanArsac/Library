package edwin.tou.ivvqlibrary.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edwin.tou.ivvqlibrary.controller.inputs.SignUpInput;
import edwin.tou.ivvqlibrary.controller.outputs.SignUpOutput;
import edwin.tou.ivvqlibrary.repository.UserRepository;
import edwin.tou.ivvqlibrary.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${api.basePath}")
    private String apiBasePath;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        clearContext();
    }

    @AfterEach
    void tearDown() {
        clearContext();
    }

    void clearContext() {
        userRepository.deleteAll();
    }

    @Test
    void testSignUpWithValidInputsCreateNewUser200Ok() throws Exception {
        SignUpInput input = new SignUpInput("Jaune", Boolean.FALSE);
        String response = mockMvc
            .perform(
                post(path("/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(input))
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        SignUpOutput output = mapper.readValue(response, SignUpOutput.class);
        assertTrue(userService.doesUserExistsWithApiKey(output.getApiKey()));
    }

    @Test
    void testSignUpWithInvalidInputs400BadRequest() throws Exception {
        SignUpInput input = new SignUpInput(" ", Boolean.FALSE);

        mockMvc
            .perform(
                post(path("/users"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(input))
            )
            .andExpect(status().isBadRequest());
    }

    private String path(String apiEndpoint) {
        return apiBasePath + apiEndpoint;
    }
}
