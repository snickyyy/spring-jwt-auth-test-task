package sc.snicky.springbootjwtauth.api.v1.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.Role;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaRoleRepository;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaUserRepository;

//@Profile("dev")
@Slf4j
@Component
@Order
@RequiredArgsConstructor
public class RootUserInitializer implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("init root user");
        if (!jpaUserRepository.findByUsernameAndIsActiveTrue("admin").isPresent()) {
            Role role = jpaRoleRepository.findByName(ERole.ADMIN).get();
            var user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin12."))
                    .isActive(true)
                    .build();
            user.assignRole(role);
            jpaUserRepository.save(user);
        }
    }
}
