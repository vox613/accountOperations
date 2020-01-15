package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Modifying
    @Query("UPDATE AccountEntity acc SET acc.account = :account, acc.accountName = :accountName WHERE acc.id = :id")
    void updateAccount(@Param("id") long id, @Param("account") long account, @Param("accountName") String accountName);


}
