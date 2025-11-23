package sc.snicky.springbootjwtauth.api.v1.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.roles.RoleNotFoundException;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.users.UserAlreadyExistException;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.users.UserNotFoundException;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaRoleRepository;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaUserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;

    /**
     * Retrieves a user by their username address.
     *
     * @param username the username address to search for; must not be {@code null}
     * @return the {@link User} with the given username
     * @throws UserNotFoundException if no user with the given username is found or the user is not active
     */
    public User getUserByUsername(String username) {
        return jpaUserRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user; must not be {@code null}
     * @return the {@link User} with the given id
     * @throws UserNotFoundException if no user with the given id is found or the user is not active
     */
    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return jpaUserRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    /**
     * Saves a new user to the repository.
     *
     * @param user the {@link User} entity to be saved; must not be {@code null}
     * @throws UserAlreadyExistException if a user with the same username already exists
     */
    @Transactional
    public void saveUser(User user) {
        try {
            jpaUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.debug("User with username {} already exists", user.getUsername());
            throw new UserAlreadyExistException("User with username " + user.getUsername() + " already exists");
        }
    }

    /**
     * Saves a new user to the repository and assigns a role to the user.
     *
     * @param user the {@link User} entity to be saved; must not be {@code null}
     * @param role the {@link ERole} to be assigned to the user; must not be {@code null}
     * @throws RoleNotFoundException if the specified role is not found
     * @throws UserAlreadyExistException if a user with the same username already exists
     */
    @Transactional
    public void saveUser(User user, ERole role) {
        try {
            user.assignRole(jpaRoleRepository.findByName(role)
                    .orElseThrow(() -> {
                        log.error("Role {} not found", role.name());
                        return new RoleNotFoundException("Role " + role.name() + " not found");
                    }));
            jpaUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.debug("User with username {} already exists", user.getUsername());
            throw new UserAlreadyExistException("User with username " + user.getUsername() + " already exists");
        }
    }
}
