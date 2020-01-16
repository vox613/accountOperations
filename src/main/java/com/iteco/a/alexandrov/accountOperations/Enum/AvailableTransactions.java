package com.iteco.a.alexandrov.accountOperations.Enum;

public enum AvailableTransactions {
    SUM("sum"),
    SUB("sub");

    private String value;

    AvailableTransactions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "AvailableTransactions{" +
                "value='" + value + '\'' +
                '}';
    }
}
