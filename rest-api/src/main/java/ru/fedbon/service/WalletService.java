package ru.fedbon.service;


import ru.fedbon.dto.WalletDto;
import ru.fedbon.model.Wallet;

import java.util.List;


public interface WalletService {

    Wallet createNewWallet(WalletDto walletDto);

    List<WalletDto> getAllWalletsForUser();

    Double getBalanceForWallet(Long walletId);
}
