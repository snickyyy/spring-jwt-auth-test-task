package unit.sc.snicky.springbootjwtauth.api.v1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.BasicRefreshToken;
import sc.snicky.springbootjwtauth.api.v1.domain.models.RefreshTokenDetails;
import sc.snicky.springbootjwtauth.api.v1.domain.models.RefreshTokenDetailsAdaptor;
import sc.snicky.springbootjwtauth.api.v1.domain.models.Role;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;
import sc.snicky.springbootjwtauth.api.v1.domain.types.NonProtectedToken;
import sc.snicky.springbootjwtauth.api.v1.domain.types.ProtectedToken;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.security.PasswordOrUsernameIsInvalidException;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.users.UserAlreadyExistException;
import sc.snicky.springbootjwtauth.api.v1.exceptions.business.users.UserNotFoundException;
import sc.snicky.springbootjwtauth.api.v1.services.AccessTokenServiceImpl;
import sc.snicky.springbootjwtauth.api.v1.services.AuthServiceImpl;
import sc.snicky.springbootjwtauth.api.v1.services.RefreshTokenService;
import sc.snicky.springbootjwtauth.api.v1.services.TokensManagerImpl;
import sc.snicky.springbootjwtauth.api.v1.services.UserService;
import sc.snicky.springbootjwtauth.api.v1.services.utils.TokenUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    private static final String TEST_EMAIL = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final Long TEST_ACCESS_TOKEN_DURATION = 3600000L;
    private final Long TEST_REFRESH_TOKEN_DURATION = 9000000L;
    private final String TEST_NON_PROTECTED_TOKEN = TokenUtils.generateToken();
    private final ProtectedToken TEST_PROTECTED_TOKEN = new ProtectedToken(TokenUtils.hashToken(TEST_NON_PROTECTED_TOKEN));


    @Spy
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserService userService;
    @Mock
    private TokensManagerImpl tokensManager;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Spy
    private AccessTokenServiceImpl accessTokenService = new AccessTokenServiceImpl();

    @InjectMocks
    private AuthServiceImpl authService;

    /**
     * Sets up the test environment before each test.
     * Sets the JWT signing key and token duration.
     */
    @BeforeEach
    void setUp() {
        accessTokenService.setJwtSigningKey("test_jwt_signing_key_which_should_be_replaced");
        accessTokenService.setAccessTokenDurationMs(TEST_ACCESS_TOKEN_DURATION); // 1 hour
    }

    @Test
    void testRegisterWithSuccess() {
        doNothing().when(userService).saveUser(any(), any(ERole.class));
        var token = buildToken(buildUser());
        when(refreshTokenService.generate(isNull(Integer.class))).thenReturn(token);

        var tokenPair = authService.register(TEST_EMAIL, TEST_PASSWORD);

        assertNotNull(tokenPair);
        assertNotNull(tokenPair.accessToken());
        assertNotNull(tokenPair.refreshToken());
        assertDoesNotThrow(() -> accessTokenService.extractUserDetails(tokenPair.accessToken()));
        assertEquals(buildUser().getUsername(), accessTokenService.extractUserDetails(tokenPair.accessToken()).getUsername());

        verify(userService).saveUser(any(), any(ERole.class));
        verify(refreshTokenService).generate(isNull(Integer.class));
    }

    @Test
    void testRegisterUserWithAlreadyExistingEmail() {
        doThrow(UserAlreadyExistException.class)
                .when(userService).saveUser(any(), any(ERole.class));

        assertThrows(UserAlreadyExistException.class,
                () -> authService.register(TEST_EMAIL, TEST_PASSWORD));
        verify(userService).saveUser(any(), any(ERole.class));
    }

    @Test
    void testLoginWithSuccess() {
        var user = buildUser();
        when(userService.getUserByUsername(TEST_EMAIL)).thenReturn(user);
        var token = buildToken(user);
        when(refreshTokenService.generate(user.getId())).thenReturn(token);

        var tokenPair = authService.login(TEST_EMAIL, TEST_PASSWORD);

        assertNotNull(tokenPair);
        assertNotNull(tokenPair.accessToken());
        assertNotNull(tokenPair.refreshToken());
        assertDoesNotThrow(() -> accessTokenService.extractUserDetails(tokenPair.accessToken()));
        assertEquals(user.getUsername(), accessTokenService.extractUserDetails(tokenPair.accessToken()).getUsername());

        verify(userService).getUserByUsername(TEST_EMAIL);
        verify(refreshTokenService).generate(user.getId());
    }

    @Test
    void testLoginWithInvalidPassword() {
        var user = buildUser();
        when(userService.getUserByUsername(TEST_EMAIL)).thenReturn(user);

        assertThrows(PasswordOrUsernameIsInvalidException.class,
                () -> authService.login(TEST_EMAIL, "wrongpassword"));

        verify(userService).getUserByUsername(TEST_EMAIL);
    }

    @Test
    void testLoginWithInvalidEmail() {
        when(userService.getUserByUsername(TEST_EMAIL))
                .thenThrow(UserNotFoundException.class);

        assertThrows(PasswordOrUsernameIsInvalidException.class,
                () -> authService.login(TEST_EMAIL, TEST_PASSWORD));

        verify(userService).getUserByUsername(TEST_EMAIL);
    }

    private RefreshTokenDetails buildToken(User user) {
        return RefreshTokenDetailsAdaptor.ofToken(
                new NonProtectedToken(TEST_NON_PROTECTED_TOKEN),
                BasicRefreshToken.builder()
                        .token(TEST_PROTECTED_TOKEN)
                        .user(user)
                        .isActive(true)
                        .expiresAt(Instant.now().plusMillis(TEST_REFRESH_TOKEN_DURATION))
                        .build());
    }

    private User buildUser() {
        var user = User.builder()
                .username(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build();
        user.assignRole(Role.builder().name(ERole.USER).build());
        user.setId(1);
        return user;
    }
}
