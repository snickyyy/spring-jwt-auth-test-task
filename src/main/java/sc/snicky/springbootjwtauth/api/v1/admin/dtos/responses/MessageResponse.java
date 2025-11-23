package sc.snicky.springbootjwtauth.api.v1.admin.dtos.responses;

import java.time.Instant;

public record MessageResponse(
    String message,
    Instant timestamp
) {
    public static MessageResponse of(String message) {
        return new MessageResponse(message, Instant.now());
    }
}

