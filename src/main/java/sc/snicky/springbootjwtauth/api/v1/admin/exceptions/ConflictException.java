package sc.snicky.springbootjwtauth.api.v1.admin.exceptions;

public abstract class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
