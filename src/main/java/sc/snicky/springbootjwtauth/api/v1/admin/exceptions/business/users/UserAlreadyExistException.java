package sc.snicky.springbootjwtauth.api.v1.admin.exceptions.business.users;

import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.ConflictException;

public class UserAlreadyExistException extends ConflictException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
