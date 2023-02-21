package edwin.tou.ivvqlibrary.services;

import edwin.tou.ivvqlibrary.domain.User;
import edwin.tou.ivvqlibrary.exceptions.UserServiceException;
import edwin.tou.ivvqlibrary.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean doesUserExistsWithApiKey(UUID apiKey) {
        return userRepository.existsByApiKey(apiKey);
    }

    public User findUserByApiKey(UUID apiKey) {
        return userRepository.findByApiKey(apiKey);
    }

    public User signUpUser(User userSigningUp) throws UserServiceException {
        String username = userSigningUp.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new UserServiceException(
                "Username: '" + username + "' already taken."
            );
        }
        return userRepository.save(userSigningUp);
    }
}
