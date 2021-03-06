package com.iteco.a.alexandrov.accountOperations.Entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "wallets_table")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_wallet_sequence")
    @SequenceGenerator(
            name = "pk_wallet_sequence",
            sequenceName = "wallet_id_seq",
            allocationSize = 1)
    private Long id;

    @NotNull
    @PositiveOrZero
    @Column(precision = 16, scale = 2)
    private BigDecimal account;

    @NotBlank
    @Length(max = 16)
    @Column(unique = true)
    private String walletName;

    @Column
    @CreationTimestamp
    private LocalDateTime createDateTime;

    public WalletEntity() {
    }


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", account=" + account +
                ", walletName=" + walletName +
                ", createDateTime=" + createDateTime +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WalletEntity that = (WalletEntity) o;
        return id.equals(that.id) &&
                account.equals(that.account) &&
                walletName.equals(that.walletName) &&
                createDateTime.equals(that.createDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, walletName, createDateTime);
    }
}
