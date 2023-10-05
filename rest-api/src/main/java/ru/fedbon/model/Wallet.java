package ru.fedbon.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.fedbon.model.transaction.Transaction;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "balance")
    private double balance;

    @Column(name = "created_date")
    private Instant createdDate;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wallet")
    private List<Card> cards;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wallet")
    private List<Transaction> transactions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

}
