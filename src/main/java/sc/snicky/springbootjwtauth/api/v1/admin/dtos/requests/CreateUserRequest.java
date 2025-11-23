package sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank
    @Size(max = 40, message = "Username must be at most 40 characters long")
    String username,

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$")
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password,

    @NotNull
    boolean isActive
) {
}
