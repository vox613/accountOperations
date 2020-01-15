package com.iteco.a.alexandrov.accountOperations.Entity;

import javax.persistence.*;

@Entity
@Table(name = "accounts_table")

public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_accounts_sequence")
    @SequenceGenerator(
            name = "pk_accounts_sequence",
            sequenceName = "accounts_id_seq",
            initialValue = 1,
            allocationSize = 1)
    private long id;

    @Column
    private long account;

    @Column
    private String accountName;


//    @Column(precision=19, scale=6)
//    BigDecimal money;

    public AccountEntity() {
    }

    public long getId() {
        return id;
    }


    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", account:" + account +
                ", accountName:'" + accountName + '\'' +
                '}';
    }
}
