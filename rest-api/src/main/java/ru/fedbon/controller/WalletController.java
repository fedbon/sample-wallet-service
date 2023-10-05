package ru.fedbon.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fedbon.dto.WalletDto;
import ru.fedbon.model.Wallet;
import ru.fedbon.service.impl.WalletServiceImpl;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletServiceImpl walletService;

    @PostMapping
    public ResponseEntity<Wallet> handleCreateNewWallet(@RequestBody WalletDto walletDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(walletService.createNewWallet(walletDto));
    }

    @GetMapping
    public ResponseEntity<List<WalletDto>> handleGetAllWalletsForUser() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(walletService.getAllWalletsForUser());
    }

    @GetMapping("/by-wallet/{walletId}")
    public ResponseEntity<String> handleGetBalanceForWallet(@PathVariable Long walletId) {
        var balance = walletService.getBalanceForWallet(walletId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Balance: " + balance);
    }
}