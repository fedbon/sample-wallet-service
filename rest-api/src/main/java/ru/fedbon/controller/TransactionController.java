package ru.fedbon.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fedbon.dto.transaction.TransactionRequest;
import ru.fedbon.dto.transaction.TransactionResponse;
import ru.fedbon.model.transaction.Transaction;
import ru.fedbon.service.impl.TransactionServiceImpl;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping
    public ResponseEntity<Transaction> handleProcess(@RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.process(transactionRequest));
    }

    @GetMapping("/by-wallet/{walletId}")
    public ResponseEntity<List<TransactionResponse>> handleGetAllTransactionsForWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionService.getAllTransactionsForWallet(walletId));
    }

}