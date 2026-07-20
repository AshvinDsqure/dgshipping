package org.dspace.app.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user's session is invalidated due to a new login from another device.
 * Returns HTTP 401 Unauthorized with a specific message.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Session has been invalidated")
public class SessionInvalidatedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Your session has expired because your account was logged in from another device.";

    public SessionInvalidatedException(String message) {
        super(message);
    }

    public SessionInvalidatedException() {
        super(DEFAULT_MESSAGE);
    }
}
