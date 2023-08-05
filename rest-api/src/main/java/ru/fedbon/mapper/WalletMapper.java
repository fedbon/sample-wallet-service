package ru.fedbon.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.fedbon.dto.WalletDto;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;

import java.time.Instant;

@Service
@AllArgsConstructor
public class WalletMapper {

    public Wallet mapDtoToWallet(WalletDto walletDto, User user) {

        if (walletDto == null && user == null)
            return null;

        var walletBuilder = Wallet.builder();

        if (walletDto != null && user != null) {
            walletBuilder.balance(0.0);
            walletBuilder.createdDate(Instant.now());
            walletBuilder.user(user);
        }
        return walletBuilder.build();

    }

    public WalletDto mapWalletToDto(Wallet wallet) {

        if (wallet == null)
            return null;

        var walletDto = new WalletDto();

        walletDto.setWalletId(wallet.getWalletId());
        walletDto.setBalance(wallet.getBalance());
        walletDto.setCreatedDate(wallet.getCreatedDate());
        walletDto.setUserId(wallet.getUser().getUserId());

        return walletDto;
    }

}
