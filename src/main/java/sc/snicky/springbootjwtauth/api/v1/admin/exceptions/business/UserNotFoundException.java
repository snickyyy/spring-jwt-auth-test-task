package sc.snicky.springbootjwtauth.api.v1.admin.exceptions.business;

import sc.snicky.springbootjwtauth.api.v1.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    /**
     * Constructor for NotFoundException.
     *
     * @param message error message
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
