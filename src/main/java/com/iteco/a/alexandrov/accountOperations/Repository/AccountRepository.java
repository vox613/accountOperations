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
//    @Query("UPDATE AccountEntity c SET c.account = :account WHERE c.id = :id")
//    AccountEntity updateAccount(@Param("newAccount") long id, @Param("account") long account);

}
