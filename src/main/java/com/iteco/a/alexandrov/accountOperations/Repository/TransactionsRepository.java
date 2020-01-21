package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionEntity, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    List<TransactionEntity> findAllByWalletId(long id);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<TransactionEntity> findTransactionEntitiesByIdAndWalletId(long transactionId, long walletId);

}
