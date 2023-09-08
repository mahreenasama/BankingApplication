package com.redmath.bankingapp.balance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redmath.bankingapp.account.Account;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name="balances")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate date;
    private int amount;
    @Column(name = "debit_credit_indicator")
    private String debitCreditIndicator;
    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore                             //@JsonIgnore will ignore 'account' attribute while creating json object of balance
    private Account account;


    public Balance(){}

    public Balance(LocalDate date, int amount, String debitCreditIndicator, Account account) {
        this.date = date;
        this.amount = amount;
        this.debitCreditIndicator = debitCreditIndicator;
        this.account = new Account(account);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDebitCreditIndicator() {
        return debitCreditIndicator;
    }

    public void setDebitCreditIndicator(String debitCreditIndicator) {
        this.debitCreditIndicator = debitCreditIndicator;
    }

    public Account getAccount() {
        return new Account(account);
    }

    public void setAccount(Account account) {
        this.account = new Account(account);
    }

}
