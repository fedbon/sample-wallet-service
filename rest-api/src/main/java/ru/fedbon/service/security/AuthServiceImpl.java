package ru.fedbon.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedbon.dto.security.AuthenticationResponse;
import ru.fedbon.dto.security.SigninRequest;
import ru.fedbon.dto.security.RefreshTokenRequest;
import ru.fedbon.dto.security.SignupRequest;
import ru.fedbon.utils.ErrorMessage;
import ru.fedbon.exception.InvalidTokenException;
import ru.fedbon.exception.UserNotFoundException;
import ru.fedbon.model.User;
import ru.fedbon.model.security.VerificationToken;
import ru.fedbon.repository.UserRepository;
import ru.fedbon.repository.security.VerificationTokenRepository;
import ru.fedbon.utils.JwtProvider;
import ru.fedbon.utils.Message;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenServiceImpl refreshTokenService;

    @Override
    public AuthenticationResponse signup(SignupRequest signupRequest) {
        var user = new User();
        user.setPassword(encodePassword(signupRequest.getPassword()));
        user.setUserMobileNumber(signupRequest.getUserMobileNumber());
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        log.info(Message.CREATED, user);

        var token = generateVerificationToken(user);

        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken("")
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .userMobileNumber(signupRequest.getUserMobileNumber())
                .build();
    }
    @Override
    public void verifyAccount(String token) {
        var verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken.isPresent()) {
            fetchUserAndEnable(verificationToken.get());
        } else {
            throw new InvalidTokenException(ErrorMessage.INVALID_TOKEN);
        }
    }
    @Override
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        var userMobileNumber = verificationToken.getUser().getUserMobileNumber();
        var user = userRepository.findByUserMobileNumber(userMobileNumber)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND + userMobileNumber));
        user.setEnabled(true);
        userRepository.save(user);
    }
    @Override
    public AuthenticationResponse signin(SigninRequest signinRequest) {
        var authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signinRequest.getUserMobileNumber(),
                signinRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        var token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .userMobileNumber(signinRequest.getUserMobileNumber())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        var principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userMobileNumber = principal.getSubject();
        return userRepository.findByUserMobileNumber(userMobileNumber)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessage.USER_NOT_FOUND + userMobileNumber));
    }
    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        var token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUserMobileNumber());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .userMobileNumber(refreshTokenRequest.getUserMobileNumber())
                .build();
    }
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    String generateVerificationToken(User user) {
        var token = UUID.randomUUID().toString();
        var verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}
