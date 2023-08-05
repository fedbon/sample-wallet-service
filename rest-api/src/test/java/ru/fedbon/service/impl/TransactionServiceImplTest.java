package ru.fedbon.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fedbon.dto.transaction.TransactionRequest;
import ru.fedbon.dto.transaction.TransactionResponse;
import ru.fedbon.mapper.TransactionMapper;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;
import ru.fedbon.model.transaction.Transaction;
import ru.fedbon.model.transaction.TransactionType;
import ru.fedbon.repository.TransactionRepository;
import ru.fedbon.repository.WalletRepository;
import ru.fedbon.service.security.AuthServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AuthServiceImpl authService;

    @InjectMocks
    private TransactionServiceImpl transactionService;


    @Test
    @DisplayName("Корректно обрабатывает транзакцию пополнения баланса кошелька " +
            "авторизованного пользователя с его карты")
    void testDepositFromCardToWallet() {
        // given
        var user = new User();
        user.setUserId(1L);
        user.setUserMobileNumber("testUserMobileNumber");
        user.setCreated(Instant.now());
        user.setEnabled(true);

        var wallet = new Wallet();
        wallet.setWalletId(1L);
        wallet.setBalance(2000.0);
        wallet.setCreatedDate(Instant.now());
        wallet.setUser(user);

        var transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionType(TransactionType.DEPOSIT_CARD_TO_WALLET);
        transactionRequest.setWalletId(wallet.getWalletId());
        transactionRequest.setUserId(user.getUserId());
        transactionRequest.setAmount(1000.0);
        transactionRequest.setRecipientMobileNumber("testRecipientMobileNumber");

        var transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setTransactionType(TransactionType.DEPOSIT_CARD_TO_WALLET);
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setAmount(1000.0);
        transaction.setRecipientMobileNumber("testRecipientMobileNumber");

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(walletRepository.findById(transactionRequest.getWalletId())).thenReturn(Optional.of(wallet));
        when(transactionMapper.mapDtoToTransaction(eq(transactionRequest), any(Wallet.class), eq(user)))
                .thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        var resultTransaction = transactionService.process(transactionRequest);

        // then
        assertNotNull(transaction);
        assertEquals(transaction.getTransactionId(), resultTransaction.getTransactionId());
        assertEquals(transaction.getTransactionType(), resultTransaction.getTransactionType());
        assertEquals(transaction.getAmount(), resultTransaction.getAmount());
        assertEquals(transaction.getRecipientMobileNumber(), resultTransaction.getRecipientMobileNumber());
        assertEquals(transaction.getWallet().getWalletId(), resultTransaction.getWallet().getWalletId());
        assertEquals(transaction.getUser().getUserId(), resultTransaction.getUser().getUserId());
    }

    @Test
    @DisplayName("Корректно обрабатывает транзакцию перевода средств авторизованного пользователя" +
            " на кошелек другого пользователя")
    void testTransferFromWalletToWallet() {
        // given
        var user = new User();
        user.setUserId(1L);
        user.setUserMobileNumber("testUserMobileNumber");
        user.setCreated(Instant.now());
        user.setEnabled(true);

        var wallet = new Wallet();
        wallet.setWalletId(1L);
        wallet.setBalance(2000.0);
        wallet.setCreatedDate(Instant.now());
        wallet.setUser(user);

        var transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionType(TransactionType.TRANSFER_WALLET_TO_WALLET);
        transactionRequest.setWalletId(wallet.getWalletId());
        transactionRequest.setUserId(user.getUserId());
        transactionRequest.setAmount(1000.0);
        transactionRequest.setRecipientMobileNumber("testRecipientMobileNumber");

        var transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setTransactionType(TransactionType.TRANSFER_WALLET_TO_WALLET);
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setAmount(1000.0);
        transaction.setRecipientMobileNumber("testRecipientMobileNumber");

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(walletRepository.findById(transactionRequest.getWalletId())).thenReturn(Optional.of(wallet));
        when(transactionMapper.mapDtoToTransaction(eq(transactionRequest), any(Wallet.class), eq(user)))
                .thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        var resultTransaction = transactionService.process(transactionRequest);

        // then
        assertNotNull(transaction);
        assertEquals(transaction.getTransactionId(), resultTransaction.getTransactionId());
        assertEquals(transaction.getTransactionType(), resultTransaction.getTransactionType());
        assertEquals(transaction.getAmount(), resultTransaction.getAmount());
        assertEquals(transaction.getRecipientMobileNumber(), resultTransaction.getRecipientMobileNumber());
        assertEquals(transaction.getWallet().getWalletId(), resultTransaction.getWallet().getWalletId());
        assertEquals(transaction.getUser().getUserId(), resultTransaction.getUser().getUserId());
    }

    @Test
    @DisplayName("Корректно возвращает список транзакций для кошелька авторизованного пользователя")
    void testGetAllTransactionsForWallet() {
        // given
        var walletId = 1L;
        var user = new User();

        user.setUserId(1L);
        user.setUserMobileNumber("testUserMobileNumber");
        user.setCreated(Instant.now());
        user.setEnabled(true);

        var wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(2000.0);
        wallet.setCreatedDate(Instant.now());
        wallet.setUser(user);

        var transaction1 = new Transaction();
        transaction1.setTransactionId(1L);
        transaction1.setTransactionType(TransactionType.DEPOSIT_CARD_TO_WALLET);
        transaction1.setWallet(wallet);
        transaction1.setUser(user);
        transaction1.setAmount(1000.0);
        transaction1.setRecipientMobileNumber("testRecipientMobileNumber");

        var transaction2 = new Transaction();
        transaction2.setTransactionId(2L);
        transaction2.setTransactionType(TransactionType.TRANSFER_WALLET_TO_WALLET);
        transaction2.setWallet(wallet);
        transaction2.setUser(user);
        transaction2.setAmount(500.0);
        transaction2.setRecipientMobileNumber("testRecipientMobileNumber");

        var transactions = List.of(transaction1, transaction2);

        var transactionResponse1 = new TransactionResponse();
        transactionResponse1.setTransactionId(transaction1.getTransactionId());
        transactionResponse1.setTransactionType(transaction1.getTransactionType());
        transactionResponse1.setWalletId(wallet.getWalletId());
        transactionResponse1.setUserId(user.getUserId());
        transactionResponse1.setAmount(transaction1.getAmount());
        transactionResponse1.setRecipientMobileNumber(transaction1.getRecipientMobileNumber());
        transactionResponse1.setDateTimeProcessed(Instant.now());
        transactionResponse1.setIsCompleted(true);

        var transactionResponse2 = new TransactionResponse();
        transactionResponse2.setTransactionId(transaction2.getTransactionId());
        transactionResponse2.setTransactionType(transaction2.getTransactionType());
        transactionResponse2.setWalletId(wallet.getWalletId());
        transactionResponse2.setUserId(user.getUserId());
        transactionResponse2.setAmount(transaction2.getAmount());
        transactionResponse2.setRecipientMobileNumber(transaction1.getRecipientMobileNumber());
        transactionResponse2.setDateTimeProcessed(Instant.now());
        transactionResponse2.setIsCompleted(true);

        // when
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWallet(wallet)).thenReturn(transactions);
        when(transactionMapper.mapTransactionToDto(transaction1)).thenReturn(transactionResponse1);
        when(transactionMapper.mapTransactionToDto(transaction2)).thenReturn(transactionResponse2);

        var resultTransactions = transactionService.getAllTransactionsForWallet(walletId);
        var resultTransactionResponse1 = resultTransactions.get(0);
        var resultTransactionResponse2 = resultTransactions.get(1);

        // then
        assertNotNull(resultTransactions);
        assertEquals(transactions.size(), resultTransactions.size());

        assertEquals(transaction1.getTransactionId(), resultTransactionResponse1.getTransactionId());
        assertEquals(transaction1.getTransactionType(), resultTransactionResponse1.getTransactionType());
        assertEquals(transaction1.getAmount(), resultTransactionResponse1.getAmount());
        assertEquals(transaction1.getRecipientMobileNumber(), resultTransactionResponse1.getRecipientMobileNumber());
        assertEquals(transaction1.getWallet().getWalletId(), resultTransactionResponse1.getWalletId());
        assertEquals(transaction1.getUser().getUserId(), resultTransactionResponse1.getUserId());

        assertEquals(transaction2.getTransactionId(), resultTransactionResponse2.getTransactionId());
        assertEquals(transaction2.getTransactionType(), resultTransactionResponse2.getTransactionType());
        assertEquals(transaction2.getAmount(), resultTransactionResponse2.getAmount());
        assertEquals(transaction2.getRecipientMobileNumber(), resultTransactionResponse2.getRecipientMobileNumber());
        assertEquals(transaction2.getWallet().getWalletId(), resultTransactionResponse2.getWalletId());
        assertEquals(transaction2.getUser().getUserId(), resultTransactionResponse2.getUserId());

        verify(walletRepository, times(1)).findById(walletId);
        verify(transactionRepository, times(1)).findByWallet(wallet);
        verify(transactionMapper, times(1)).mapTransactionToDto(transaction1);
        verify(transactionMapper, times(1)).mapTransactionToDto(transaction2);
    }
}