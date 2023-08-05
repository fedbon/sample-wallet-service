package ru.fedbon.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedbon.model.transaction.TransactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private TransactionType transactionType;
    private Long walletId;
    private Long userId;
    private Double amount;
    private String recipientMobileNumber;
}
