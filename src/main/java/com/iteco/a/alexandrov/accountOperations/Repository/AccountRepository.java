package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

//    @Modifying
//    @Query("UPDATE AccountEntity acc SET  acc.account = :account, acc.accountName = :name, acc.operation = :operation, acc.operationAmount = :operationAmount WHERE acc.id = :id")
//    int updateAccount(@Param("id") long id, @Param("account") String account, @Param("account") AccountEntity account,@Param("account") AccountEntity account,@Param("account") AccountEntity account,);
//
    @Modifying
    @Query("UPDATE AccountEntity acc SET acc.account = :account WHERE acc.id = :id")
    int updateAccountValue(@Param("id") long id, @Param("account") long account);

    @Modifying
    @Query("UPDATE AccountEntity acc SET acc.operation = :operation, acc.operationAmount = :amountOp WHERE acc.id = :id")
    int patchAccount(@Param("id") long id, @Param("operation") String operation, @Param("amountOp") long amountOp);

}
