package ru.fedbon.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedbon.dto.CardDto;
import ru.fedbon.model.Card;
import ru.fedbon.service.security.AuthServiceImpl;
import ru.fedbon.utils.ErrorMessage;
import ru.fedbon.exception.WalletNotFoundException;
import ru.fedbon.mapper.CardMapper;
import ru.fedbon.repository.CardRepository;
import ru.fedbon.repository.WalletRepository;
import ru.fedbon.service.CardService;
import ru.fedbon.utils.Message;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private final WalletRepository walletRepository;

    private final CardRepository cardRepository;

    private final AuthServiceImpl authService;

    private final CardMapper cardMapper;

    @Override
    @Transactional
    public Card saveCard(CardDto cardDto) {
        var wallet = walletRepository.findById(cardDto.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException(ErrorMessage.WALLET_NOT_FOUND + cardDto.getWalletId()));

        var card = cardRepository.save(cardMapper.mapDtoToCard(cardDto, wallet, authService.getCurrentUser()));

        log.info(Message.SAVED, card);

        return card;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDto> getAllCardsForWallet(Long walletId) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(ErrorMessage.WALLET_NOT_FOUND + walletId));
        var cards = cardRepository.findByWallet(wallet);
        var cardDtoList = cards.stream()
                .map(cardMapper::mapCardToDto)
                .toList();

        log.info(Message.LIST, cardDtoList);

        return cardDtoList;
    }
}
