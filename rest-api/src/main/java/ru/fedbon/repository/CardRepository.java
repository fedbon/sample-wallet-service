package ru.fedbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.fedbon.model.Card;
import ru.fedbon.model.Wallet;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByWallet(Wallet wallet);
}
