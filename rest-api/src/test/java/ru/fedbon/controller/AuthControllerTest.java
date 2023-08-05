package ru.fedbon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.fedbon.dto.security.AuthenticationResponse;
import ru.fedbon.dto.security.RefreshTokenRequest;
import ru.fedbon.dto.security.SigninRequest;
import ru.fedbon.dto.security.SignupRequest;
import ru.fedbon.service.security.AuthServiceImpl;
import ru.fedbon.service.security.RefreshTokenServiceImpl;
import ru.fedbon.utils.JsonStringWrapper;

import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private RefreshTokenServiceImpl refreshTokenService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private JsonStringWrapper jsonStringWrapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        jsonStringWrapper = new JsonStringWrapper();
    }

    @Test
    @DisplayName("POST /api/auth/signup возвращает корректное тело ответа о создании нового аккаунта и HTTP-статус ОК")
    void testHandleSignup() throws Exception {
        // given
        var signupRequest = new SignupRequest("testUserMobileNumber", "testPassword");
        var authenticationResponse = new AuthenticationResponse("testToken",
                "testRefreshToken", Instant.now(), "testUserMobileNumber");

        // when
        when(authService.signup(any(SignupRequest.class))).thenReturn(authenticationResponse);

        // then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User signed up successfully")))
                .andExpect(content().string(containsString(authenticationResponse.toString())));

        verify(authService, times(1)).signup(any(SignupRequest.class));
    }

    @Test
    @DisplayName("GET /api/auth/account_verification/ возвращает корректное тело ответа о верификации " +
            "пользователя и HTTP-статус ОК")
    void testHandleVerifyAccount() throws Exception {
        // given
        String token = "testVerificationToken";

        // then
        mockMvc.perform(get("/api/auth/account_verification/{token}", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account activated successfully")));

        verify(authService, times(1)).verifyAccount(token);
    }

    @Test
    @DisplayName("POST /api/auth/signin возвращает корректное тело ответа с данными " +
            "о входе пользователя в аккаунт и HTTP-статус ОК")
    void testHandleSignin() throws Exception {
        // given
        var signinRequest = new SigninRequest("testUserMobileNumber", "testPassword");
        var authenticationResponse = new AuthenticationResponse("testToken",
                "testRefreshToken", Instant.now(), "testUserMobileNumber");

        // when
        when(authService.signin(any(SigninRequest.class))).thenReturn(authenticationResponse);

        // then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(signinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticationToken").value("testToken"))
                .andExpect(jsonPath("$.refreshToken").value("testRefreshToken"))
                .andExpect(jsonPath("$.expiresAt").exists());

        verify(authService, times(1)).signin(any(SigninRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/refresh/token возвращает корректное тело ответа с обновленным токеном " +
            "и HTTP-статус ОК")
    void testHandleRefreshToken() throws Exception {
        // given
        var refreshTokenRequest = new RefreshTokenRequest("testRefreshToken",
                "testUserMobileNumber");

        var authenticationResponse = new AuthenticationResponse("testToken",
                "testRefreshToken", Instant.now(), "testUserMobileNumber");

        // when
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authenticationResponse);

        // then
        mockMvc.perform(post("/api/auth/refresh/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticationToken").value("testToken"))
                .andExpect(jsonPath("$.refreshToken").value("testRefreshToken"))
                .andExpect(jsonPath("$.expiresAt").exists());

        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/signout возвращает корректное тело ответа об удалении токена и HTTP-статус ОК")
    void testHandleSignout() throws Exception {
        // given
        var refreshTokenRequest = new RefreshTokenRequest("testRefreshToken",
                "testUserMobileNumber");

        // then
        mockMvc.perform(post("/api/auth/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Refresh token deleted successfully")));

        verify(refreshTokenService, times(1))
                .deleteRefreshToken(refreshTokenRequest.getRefreshToken());
    }
}
