package ru.fedbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.fedbon.model.transaction.Transaction;
import ru.fedbon.model.Wallet;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWallet(Wallet wallet);

}
