package ru.fedbon.model.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.fedbon.model.User;
import ru.fedbon.model.Wallet;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "recipient_mobile_number")
    private String recipientMobileNumber;

    @Column(name = "date_time_processed")
    private Instant dateTimeProcessed;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id")
    private Wallet wallet;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

}
