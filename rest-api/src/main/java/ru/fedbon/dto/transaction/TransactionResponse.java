package ru.fedbon.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.fedbon.model.transaction.TransactionType;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "dateTimeProcessed")
public class TransactionResponse {
    private Long transactionId;
    private TransactionType transactionType;
    private Long walletId;
    private Long userId;
    private Double amount;
    private String recipientMobileNumber;
    private Instant dateTimeProcessed;
    private Boolean isCompleted;
}
