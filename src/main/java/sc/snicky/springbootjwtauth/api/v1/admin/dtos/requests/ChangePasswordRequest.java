package sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests;

public record ChangePasswordRequest(
    String newPassword
) {
}
