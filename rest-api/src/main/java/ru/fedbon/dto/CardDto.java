package ru.fedbon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Long cardId;
    private String cardNumber;
    private String cardHolderName;
    private String cvvCode;
    private String expiryDate;
    private Long walletId;
    private Long userId;
}
