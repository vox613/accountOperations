package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionEntity, Long> {


    List<TransactionEntity> findAllByWalletId(long id);

    Optional<TransactionEntity> findTransactionEntitiesByIdAndWalletId(long transactionId, long walletId);

}
