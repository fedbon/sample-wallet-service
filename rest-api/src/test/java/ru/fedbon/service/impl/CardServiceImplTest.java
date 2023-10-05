package ru.fedbon.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fedbon.dto.CardDto;
import ru.fedbon.mapper.CardMapper;
import ru.fedbon.model.Card;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;
import ru.fedbon.repository.CardRepository;
import ru.fedbon.repository.WalletRepository;
import ru.fedbon.service.security.AuthServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AuthServiceImpl authService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    @DisplayName("Корректно сохраняет новую карту в кошелек авторизованного пользователя пользователя")
    void testSaveCard() {
        // given
        var cardDto = new CardDto();
        cardDto.setCardId(1L);
        cardDto.setCardNumber("1234567812345678");
        cardDto.setCardHolderName("John Doe");
        cardDto.setCvvCode("123");
        cardDto.setExpiryDate("12/25");
        cardDto.setWalletId(1L);
        cardDto.setUserId(1L);

        var wallet = new Wallet();
        wallet.setWalletId(1L);

        var user = new User();
        user.setUserId(1L);

        var card = new Card();
        card.setCardId(1L);

        // when
        when(walletRepository.findById(cardDto.getWalletId())).thenReturn(Optional.of(wallet));
        when(authService.getCurrentUser()).thenReturn(user);
        when(cardMapper.mapDtoToCard(cardDto, wallet, user)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);

        var result = cardService.saveCard(cardDto);

        // then
        assertNotNull(result);
        assertEquals(card.getCardId(), result.getCardId());
        assertEquals(card.getCardNumber(), result.getCardNumber());
        assertEquals(card.getCardHolderName(), result.getCardHolderName());
        assertEquals(card.getCvvCode(), result.getCvvCode());
        assertEquals(card.getExpiryDate(), result.getExpiryDate());
        assertEquals(card.getWallet(), result.getWallet());
        assertEquals(card.getUser(), result.getUser());
    }

    @Test
    @DisplayName("Корректно возвращает список всех карт для кошелька авторизованного пользователя")
    void testGetAllCardsForWallet() {
        // given
        long walletId = 1L;

        var currentUser = new User();
        currentUser.setUserId(1L);

        var wallet = new Wallet();
        wallet.setWalletId(walletId);

        var card1 = new Card();
        card1.setCardId(1L);
        card1.setCardNumber("3410211133036789");
        card1.setCardHolderName("Max Payne");
        card1.setCvvCode("777");
        card1.setExpiryDate("03/25");
        card1.setWallet(wallet);
        card1.setUser(currentUser);

        var card2 = new Card();
        card2.setCardId(2L);
        card2.setCardNumber("2210101155036407");
        card2.setCardHolderName("John Shepard");
        card2.setCvvCode("222");
        card2.setExpiryDate("01/48");
        card2.setWallet(wallet);
        card2.setUser(currentUser);

        var cards = List.of(card1, card2);

        var cardDto1 = new CardDto();
        cardDto1.setCardId(card1.getCardId());
        cardDto1.setCardNumber(card1.getCardNumber());
        cardDto1.setCardHolderName(card1.getCardHolderName());
        cardDto1.setCvvCode(card1.getCvvCode());
        cardDto1.setExpiryDate(card1.getExpiryDate());
        cardDto1.setWalletId(wallet.getWalletId());
        cardDto1.setUserId(currentUser.getUserId());

        var cardDto2 = new CardDto();
        cardDto2.setCardId(card2.getCardId());
        cardDto2.setCardNumber(card2.getCardNumber());
        cardDto2.setCardHolderName(card2.getCardHolderName());
        cardDto2.setCvvCode(card2.getCvvCode());
        cardDto2.setExpiryDate(card2.getExpiryDate());
        cardDto2.setWalletId(wallet.getWalletId());
        cardDto2.setUserId(currentUser.getUserId());

        // when
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(cardRepository.findByWallet(wallet)).thenReturn(cards);
        when(cardMapper.mapCardToDto(card1)).thenReturn(cardDto1);
        when(cardMapper.mapCardToDto(card2)).thenReturn(cardDto2);

        var result = cardService.getAllCardsForWallet(wallet.getWalletId());
        var resultCardDto1 = result.get(0);
        var resultCardDto2 = result.get(1);

        // then
        assertNotNull(result);
        assertEquals(cards.size(), result.size());

        assertEquals(cardDto1.getCardId(), resultCardDto1.getCardId());
        assertEquals(cardDto1.getCardNumber(), resultCardDto1.getCardNumber());
        assertEquals(cardDto1.getCardHolderName(), resultCardDto1.getCardHolderName());
        assertEquals(cardDto1.getCvvCode(), resultCardDto1.getCvvCode());
        assertEquals(cardDto1.getExpiryDate(), resultCardDto1.getExpiryDate());
        assertEquals(wallet.getWalletId(), resultCardDto1.getWalletId());
        assertEquals(currentUser.getUserId(), resultCardDto1.getUserId());

        assertEquals(cardDto2.getCardId(), resultCardDto2.getCardId());
        assertEquals(cardDto2.getCardNumber(), resultCardDto2.getCardNumber());
        assertEquals(cardDto2.getCardHolderName(), resultCardDto2.getCardHolderName());
        assertEquals(cardDto2.getCvvCode(), resultCardDto2.getCvvCode());
        assertEquals(cardDto2.getExpiryDate(), resultCardDto2.getExpiryDate());
        assertEquals(wallet.getWalletId(), resultCardDto2.getWalletId());
        assertEquals(currentUser.getUserId(), resultCardDto2.getUserId());

        verify(walletRepository, times(1)).findById(walletId);
        verify(cardRepository, times(1)).findByWallet(wallet);
        verify(cardMapper, times(1)).mapCardToDto(card1);
        verify(cardMapper, times(1)).mapCardToDto(card2);
    }
}