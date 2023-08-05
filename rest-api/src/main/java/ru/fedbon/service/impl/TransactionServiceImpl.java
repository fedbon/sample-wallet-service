package ru.fedbon.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedbon.dto.transaction.TransactionRequest;
import ru.fedbon.dto.transaction.TransactionResponse;
import ru.fedbon.model.transaction.Transaction;
import ru.fedbon.service.security.AuthServiceImpl;
import ru.fedbon.utils.ErrorMessage;
import ru.fedbon.exception.InsufficientFundsException;
import ru.fedbon.exception.WalletNotFoundException;
import ru.fedbon.mapper.TransactionMapper;
import ru.fedbon.repository.TransactionRepository;
import ru.fedbon.repository.WalletRepository;
import ru.fedbon.service.TransactionService;
import ru.fedbon.utils.Message;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {


    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuthServiceImpl authService;

    @Override
    public Transaction process(TransactionRequest transactionRequest) {
        return switch (transactionRequest.getTransactionType()) {
            case TRANSFER_WALLET_TO_WALLET -> transferFromWalletToWallet(transactionRequest);
            case DEPOSIT_CARD_TO_WALLET -> depositFromCardToWallet(transactionRequest);
        };
    }
    @Override
    public Transaction transferFromWalletToWallet(TransactionRequest transactionRequest) {
        var wallet = walletRepository.findById(transactionRequest.getWalletId());
        if (wallet.isPresent()) {
            var walletInstance = wallet.get();
            double newBalance = walletInstance.getBalance() - transactionRequest.getAmount();
            if (newBalance >= 0) {
                walletInstance.setBalance(newBalance);
                walletRepository.save(walletInstance);
            } else {
                throw new InsufficientFundsException(
                        ErrorMessage.WALLET_NOT_ENOUGH_MONEY + transactionRequest.getWalletId());
            }
            var transaction = transactionRepository.save(transactionMapper
                    .mapDtoToTransaction(transactionRequest, walletInstance, authService.getCurrentUser()));

            log.info(Message.TRANSACTION_PROCESSED, transaction);

            return transaction;
        } else {
            throw new WalletNotFoundException(ErrorMessage.WALLET_NOT_FOUND + transactionRequest.getWalletId());
        }
    }
    @Override
    public Transaction depositFromCardToWallet(TransactionRequest transactionRequest) {
        var wallet = walletRepository.findById(transactionRequest.getWalletId());
        if (wallet.isPresent()) {
            var walletInstance = wallet.get();
            double newBalance = walletInstance.getBalance() + transactionRequest.getAmount();
            walletInstance.setBalance(newBalance);

            walletRepository.save(walletInstance);
            var transaction = transactionRepository.save(transactionMapper
                    .mapDtoToTransaction(transactionRequest, walletInstance, authService.getCurrentUser()));

            log.info(Message.TRANSACTION_PROCESSED, transaction);

            return transaction;
        } else {
            throw new WalletNotFoundException(ErrorMessage.WALLET_NOT_FOUND + transactionRequest.getWalletId());
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactionsForWallet(Long walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(ErrorMessage.WALLET_NOT_FOUND + walletId));

        var transactions = transactionRepository.findByWallet(wallet);
        var transactionDtoList = transactions.stream()
                .map(transactionMapper::mapTransactionToDto)
                .toList();

        log.info(Message.LIST, transactionDtoList);

        return transactionDtoList;
    }
}
