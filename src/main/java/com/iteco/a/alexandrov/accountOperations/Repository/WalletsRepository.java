package com.iteco.a.alexandrov.accountOperations.Repository;

import com.iteco.a.alexandrov.accountOperations.Entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletsRepository extends JpaRepository<WalletEntity, Long> {

    @Modifying
    @Query("UPDATE WalletEntity acc SET acc.account = :account, acc.walletName = :walletName WHERE acc.id = :id")
    void updateWallet(@Param("id") long id, @Param("account") BigDecimal account, @Param("walletName") String walletName);

    boolean existsByWalletName(String walletName);


    @Lock(LockModeType.OPTIMISTIC)
    Optional<WalletEntity> findById(long id);

    @NotNull
    @Lock(LockModeType.OPTIMISTIC)
    WalletEntity save(@NotNull WalletEntity walletEntity);

    @Lock(LockModeType.OPTIMISTIC)
    void deleteById(long id);

    @Lock(LockModeType.OPTIMISTIC)
    void deleteAll();


}
