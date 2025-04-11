package podgorskip.swift.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ParsingException extends RuntimeException {
    private HttpStatus httpStatus;

    public ParsingException(final String message) {
        super(message);
    }

    public ParsingException(final String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }
}
