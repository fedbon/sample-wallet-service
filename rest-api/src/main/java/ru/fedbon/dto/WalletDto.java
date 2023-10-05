package ru.fedbon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
    private Long walletId;

    private Double balance;

    private Instant createdDate;

    private Long userId;
}
