package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)                  //write no open transactions found
    List<TransactionEntity> findAllByWalletId(long id);

    // TODO: 23.01.2020 Change all @Lock and check it works 
}
