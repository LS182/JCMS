package za.co.jcms.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SessionCapacityExceededException extends RuntimeException {
    public SessionCapacityExceededException(String message) {
        super(message);
    }
}
