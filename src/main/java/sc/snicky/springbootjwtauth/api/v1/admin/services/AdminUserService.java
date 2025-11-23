package sc.snicky.springbootjwtauth.api.v1.admin.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.snicky.springbootjwtauth.api.v1.admin.dtos.requests.CreateUserRequest;
import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.business.roles.RoleNameNotFoundException;
import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.business.users.UserAlreadyExistException;
import sc.snicky.springbootjwtauth.api.v1.admin.exceptions.business.users.UserNotFoundException;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.Role;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaRoleRepository;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService extends AbstractCrudServiceImpl<User, Integer> {
    private static final Integer PAGE_SIZE = 20;
    private final JpaUserRepository jpaUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JpaRoleRepository jpaRoleRepository;

    @Override
    protected JpaRepository<User, Integer> getRepo() {
        return jpaUserRepository;
    }

    @Transactional(readOnly = true)
    public Page<User> getListOfAllUsers(int page) {
        return readAll(page, PAGE_SIZE);
    }

    @Transactional
    public void changeUsernameOfUser(int userId, String newUsername) {
        try {
            var user = jpaUserRepository.getReferenceById(userId);
            user.setUsername(newUsername);
            jpaUserRepository.save(user);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Transactional
    public void changePasswordOfUser(int userId, String newPassword) {
        try {
            var user = jpaUserRepository.getReferenceById(userId);
            user.setPassword(passwordEncoder.encode(newPassword));
            jpaUserRepository.save(user);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Transactional
    public void assignRoleToUser(int userId, String roleName) {
        try {
            Role role = getRoleByName(roleName);
            User user = jpaUserRepository.getReferenceById(userId);
            user.assignRole(role);
            jpaUserRepository.save(user);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Transactional
    public void removeRoleFromUser(int userId, String roleName) {
        try {
            Role role = getRoleByName(roleName);
            User user = jpaUserRepository.getReferenceById(userId);
            user.getRoles().remove(role);
            jpaUserRepository.save(user);
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
    }

    @Transactional
    public void createUser(CreateUserRequest data) {
        try {
            Role role = jpaRoleRepository.findByName(ERole.USER).get();
            var user = User.builder()
                    .username(data.username())
                    .isActive(data.isActive())
                    .password(passwordEncoder.encode(data.password()))
                    .build();
            user.assignRole(role);
            jpaUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistException("User with username " + data.username() + " already exists.");
        }
    }

    private Role getRoleByName(String roleName) {
        try {
            return jpaRoleRepository.findByName(ERole.valueOf(roleName.trim().toUpperCase()))
                    .orElseThrow(() -> new RoleNameNotFoundException("Invalid role name: " + roleName));
        } catch (IllegalArgumentException e) {
            throw new RoleNameNotFoundException("Invalid role name: " + roleName);
        }
    }
}
