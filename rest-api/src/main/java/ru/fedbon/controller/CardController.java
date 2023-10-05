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
import ru.fedbon.dto.CardDto;
import ru.fedbon.model.Card;
import ru.fedbon.service.impl.CardServiceImpl;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/cards")
public class CardController {

    private final CardServiceImpl cardService;

    @PostMapping
    public ResponseEntity<Card> handleSaveCard(@RequestBody CardDto cardDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.saveCard(cardDto));
    }

    @GetMapping("/by-wallet/{walletId}")
    public ResponseEntity<List<CardDto>> handleGetAllCardsForWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cardService.getAllCardsForWallet(walletId));
    }

}