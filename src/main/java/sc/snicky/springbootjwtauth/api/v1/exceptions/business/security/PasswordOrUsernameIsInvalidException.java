package sc.snicky.springbootjwtauth.api.v1.exceptions.business.security;

import sc.snicky.springbootjwtauth.api.v1.exceptions.UnauthorizedException;

public class PasswordOrUsernameIsInvalidException extends UnauthorizedException {
    /**
     * Constructs a new PasswordOrUsernameIsInvalidException exception with the specified detail message.
     *
     * @param message the detail message
     */
    public PasswordOrUsernameIsInvalidException(String message) {
        super(message);
    }
}
