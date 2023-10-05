package ru.fedbon.service;

import ru.fedbon.dto.transaction.TransactionRequest;
import ru.fedbon.dto.transaction.TransactionResponse;
import ru.fedbon.model.transaction.Transaction;

import java.util.List;


public interface TransactionService {

    Transaction process(TransactionRequest transactionRequest);

    Transaction transferFromWalletToWallet(TransactionRequest transactionRequest);

    Transaction depositFromCardToWallet(TransactionRequest transactionRequest);

    List<TransactionResponse> getAllTransactionsForWallet(Long walletId);

}
