package edwin.tou.ivvqlibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserServiceException extends Exception {

    public UserServiceException(String message) {
        super(message);
    }
}
