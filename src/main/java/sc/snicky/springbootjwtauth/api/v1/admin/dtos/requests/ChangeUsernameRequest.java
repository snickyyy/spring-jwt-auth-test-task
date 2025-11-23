package sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest(
        @NotBlank
        @Size(max = 40, message = "Username must be at most 40 characters long")
        String newUsername
) {
}
