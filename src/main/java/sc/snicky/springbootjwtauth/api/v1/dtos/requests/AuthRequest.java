package sc.snicky.springbootjwtauth.api.v1.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        // CHECKSTYLE:OFF
        @NotBlank
        @Size(max = 40, message = "Username must be at most 40 characters long")
        @Schema(description = "User username address", example = "user@example.com")
        String username,

        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$")
        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters long")
        @Schema(description = "User password with at least one digit, one lowercase, and one uppercase letter", example = "Password_123")
        String password
        // CHECKSTYLE:ON
) {
}
