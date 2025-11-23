package sc.snicky.springbootjwtauth.api.v1.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
public class User extends BaseEntity<Integer> implements Serializable {
    /**
     * Max length for the username field.
     */
    private static final int USERNAME_MAX_LENGTH = 40;

    /**
     * User username address.
     */
    @Column(name = "username", nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;

    /**
     * User password hash.
     */
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    /**
     * Indicates whether the user is active.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * User roles.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Adds a role to the user's set of roles.
     *
     * @param role the role to add
     */
    public void assignRole(Role role) {
        this.roles.add(role);
    }
}
