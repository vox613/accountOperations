package com.iteco.a.alexandrov.accountOperations.Entity;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "operations_history")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_operations_sequence")
    @SequenceGenerator(
            name = "pk_operations_sequence",
            sequenceName = "operations_id_seq",
            initialValue = 1,
            allocationSize = 1)
    private long id;

    @Column(nullable = false)
    private long accountId;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    @Min(value = 0L)
    private long transactionAmount;


    public OperationEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString() {
        return "OperationEntity{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", operation='" + operation + '\'' +
                ", transactionAmount=" + transactionAmount +
                '}';
    }
}
