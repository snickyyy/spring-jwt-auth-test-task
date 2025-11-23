package sc.snicky.springbootjwtauth.api.v1.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.Role;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaRoleRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RolesInitializer implements CommandLineRunner {
    private final JpaRoleRepository jpaRoleRepository;

    /**
     * Initializes roles in the database at application startup.
     * Iterates through all values of the {@link ERole} enum and ensures that each role exists in the database.
     * If a role is not found, it is created and saved.
     *
     * @param args command-line arguments passed to the application
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void run(String... args) throws Exception {
        for (var role : ERole.values()) {
            log.info("init role {}", role.name());
            jpaRoleRepository.findByName(role)
                    .orElseGet(() -> jpaRoleRepository.saveAndFlush(
                            Role.builder()
                                    .name(role)
                                    .build()
                    ));
        }
    }
}
