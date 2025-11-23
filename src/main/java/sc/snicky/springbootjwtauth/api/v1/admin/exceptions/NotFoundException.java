package sc.snicky.springbootjwtauth.api.v1.admin.exceptions;

public abstract class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
