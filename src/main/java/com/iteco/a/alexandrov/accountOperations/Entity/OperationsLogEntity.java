package com.iteco.a.alexandrov.accountOperations.Entity;

import javax.persistence.*;

@Entity
@Table(name = "operations_history")
public class OperationsLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private long accountId;

    @Column(nullable = false)
    private String requestType;

    @Column
    private String operation;

    @Column(nullable = false)
    private long transactionAmount;

    @Column(nullable = false)
    private long accountBeforeOperations;

    @Column(nullable = false)
    private long accountAfterOperations;

    public OperationsLogEntity() {
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

    public long getAccountBeforeOperations() {
        return accountBeforeOperations;
    }

    public void setAccountBeforeOperations(long accountBeforeOperations) {
        this.accountBeforeOperations = accountBeforeOperations;
    }

    public long getAccountAfterOperations() {
        return accountAfterOperations;
    }

    public void setAccountAfterOperations(long accountAfterOperations) {
        this.accountAfterOperations = accountAfterOperations;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "OperationsLogEntity{" +
                "id=" + id +
                ", operation='" + operation + '\'' +
                '}';
    }
}
