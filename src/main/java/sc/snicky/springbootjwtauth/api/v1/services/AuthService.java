package sc.snicky.springbootjwtauth.api.v1.services;

import sc.snicky.springbootjwtauth.api.v1.dtos.TokenPair;

public interface AuthService { // todo add method assign role
    /**
     * Registers a new user with the provided username and password.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @return a TokenPair containing the access and refresh tokens
     */
    TokenPair register(String username, String password);

    /**
     * Authenticates a user with the provided username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return a TokenPair containing the access and refresh tokens
     */
    TokenPair login(String username, String password);

    /**
     * Refreshes the access and refresh tokens using the provided refresh token.
     *
     * @param refreshToken the refresh token to generate new tokens
     * @return a TokenPair containing the new access and refresh tokens
     */
    TokenPair refreshTokens(String refreshToken);

    /**
     * Logs out the user by invalidating the provided refresh token.
     *
     * @param refreshToken the refresh token to be invalidated
     */
    void logout(String refreshToken);
}
