package ru.fedbon.service.security;

import ru.fedbon.model.security.RefreshToken;


public interface RefreshTokenService {
    RefreshToken generateRefreshToken();

    void validateRefreshToken(String token);

    void deleteRefreshToken(String token);
}
