package ru.fedbon.service.security;

import ru.fedbon.dto.security.AuthenticationResponse;
import ru.fedbon.dto.security.SigninRequest;
import ru.fedbon.dto.security.RefreshTokenRequest;
import ru.fedbon.dto.security.SignupRequest;
import ru.fedbon.model.User;
import ru.fedbon.model.security.VerificationToken;

public interface AuthService {

    AuthenticationResponse signup(SignupRequest signupRequest);

    void verifyAccount(String token);

    void fetchUserAndEnable(VerificationToken verificationToken);

    AuthenticationResponse signin(SigninRequest signinRequest);

    User getCurrentUser();

    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
