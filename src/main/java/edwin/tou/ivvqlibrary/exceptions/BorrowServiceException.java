package edwin.tou.ivvqlibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BorrowServiceException extends Exception {

    public BorrowServiceException(String message) {
        super(message);
    }
}
