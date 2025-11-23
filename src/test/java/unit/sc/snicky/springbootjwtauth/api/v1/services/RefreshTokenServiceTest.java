package unit.sc.snicky.springbootjwtauth.api.v1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.BasicRefreshToken;
import sc.snicky.springbootjwtauth.api.v1.domain.models.RefreshTokenDetails;
import sc.snicky.springbootjwtauth.api.v1.domain.models.Role;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;
import sc.snicky.springbootjwtauth.api.v1.domain.types.ProtectedToken;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.security.InvalidRefreshTokenException;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.users.UserNotFoundException;
import sc.snicky.springbootjwtauth.api.v1.repositories.BasicRefreshTokenRepository;
import sc.snicky.springbootjwtauth.api.v1.repositories.JpaUserRepository;
import sc.snicky.springbootjwtauth.api.v1.services.RefreshTokenServiceImpl;
import sc.snicky.springbootjwtauth.api.v1.services.utils.TokenUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    private static final String TEST_EMAIL = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private final Long TEST_REFRESH_TOKEN_DURATION = 9000000L;
    private final String TEST_NON_PROTECTED_TOKEN = TokenUtils.generateToken();
    private final ProtectedToken TEST_PROTECTED_TOKEN = new ProtectedToken(TokenUtils.hashToken(TEST_NON_PROTECTED_TOKEN));

    @Mock
    private BasicRefreshTokenRepository basicRefreshTokenRepository;

    @Mock
    private JpaUserRepository jpaUserRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenServiceTest;

    /**
     * Executed before each test.
     * <p>
     * Sets the refresh token duration in milliseconds
     * for the tested service.
     */
    @BeforeEach
    void setup() {
        refreshTokenServiceTest.setRefreshTokenDurationMs(TEST_REFRESH_TOKEN_DURATION);
    }

    @Test
    void testGenerateRefreshTokenWithSuccess() {
        var userId = 1;
        var testUser = buildUser();
        testUser.setId(userId);
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(basicRefreshTokenRepository).save(any());

        RefreshTokenDetails result = refreshTokenServiceTest.generate(userId);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getUser().getId());

        verify(basicRefreshTokenRepository).save(any());
    }

    @Test
    void testGenerateRefreshTokenWithUserNotFoundException() {
        var userId = 1;
        var testUser = buildUser();
        testUser.setId(userId);
        when(jpaUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> refreshTokenServiceTest.generate(userId));
    }

    @Test
    void testIsValidWithSuccess() {
        var token = buildToken(buildUser());
        when(basicRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

        var result = refreshTokenServiceTest.isValid(TEST_NON_PROTECTED_TOKEN);

        assertTrue(result);
    }

    @Test
    void testIsValidWithTokenNotFound() {
        when(basicRefreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        var result = refreshTokenServiceTest.isValid(TEST_NON_PROTECTED_TOKEN);

        assertFalse(result);
    }

    @Test
    void testIsValidWithTokenIsExpired() {
        var token = buildToken(buildUser());
        token.setExpiresAt(Instant.now().minusSeconds(1));
        when(basicRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

        var result = refreshTokenServiceTest.isValid(TEST_NON_PROTECTED_TOKEN);

        assertFalse(result);
    }

    @Test
    void testIsValidWithTokenIsNotActive() {
        var token = buildToken(buildUser());
        token.setIsActive(false);
        when(basicRefreshTokenRepository.findByToken(TEST_PROTECTED_TOKEN)).thenReturn(Optional.of(token));

        var result = refreshTokenServiceTest.isValid(TEST_NON_PROTECTED_TOKEN);

        assertFalse(result);

        verify(basicRefreshTokenRepository).findByToken(TEST_PROTECTED_TOKEN);
    }

    @Test
    void testRotateWithSuccess() {
        var testUser = buildUser();
        var oldToken = buildToken(testUser);
        when(basicRefreshTokenRepository.findByToken(TEST_PROTECTED_TOKEN)).thenReturn(Optional.of(oldToken));
        doNothing().when(basicRefreshTokenRepository).delete(TEST_PROTECTED_TOKEN);
        doNothing().when(basicRefreshTokenRepository).save(any());

        RefreshTokenDetails result = refreshTokenServiceTest.rotate(TEST_NON_PROTECTED_TOKEN);

        assertNotNull(result);
        assertNotEquals(TEST_PROTECTED_TOKEN.getToken(), result.getToken().getToken());
        assertEquals(testUser.getUsername(), result.getUser().getUsername());
        assertEquals(oldToken.getExpiresAt(), result.getExpiry());

        verify(basicRefreshTokenRepository).delete(TEST_PROTECTED_TOKEN);
    }

    @Test
    void testRotateWithRefreshTokenNotValidException() {
        when(basicRefreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenServiceTest.rotate(TEST_NON_PROTECTED_TOKEN)
        );
    }

    @Test
    void testRotateWithRefreshTokenIsExpired() {
        var token = buildToken(buildUser());
        token.setExpiresAt(Instant.now().minusSeconds(1));
        when(basicRefreshTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenServiceTest.rotate(TEST_NON_PROTECTED_TOKEN)
        );
    }

    @Test
    void testRevokeTokenWithSuccess() {
        doNothing().when(basicRefreshTokenRepository).delete(any());

        refreshTokenServiceTest.revoke(TEST_NON_PROTECTED_TOKEN);

        verify(basicRefreshTokenRepository).delete(any());
    }

    @Test
    void testFindByTokenWithSuccess() {

        var testToken = buildToken(buildUser());
        when(basicRefreshTokenRepository.findByToken(TEST_PROTECTED_TOKEN))
                .thenReturn(Optional.of(testToken));

        Optional<RefreshTokenDetails> result = refreshTokenServiceTest.findByToken(TEST_NON_PROTECTED_TOKEN);

        assertTrue(result.isPresent());
        assertEquals(testToken.getUser().getUsername(), result.get().getUser().getUsername());

        verify(basicRefreshTokenRepository).findByToken(TEST_PROTECTED_TOKEN);
    }

    private BasicRefreshToken buildToken(User user) {
        return BasicRefreshToken.builder()
                .token(TEST_PROTECTED_TOKEN)
                .user(user)
                .isActive(true)
                .expiresAt(Instant.now().plusMillis(TEST_REFRESH_TOKEN_DURATION))
                .build();
    }

    private User buildUser() {
        var user = User.builder()
                .username(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        user.assignRole(Role.builder().name(ERole.USER).build());
        return user;
    }
}
