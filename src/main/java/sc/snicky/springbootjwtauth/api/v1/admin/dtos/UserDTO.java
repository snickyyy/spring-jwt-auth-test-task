package sc.snicky.springbootjwtauth.api.v1.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserDTO(
    Integer id,

    @NotBlank
    @Size(min = 3, max = 40)
    String username,

    @NotBlank
    String password,

    Boolean isActive) {
}
