package com.iteco.a.alexandrov.accountOperations.Entity;

import javax.persistence.*;

@Entity
@Table(name = "accounts_table")

public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Column
    long account;


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

    @Override
    public String toString() {
        return "AccountEntity{" +
                "id=" + id +
                ", account=" + account +
                '}';
    }
}
