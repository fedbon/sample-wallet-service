package ru.fedbon.service;

import ru.fedbon.dto.CardDto;
import ru.fedbon.model.Card;

import java.util.List;

public interface CardService {

    Card saveCard(CardDto cardDto);

    List<CardDto> getAllCardsForWallet(Long walletId);
}
