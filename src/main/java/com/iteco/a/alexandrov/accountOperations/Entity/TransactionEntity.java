package com.iteco.a.alexandrov.accountOperations.Entity;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_transactional_sequence")
    @SequenceGenerator(
            name = "pk_transactional_sequence",
            sequenceName = "transactional_id_seq",
            initialValue = 1,
            allocationSize = 1)
    private long id;

    @Positive
    @Column(name = "wallet_id", nullable = false)
    private long walletId;

    @Length(max = 16)
    @Column(nullable = false)
    private String walletName;

    @Length(max = 3)
    @Column(nullable = false)
    private String transactionType;

    @Positive
    @Column(precision = 16, scale = 2, nullable = false)
    private BigDecimal transactionAmount;

    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal walletAccountAfterTransaction;

    @Column
    @UpdateTimestamp
    private LocalDateTime transactionalDate;

    public TransactionEntity() {
    }

    public TransactionEntity(long walletId, String walletName, String transactionType,
                             BigDecimal transactionAmount, BigDecimal walletAccountAfterTransaction) {
        this.walletId = walletId;
        this.walletName = walletName;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.walletAccountAfterTransaction = walletAccountAfterTransaction;
    }

    public long getId() {
        return id;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getWalletAccountAfterTransaction() {
        return walletAccountAfterTransaction;
    }

    public void setWalletAccountAfterTransaction(BigDecimal walletAccountAfterTransaction) {
        this.walletAccountAfterTransaction = walletAccountAfterTransaction;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public LocalDateTime getTransactionalDate() {
        return transactionalDate;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", walletId=" + walletId +
                ", walletName='" + walletName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", walletAccountAfterTransaction=" + walletAccountAfterTransaction +
                ", transactionalDate=" + transactionalDate +
                '}';
    }
}
