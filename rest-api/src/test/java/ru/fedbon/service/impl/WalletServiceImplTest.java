package ru.fedbon.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fedbon.dto.WalletDto;
import ru.fedbon.mapper.WalletMapper;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;
import ru.fedbon.repository.WalletRepository;
import ru.fedbon.service.security.AuthServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    @DisplayName("Корректно создает новый кошелек для авторизованного пользователя")
    void testCreateNewWallet() {
        // given
        var walletDto = new WalletDto(1L, 0.0, Instant.now(), 1L);
        var user = new User(1L, "testMobile", "testPassword",
                Instant.now(), true);

        var wallet = new Wallet();
        wallet.setWalletId(1L);
        wallet.setBalance(0.0);
        wallet.setCreatedDate(Instant.now());
        wallet.setUser(user);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(walletMapper.mapDtoToWallet(walletDto, user)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        var resultWallet = walletService.createNewWallet(walletDto);

        // then
        assertNotNull(resultWallet);
        assertEquals(wallet.getWalletId(), resultWallet.getWalletId());
        assertEquals(wallet.getBalance(), resultWallet.getBalance());
        assertEquals(wallet.getCreatedDate(), resultWallet.getCreatedDate());
        assertEquals(wallet.getUser(), resultWallet.getUser());
    }

    @Test
    @DisplayName("Корректно возвращает список всех кошельков авторизованного пользователя")
    void testGetAllWalletsForUser() {
        // given
        long userId = 1L;
        var currentUser = new User();
        currentUser.setUserId(userId);

        var wallet1 = new Wallet();
        wallet1.setWalletId(1L);
        wallet1.setBalance(100.0);
        wallet1.setCreatedDate(Instant.now());
        wallet1.setUser(currentUser);

        var wallet2 = new Wallet();
        wallet2.setWalletId(2L);
        wallet2.setBalance(200.0);
        wallet2.setCreatedDate(Instant.now());
        wallet2.setUser(currentUser);

        var wallets = List.of(wallet1, wallet2);

        var walletDto1 = new WalletDto();
        walletDto1.setWalletId(wallet1.getWalletId());
        walletDto1.setBalance(wallet1.getBalance());
        walletDto1.setCreatedDate(wallet1.getCreatedDate());
        walletDto1.setUserId(currentUser.getUserId());

        var walletDto2 = new WalletDto();
        walletDto2.setWalletId(wallet2.getWalletId());
        walletDto2.setBalance(wallet2.getBalance());
        walletDto2.setCreatedDate(wallet2.getCreatedDate());
        walletDto2.setUserId(currentUser.getUserId());

        // when
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(walletRepository.findAllByUser(currentUser)).thenReturn(wallets);
        when(walletMapper.mapWalletToDto(wallet1)).thenReturn(walletDto1);
        when(walletMapper.mapWalletToDto(wallet2)).thenReturn(walletDto2);

        var walletDtoList = walletService.getAllWalletsForUser();
        var resultWalletDto1 = walletDtoList.get(0);
        var resultWalletDto2 = walletDtoList.get(1);

        // then
        assertNotNull(walletDtoList);
        assertEquals(wallets.size(), walletDtoList.size());

        assertEquals(wallet1.getWalletId(), resultWalletDto1.getWalletId());
        assertEquals(wallet1.getBalance(), resultWalletDto1.getBalance());
        assertEquals(wallet1.getCreatedDate(), resultWalletDto1.getCreatedDate());
        assertEquals(wallet1.getUser().getUserId(), resultWalletDto1.getUserId());

        assertEquals(wallet2.getWalletId(), resultWalletDto2.getWalletId());
        assertEquals(wallet2.getBalance(), resultWalletDto2.getBalance());
        assertEquals(wallet2.getCreatedDate(), resultWalletDto2.getCreatedDate());
        assertEquals(wallet2.getUser().getUserId(), resultWalletDto2.getUserId());

        verify(authService, times(1)).getCurrentUser();
        verify(walletRepository, times(1)).findAllByUser(currentUser);
        verify(walletMapper, times(1)).mapWalletToDto(wallet1);
        verify(walletMapper, times(1)).mapWalletToDto(wallet2);
    }

    @Test
    @DisplayName("Корректно возвращает значение баланса кошелька авторизованного пользователя")
    void testGetBalanceForWallet() {
        // given
        long walletId = 1L;
        double balance = 500.0;

        var wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(balance);
        wallet.setCreatedDate(Instant.now());

        // when
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        double resultBalance = walletService.getBalanceForWallet(walletId);

        // then
        assertEquals(balance, resultBalance);
        verify(walletRepository, times(1)).findById(walletId);
    }
}