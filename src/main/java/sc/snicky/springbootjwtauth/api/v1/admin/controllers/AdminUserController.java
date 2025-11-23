package sc.snicky.springbootjwtauth.api.v1.admin.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.UserDTO;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests.ChangePasswordRequest;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests.ChangeRoleRequest;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests.ChangeUsernameRequest;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests.CreateUserRequest;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.responses.MessageResponse;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.responses.PageResponse;
import sc.snicky.springbootjwtauth.api.v1.admin.mappers.UserDtoMapper;
import sc.snicky.springbootjwtauth.api.v1.admin.services.AdminUserService;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;

@RequestMapping("/api/v1/admin/users")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping
    public ResponseEntity<MessageResponse> createDefaultUser(@Validated @RequestBody CreateUserRequest request) {
        adminUserService.createUser(request);
        return ResponseEntity.ok(MessageResponse.of("User created successfully"));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page) {
        Page<User> usersPage = adminUserService.getListOfAllUsers(page);
        PageResponse<UserDTO> response = new PageResponse<>(
                usersPage.getContent().stream().map(userDtoMapper::toUserDTO).toList(),
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/field/username")
    public ResponseEntity<MessageResponse> changeUsername(
            @PathVariable int id,
            @Validated @RequestBody ChangeUsernameRequest request) {
        adminUserService.changeUsernameOfUser(id, request.newUsername());
        return ResponseEntity.ok(MessageResponse.of("Username changed successfully"));
    }

    @PatchMapping("/{id}/field/password")
    public ResponseEntity<MessageResponse> changePassword(
            @PathVariable int id,
            @Validated @RequestBody ChangePasswordRequest request) {
        adminUserService.changePasswordOfUser(id, request.newPassword());
        return ResponseEntity.ok(MessageResponse.of("Password changed successfully"));
    }

    @PatchMapping("/{id}/field/role")
    public ResponseEntity<MessageResponse> assignRoleToUser(
            @PathVariable int id,
            @Validated @RequestBody ChangeRoleRequest request) {
        adminUserService.assignRoleToUser(id, request.role());
        return ResponseEntity.ok(MessageResponse.of("Role assigned successfully"));
    }

    @DeleteMapping("/{id}/field/role")
    public ResponseEntity<MessageResponse> removeRoleFromUser(
            @PathVariable int id,
            @Validated @RequestBody ChangeRoleRequest request) {
        adminUserService.removeRoleFromUser(id, request.role());
        return ResponseEntity.ok(MessageResponse.of("Role removed successfully"));
    }

}
