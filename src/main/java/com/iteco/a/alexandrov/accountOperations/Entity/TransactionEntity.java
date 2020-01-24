package com.iteco.a.alexandrov.accountOperations.Entity;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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
//    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
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

    public void setTransactionalDate(LocalDateTime transactionalDate) {
        this.transactionalDate = transactionalDate;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", walletId=" + walletId +
                ", walletName=" + walletName +
                ", transactionType=" + transactionType +
                ", transactionAmount=" + transactionAmount +
                ", walletAccountAfterTransaction=" + walletAccountAfterTransaction +
                ", transactionalDate=" + transactionalDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return id == that.id &&
                walletId == that.walletId &&
                walletName.equals(that.walletName) &&
                transactionType.equals(that.transactionType) &&
                transactionAmount.equals(that.transactionAmount) &&
                Objects.equals(walletAccountAfterTransaction, that.walletAccountAfterTransaction) &&
                transactionalDate.equals(that.transactionalDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, walletId, walletName, transactionType, transactionAmount, walletAccountAfterTransaction, transactionalDate);
    }
}
