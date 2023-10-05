package ru.fedbon.service.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedbon.utils.ErrorMessage;
import ru.fedbon.exception.InvalidTokenException;
import ru.fedbon.model.security.RefreshToken;
import ru.fedbon.repository.security.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateRefreshToken() {
        var refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresentOrElse(
                refreshToken -> { }, () -> {
                    throw new InvalidTokenException(ErrorMessage.INVALID_TOKEN);
                }
        );
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
