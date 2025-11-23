package sc.snicky.springbootjwtauth.api.v1.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDetailsAdaptor implements UserDetails {
    private Collection<? extends GrantedAuthority> grantedAuthorities;
    private String username;
    private String password;

    /**
     * Creates a UserDetailsAdaptor from a given User entity.
     * Maps user roles to GrantedAuthority and sets username and password.
     *
     * @param user the User entity to adapt
     * @return a UserDetailsAdaptor instance
     */
    public static UserDetailsAdaptor ofUser(User user) {
        return UserDetailsAdaptor.builder()
                .grantedAuthorities(user.getRoles().stream()
                        .map(Role::getName)
                        .map(erole -> (GrantedAuthority) erole::name)
                        .toList())
                .password(user.getPassword())
                .username(user.getUsername())
                .build();
    }

    /**
     * Returns the authorities granted to the user.
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.copyOf(grantedAuthorities);
    }

    /**
     * Returns the password used to authenticate the user.
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     * @return the user's username
     */
    @Override
    public String getUsername() {
        return username;
    }
}
