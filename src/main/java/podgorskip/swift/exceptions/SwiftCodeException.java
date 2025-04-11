package podgorskip.swift.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SwiftCodeException extends RuntimeException {
    private HttpStatus httpStatus;

    public SwiftCodeException(String message) {
        super(message);
    }

    public SwiftCodeException(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }
}