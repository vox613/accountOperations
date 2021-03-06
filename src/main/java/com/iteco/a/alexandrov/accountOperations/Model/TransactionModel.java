package com.iteco.a.alexandrov.accountOperations.Model;

import com.iteco.a.alexandrov.accountOperations.Validator.ValidOperationType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Contains data for a wallet account transaction
 */
public class TransactionModel {

    @NotNull
    @Positive
    @Min(value = 1)
    private long walletId;

    @NotNull
    @NotBlank
    @ValidOperationType
    private String transactionType;

    @NotNull
    @Positive
    private BigDecimal transactionAmount;


    public TransactionModel(long walletId, String transactionType, BigDecimal transactionAmount) {
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
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

    @Override
    public String toString() {
        return "TransactionModel{" +
                "walletId=" + walletId +
                ", transactionType='" + transactionType + '\'' +
                ", transactionAmount=" + transactionAmount +
                '}';
    }
}
