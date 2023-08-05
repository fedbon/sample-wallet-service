package ru.fedbon.model.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
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
