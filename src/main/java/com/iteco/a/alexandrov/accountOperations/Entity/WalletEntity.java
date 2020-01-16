package com.iteco.a.alexandrov.accountOperations.Entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets_table")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_wallet_sequence")
    @SequenceGenerator(
            name = "pk_wallet_sequence",
            sequenceName = "wallet_id_seq",
            initialValue = 1,
            allocationSize = 1)
    private long id;

    @NotNull
    @PositiveOrZero
    @Column(precision = 16, scale = 2)
    private BigDecimal account;

    @Column
    @NotBlank
    @Length(max = 16)
    private String walletName;

    @Column
    @CreationTimestamp
    private LocalDateTime createDateTime;


    public WalletEntity() {
    }

    public long getId() {
        return id;
    }


    public BigDecimal getAccount() {
        return account;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String accountName) {
        this.walletName = accountName;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    @Override
    public String toString() {
        return "WalletEntity{" +
                "id=" + id +
                ", account=" + account +
                ", walletName='" + walletName + '\'' +
                ", createDateTime=" + createDateTime +
                '}';
    }
}
