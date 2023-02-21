package edwin.tou.ivvqlibrary.controller;

import edwin.tou.ivvqlibrary.controller.inputs.SignUpInput;
import edwin.tou.ivvqlibrary.controller.outputs.SignUpOutput;
import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.services.UserService;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "${api.basePath}/users",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public SignUpOutput signUp(@Valid @RequestBody SignUpInput signUpInput)
        throws UserServiceException {
        User userSigningUp = new User();
        userSigningUp.setUsername(signUpInput.getUsername());
        userSigningUp.setLibraire(signUpInput.isLibraire());
        User userSignedUp = userService.signUpUser(userSigningUp);
        return SignUpOutput.of(userSignedUp.getApiKey());
    }
}
