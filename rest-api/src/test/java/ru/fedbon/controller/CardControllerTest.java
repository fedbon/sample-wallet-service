package ru.fedbon.controller;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.fedbon.dto.CardDto;
import ru.fedbon.model.Card;
import ru.fedbon.service.impl.CardServiceImpl;
import ru.fedbon.utils.JsonStringWrapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @InjectMocks
    private CardController cardController;

    @Mock
    private CardServiceImpl cardService;

    private MockMvc mockMvc;

    private JsonStringWrapper jsonStringWrapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
        jsonStringWrapper = new JsonStringWrapper();
    }

    @Test
    @DisplayName("POST /api/cards возвращает корректное тело ответа о сохраненной карте и HTTP-статус CREATED")
    void testHandleSaveCard() throws Exception {
        // given
        var cardDto = new CardDto();
        cardDto.setCardNumber("1234567890123456");
        cardDto.setCardHolderName("Gordon Freeman");
        cardDto.setCvvCode("123");
        cardDto.setExpiryDate("12/25");

        var savedCard = new Card();
        savedCard.setCardId(1L);
        savedCard.setCardNumber(cardDto.getCardNumber());
        savedCard.setCardHolderName(cardDto.getCardHolderName());
        savedCard.setCvvCode(cardDto.getCvvCode());
        savedCard.setExpiryDate(cardDto.getExpiryDate());


        // when
        when(cardService.saveCard(any(CardDto.class))).thenReturn(savedCard);

        // then
        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(cardDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardId").value(savedCard.getCardId()))
                .andExpect(jsonPath("$.cardNumber").value(savedCard.getCardNumber()))
                .andExpect(jsonPath("$.cardHolderName").value(savedCard.getCardHolderName()))
                .andExpect(jsonPath("$.cvvCode").value(savedCard.getCvvCode()))
                .andExpect(jsonPath("$.expiryDate").value(savedCard.getExpiryDate()));

        verify(cardService, times(1)).saveCard(any(CardDto.class));
    }

    @Test
    @DisplayName("GET /by-wallet/{walletId} возвращает корректное тело ответа со списком всех привязанных " +
            "к кошельку карт и HTTP-статус ОК")
    void testHandleGetAllCardsForWallet() throws Exception {
        // given
        long walletId = 1L;

        var cardList = List.of(
                new CardDto(1L, "1234567890123456", "Gordon Freeman",
                        "123", "12/25", 1L, 1L),
                new CardDto(2L, "9876543210987654", "Luke Skywalker",
                        "456", "09/23", 1L, 2L)
        );

        // when
        when(cardService.getAllCardsForWallet(walletId)).thenReturn(cardList);

        // then
        mockMvc.perform(get("/api/cards/by-wallet/{walletId}", walletId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(cardList.size()))
                .andExpect(jsonPath("$[0].cardId").value(cardList.get(0).getCardId()))
                .andExpect(jsonPath("$[0].cardNumber").value(cardList.get(0).getCardNumber()))
                .andExpect(jsonPath("$[0].cardHolderName").value(cardList.get(0).getCardHolderName()))
                .andExpect(jsonPath("$[0].cvvCode").value(cardList.get(0).getCvvCode()))
                .andExpect(jsonPath("$[0].expiryDate").value(cardList.get(0).getExpiryDate()))
                .andExpect(jsonPath("$[1].cardId").value(cardList.get(1).getCardId()))
                .andExpect(jsonPath("$[1].cardNumber").value(cardList.get(1).getCardNumber()))
                .andExpect(jsonPath("$[1].cardHolderName").value(cardList.get(1).getCardHolderName()))
                .andExpect(jsonPath("$[1].cvvCode").value(cardList.get(1).getCvvCode()))
                .andExpect(jsonPath("$[1].expiryDate").value(cardList.get(1).getExpiryDate()));

        verify(cardService, times(1)).getAllCardsForWallet(walletId);
    }
}