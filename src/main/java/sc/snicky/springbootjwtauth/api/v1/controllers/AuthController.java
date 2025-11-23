package sc.snicky.springbootjwtauth.api.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sc.snicky.springbootjwtauth.api.v1.dtos.TokenPair;
import sc.snicky.springbootjwtauth.api.v1.dtos.requests.AuthRequest;
import sc.snicky.springbootjwtauth.api.v1.dtos.responses.AuthResponse;
import sc.snicky.springbootjwtauth.api.v1.services.AuthService;
import sc.snicky.springbootjwtauth.api.v1.services.SessionService;

import java.time.Instant;

/**
 * Controller for handling authentication-related operations such as registration, login, token refresh, and logout.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "authentication", description = "Endpoints for user authentication and session management")
public class AuthController {
    private final AuthService authService;
    private final SessionService sessionService;

    /**
     * Registers a new user and returns authentication tokens.
     *
     * @param response    the HTTP response
     * @param authRequest the authentication request containing user credentials
     * @return a response entity containing the authentication response
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user and returns authentication tokens.")
    public ResponseEntity<AuthResponse> register(
            HttpServletResponse response, @Valid @RequestBody AuthRequest authRequest) {

        var tokens = authService.register(authRequest.username(), authRequest.password());
        sessionService.setSessionToken(response, tokens.refreshToken());

        return buildAuthResponse(response, tokens, "User registered successfully");
    }

    /**
     * Authenticates a user and returns authentication tokens.
     *
     * @param response    the HTTP response
     * @param authRequest the authentication request containing user credentials
     * @return a response entity containing the authentication response
     */
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns authentication tokens.", responses = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials provided")})
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            HttpServletResponse response, @Valid @RequestBody AuthRequest authRequest) {

        var tokens = authService.login(authRequest.username(), authRequest.password());
        sessionService.setSessionToken(response, tokens.refreshToken());

        return buildAuthResponse(response, tokens, "User logged in successfully");
    }

    /**
     * Refreshes authentication tokens using the refresh token.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a response entity containing the authentication response
     */
    @Operation(
            summary = "Refresh authentication tokens",
            description = "Refreshes authentication tokens using the provided refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
            })
    @PostMapping("/tokens/refresh")
    public ResponseEntity<AuthResponse> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        var refreshToken = sessionService.getSessionToken(request);
        var tokens = authService.refreshTokens(refreshToken);
        sessionService.setSessionToken(response, tokens.refreshToken());

        return buildAuthResponse(response, tokens, "Tokens refreshed successfully");
    }

    /**
     * Logs out the user by invalidating the refresh token.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a response entity with no content
     */
    @Operation(summary = "Logout user", description = "Logs out the user by invalidating the refresh token.", responses = {
            @ApiResponse(responseCode = "204", description = "User logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")})
    @DeleteMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        var refreshToken = sessionService.getSessionToken(request);
        authService.logout(refreshToken);
        sessionService.clearSessionToken(response);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<AuthResponse> buildAuthResponse(
            HttpServletResponse response, TokenPair tokens, String message) {
        sessionService.setSessionToken(response, tokens.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new AuthResponse(message, tokens.accessToken(), Instant.now()));
    }
}
