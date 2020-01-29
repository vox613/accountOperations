package com.iteco.a.alexandrov.accountOperations.Enum;

public enum AvailableOperations {
    SUM("sum"),
    SUB("sub");

    private String value;

    AvailableOperations(String value) {
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
