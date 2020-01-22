package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionEntity, Long> {

    //    @Lock(LockModeType.OPTIMISTIC)                    write no open transactions found
    List<TransactionEntity> findAllByWalletId(long id);

}
