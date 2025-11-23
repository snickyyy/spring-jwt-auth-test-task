package unit.sc.snicky.springbootjwtauth.api.v1.services;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sc.snicky.springbootjwtauth.api.v1.domain.enums.ERole;
import sc.snicky.springbootjwtauth.api.v1.domain.models.BasicRefreshToken;
import sc.snicky.springbootjwtauth.api.v1.domain.models.RefreshTokenDetailsAdaptor;
import sc.snicky.springbootjwtauth.api.v1.domain.models.Role;
import sc.snicky.springbootjwtauth.api.v1.domain.models.User;
import sc.snicky.springbootjwtauth.api.v1.domain.types.NonProtectedToken;
import sc.snicky.springbootjwtauth.api.v1.domain.types.ProtectedToken;
import sc.snicky.springbootjwtauth.api.v1.services.AccessTokenServiceImpl;
import sc.snicky.springbootjwtauth.api.v1.services.RefreshTokenServiceImpl;
import sc.snicky.springbootjwtauth.api.v1.services.TokensManagerImpl;
import sc.snicky.springbootjwtauth.api.v1.services.utils.TokenUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class TokensManagerTest {
    private static final String TEST_EMAIL = "testuser";
    private static final String TEST_PASSWORD = "testpassword";
    private static final Long TEST_REFRESH_TOKEN_DURATION = 9000000L;

    private final String TEST_NON_PROTECTED_TOKEN = TokenUtils.generateToken();
    private final ProtectedToken TEST_PROTECTED_TOKEN = new ProtectedToken(TokenUtils.hashToken(TEST_NON_PROTECTED_TOKEN));


    @Mock
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private AccessTokenServiceImpl accessTokenService;

    @InjectMocks
    private TokensManagerImpl tokensManager;


    @Test
    void generateTokensWithSuccess() {
        var user = buildUser();
        user.setId(1);
        var token = buildToken(user);
        when(refreshTokenService.generate(1))
                .thenReturn(RefreshTokenDetailsAdaptor.ofToken(new NonProtectedToken(TEST_NON_PROTECTED_TOKEN), token));

        var result = tokensManager.generateTokens(1);

        assertNotNull(result);
        assertEquals(TEST_NON_PROTECTED_TOKEN, result.refreshToken());

        verify(refreshTokenService).generate(1);
    }

    @Test
    void refreshTokensWithSuccess() {
        var user = buildUser();
        user.setId(1);
        var newToken = buildToken(user);
        when(refreshTokenService.rotate(TEST_NON_PROTECTED_TOKEN))
                .thenReturn(RefreshTokenDetailsAdaptor.ofToken(new NonProtectedToken(TEST_NON_PROTECTED_TOKEN), newToken));

        var result = tokensManager.refreshTokens(TEST_NON_PROTECTED_TOKEN);

        assertNotNull(result);
        assertEquals(TEST_NON_PROTECTED_TOKEN, result.refreshToken());

        verify(refreshTokenService).rotate(TEST_NON_PROTECTED_TOKEN);
    }

    private User buildUser() {
        var user = User.builder()
                .username(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();
        user.assignRole(Role.builder().name(ERole.USER).build());
        return user;
    }

    private BasicRefreshToken buildToken(User user) {
        return BasicRefreshToken.builder()
                .token(TEST_PROTECTED_TOKEN)
                .user(user)
                .expiresAt(Instant.now().plusMillis(TEST_REFRESH_TOKEN_DURATION))
                .build();
    }
}
