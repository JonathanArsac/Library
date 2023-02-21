package edwin.tou.ivvqlibrary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class BookServiceException extends Exception {

    public BookServiceException(String s, RestClientException e) {
        super(s, e);
    }
}
