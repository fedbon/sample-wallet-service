package ru.fedbon.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.fedbon.dto.transaction.TransactionRequest;
import ru.fedbon.dto.transaction.TransactionResponse;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;
import ru.fedbon.model.transaction.Transaction;

import java.time.Instant;

@Service
@AllArgsConstructor
public class TransactionMapper {

    public Transaction mapDtoToTransaction(TransactionRequest transactionRequest, Wallet wallet, User user) {

        if (transactionRequest == null && wallet == null && user == null)
            return null;

        var transactionBuilder = Transaction.builder();

        if (transactionRequest != null && wallet != null && user != null) {
            transactionBuilder.transactionType(transactionRequest.getTransactionType());
            transactionBuilder.amount(transactionRequest.getAmount());
            transactionBuilder.dateTimeProcessed(Instant.now());
            transactionBuilder.recipientMobileNumber(transactionRequest.getRecipientMobileNumber());
            transactionBuilder.isCompleted(true);
            transactionBuilder.wallet(wallet);
            transactionBuilder.user(user);
        }
        return transactionBuilder.build();

    }

    public TransactionResponse mapTransactionToDto(Transaction transaction) {

        if (transaction == null)
            return null;

        var transactionResponse = new TransactionResponse();

        transactionResponse.setTransactionId(transaction.getTransactionId());
        transactionResponse.setTransactionType(transaction.getTransactionType());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setRecipientMobileNumber(transaction.getRecipientMobileNumber());
        transactionResponse.setDateTimeProcessed(transaction.getDateTimeProcessed());
        transactionResponse.setIsCompleted(transaction.getIsCompleted());
        transactionResponse.setWalletId(transaction.getWallet().getWalletId());
        transactionResponse.setUserId(transaction.getUser().getUserId());

        return transactionResponse;
    }

}
