package ru.fedbon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.fedbon.dto.WalletDto;
import ru.fedbon.model.Wallet;
import ru.fedbon.service.impl.WalletServiceImpl;
import ru.fedbon.utils.JsonStringWrapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @InjectMocks
    private WalletController walletController;

    @Mock
    private WalletServiceImpl walletService;

    private MockMvc mockMvc;

    private JsonStringWrapper jsonStringWrapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
        jsonStringWrapper = new JsonStringWrapper();
    }

    @Test
    @DisplayName("POST /api/wallets возвращает корректное тело ответа с информацией " +
            "о созданном кошельке и HTTP-статус CREATED")
    void testHandleCreateNewWallet() throws Exception {
        // given
        var walletDto = new WalletDto();
        walletDto.setBalance(0.0);
        walletDto.setCreatedDate(Instant.now());
        walletDto.setUserId(1L);

        var savedWallet = new Wallet();
        savedWallet.setWalletId(1L);
        savedWallet.setBalance(walletDto.getBalance());
        savedWallet.setCreatedDate(walletDto.getCreatedDate());

        // when
        when(walletService.createNewWallet(any(WalletDto.class))).thenReturn(savedWallet);

        // then
        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(walletDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.walletId").value(savedWallet.getWalletId()))
                .andExpect(jsonPath("$.balance").value(savedWallet.getBalance()))
                .andExpect(jsonPath("$.createdDate").exists());

        verify(walletService, times(1)).createNewWallet(any(WalletDto.class));
    }

    @Test
    @DisplayName("GET /api/wallets возвращает корректное тело ответа " +
            "со списком всех кошельков пользователя и HTTP-статус ОК")
    void testHandleGetAllWalletsForUser() throws Exception {
        // given
        var walletDtoList = List.of(
                new WalletDto(1L, 0.0, Instant.now().plusSeconds(3600), 1L),
                new WalletDto(2L, 0.0, Instant.now().plusSeconds(3600), 1L)
        );

        // when
        when(walletService.getAllWalletsForUser()).thenReturn(walletDtoList);

        // then
        mockMvc.perform(get("/api/wallets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(walletDtoList.size()))
                .andExpect(jsonPath("$[0].walletId").value(walletDtoList.get(0).getWalletId()))
                .andExpect(jsonPath("$[0].balance").value(walletDtoList.get(0).getBalance()))
                .andExpect(jsonPath("$[0].createdDate").exists())
                .andExpect(jsonPath("$[1].walletId").value(walletDtoList.get(1).getWalletId()))
                .andExpect(jsonPath("$[1].balance").value(walletDtoList.get(1).getBalance()))
                .andExpect(jsonPath("$[1].createdDate").exists());

        verify(walletService, times(1)).getAllWalletsForUser();
    }

    @Test
    @DisplayName("GET /by-wallet/{walletId} возвращает корректное тело ответа с балансом кошелька и HTTP-статус ОК")
    void testHandleGetBalanceForWallet() throws Exception {
        // given
        long walletId = 1L;

        double balance = 0.0;

        // when
        when(walletService.getBalanceForWallet(walletId)).thenReturn(balance);

        // then
        mockMvc.perform(get("/api/wallets/by-wallet/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Balance: " + balance));

        verify(walletService, times(1)).getBalanceForWallet(walletId);
    }

}