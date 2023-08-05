package ru.fedbon.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fedbon.dto.security.AuthenticationResponse;
import ru.fedbon.dto.security.SigninRequest;
import ru.fedbon.dto.security.RefreshTokenRequest;
import ru.fedbon.dto.security.SignupRequest;
import ru.fedbon.service.security.AuthServiceImpl;
import ru.fedbon.service.security.RefreshTokenServiceImpl;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final RefreshTokenServiceImpl refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> handleSignup(@RequestBody SignupRequest signupRequest) {
        AuthenticationResponse authenticationResponse = authService.signup(signupRequest);
        return new ResponseEntity<>("User signed up successfully %n Response: " +
                authenticationResponse, HttpStatus.OK);
    }

    @GetMapping("/account_verification/{token}")
    public ResponseEntity<String> handleVerifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    @PostMapping("/signin")
    public AuthenticationResponse handleSignin(@RequestBody SigninRequest signinRequest) {
        return authService.signin(signinRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse handleRefreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/signout")
    public ResponseEntity<String> handleSignout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity
                .status(OK)
                .body("Refresh token deleted successfully");
    }
}
