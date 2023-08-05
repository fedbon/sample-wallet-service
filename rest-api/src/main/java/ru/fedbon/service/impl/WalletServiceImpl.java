package ru.fedbon.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedbon.dto.WalletDto;
import ru.fedbon.model.Wallet;
import ru.fedbon.service.security.AuthServiceImpl;
import ru.fedbon.utils.ErrorMessage;
import ru.fedbon.exception.WalletNotFoundException;
import ru.fedbon.mapper.WalletMapper;
import ru.fedbon.repository.WalletRepository;
import ru.fedbon.service.WalletService;
import ru.fedbon.utils.Message;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {


    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final AuthServiceImpl authService;
    @Override
    public Wallet createNewWallet(WalletDto walletDto) {
        var wallet = walletRepository.save(walletMapper.mapDtoToWallet(walletDto, authService.getCurrentUser()));

        log.info(Message.CREATED, wallet);

        return wallet;
    }
    @Override
    @Transactional(readOnly = true)
    public List<WalletDto> getAllWalletsForUser() {
        var wallets = walletRepository.findAllByUser(authService.getCurrentUser());
        var walletDtoList = wallets.stream()
                .map(walletMapper::mapWalletToDto)
                .toList();

        log.info(Message.LIST, walletDtoList);

        return walletDtoList;
    }
    @Override
    @Transactional(readOnly = true)
    public Double getBalanceForWallet(Long walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(ErrorMessage.WALLET_NOT_FOUND + walletId));

        log.info(Message.WALLET_BALANCE, walletId, wallet.getBalance());

        return wallet.getBalance();
    }
}
