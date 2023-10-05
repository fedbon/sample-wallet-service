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
import ru.fedbon.dto.transaction.TransactionRequest;
import ru.fedbon.dto.transaction.TransactionResponse;
import ru.fedbon.model.transaction.Transaction;
import ru.fedbon.model.transaction.TransactionType;
import ru.fedbon.service.impl.TransactionServiceImpl;
import ru.fedbon.utils.JsonStringWrapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;



@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionServiceImpl transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    private JsonStringWrapper jsonStringWrapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        jsonStringWrapper = new JsonStringWrapper();
    }

    @Test
    @DisplayName("POST /api/transactions возвращает корректное тело ответа о " +
            "проведенной транзакции и HTTP-статус CREATED")
    void testHandleProcess() throws Exception {
        // given
        var transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionType(TransactionType.TRANSFER_WALLET_TO_WALLET);
        transactionRequest.setWalletId(1L);
        transactionRequest.setUserId(1L);
        transactionRequest.setAmount(1000.0);
        transactionRequest.setRecipientMobileNumber("testRecipientMobileNumber");

        var transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setRecipientMobileNumber(transactionRequest.getRecipientMobileNumber());
        transaction.setDateTimeProcessed(Instant.now());
        transaction.setIsCompleted(true);

        // when
        when(transactionService.process(any(TransactionRequest.class))).thenReturn(transaction);

        // then
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringWrapper.asJsonString(transactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(transaction.getTransactionId()))
                .andExpect(jsonPath("$.transactionType").value(transaction.getTransactionType().name()))
                .andExpect(jsonPath("$.amount").value(transaction.getAmount()))
                .andExpect(jsonPath("$.recipientMobileNumber").value(transaction.getRecipientMobileNumber()))
                .andExpect(jsonPath("$.dateTimeProcessed").exists())
                .andExpect(jsonPath("$.isCompleted").value(transaction.getIsCompleted()));

        verify(transactionService, times(1)).process(any(TransactionRequest.class));
    }

    @Test
    @DisplayName("GET /api/transactions/by-wallet/{walletId} возвращает корректное тело ответа " +
            "со списком транзакций выбранного кошелька и HTTP-статус ОК")
    void testHandleGetAllTransactionsForWallet() throws Exception {
        // given
        long walletId = 1L;

        var transactionResponseList = List.of(
                new TransactionResponse(1L, TransactionType.TRANSFER_WALLET_TO_WALLET, 1L,
                        1L, 2000.0, "testRecipientMobileNumber",
                        Instant.now(), true),
                new TransactionResponse(1L, TransactionType.TRANSFER_WALLET_TO_WALLET, 1L,
                        1L, 2000.0, "testRecipientMobileNumber",
                        Instant.now(), true)
        );

        // when
        when(transactionService.getAllTransactionsForWallet(anyLong())).thenReturn(transactionResponseList);

        // then
        mockMvc.perform(get("/api/transactions/by-wallet/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(transactionResponseList.size()))
                .andExpect(jsonPath("$[0].transactionId")
                        .value(transactionResponseList.get(0).getTransactionId()))
                .andExpect(jsonPath("$[0].transactionType")
                        .value(transactionResponseList.get(0).getTransactionType().name()))
                .andExpect(jsonPath("$[0].amount").value(transactionResponseList.get(0).getAmount()))
                .andExpect(jsonPath("$[0].recipientMobileNumber")
                        .value(transactionResponseList.get(0).getRecipientMobileNumber()))
                .andExpect(jsonPath("$[0].dateTimeProcessed").exists())
                .andExpect(jsonPath("$[0].isCompleted")
                        .value(transactionResponseList.get(0).getIsCompleted()))
                .andExpect(jsonPath("$[1].transactionId")
                        .value(transactionResponseList.get(1).getTransactionId()))
                .andExpect(jsonPath("$[1].transactionType")
                        .value(transactionResponseList.get(1).getTransactionType().name()))
                .andExpect(jsonPath("$[1].amount").value(transactionResponseList.get(1).getAmount()))
                .andExpect(jsonPath("$[1].recipientMobileNumber")
                        .value(transactionResponseList.get(1).getRecipientMobileNumber()))
                .andExpect(jsonPath("$[1].dateTimeProcessed").exists())
                .andExpect(jsonPath("$[1].isCompleted")
                        .value(transactionResponseList.get(1).getIsCompleted()));


        verify(transactionService, times(1)).getAllTransactionsForWallet(anyLong());
    }

}
