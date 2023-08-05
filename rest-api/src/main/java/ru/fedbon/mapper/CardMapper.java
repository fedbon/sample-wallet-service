package ru.fedbon.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.fedbon.dto.CardDto;
import ru.fedbon.model.Card;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;


@Service
@AllArgsConstructor
public class CardMapper {

    public Card mapDtoToCard(CardDto cardDto, Wallet wallet, User user) {

        if (cardDto == null && wallet == null && user == null)
            return null;

        var cardBuilder = Card.builder();

        if (cardDto != null && wallet != null && user != null) {
            cardBuilder.cardNumber(cardDto.getCardNumber());
            cardBuilder.cardHolderName(cardDto.getCardHolderName());
            cardBuilder.cvvCode(cardDto.getCvvCode());
            cardBuilder.expiryDate(cardDto.getExpiryDate());
            cardBuilder.wallet(wallet);
            cardBuilder.user(user);
        }
        return cardBuilder.build();
    }

    public CardDto mapCardToDto(Card card) {

        if (card == null)
            return null;

        var cardDto = new CardDto();

        cardDto.setCardId(card.getCardId());
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setCardHolderName(card.getCardHolderName());
        cardDto.setCvvCode(card.getCvvCode());
        cardDto.setExpiryDate(card.getExpiryDate());
        cardDto.setWalletId(card.getWallet().getWalletId());
        cardDto.setUserId(card.getUser().getUserId());

        return cardDto;
    }

}
