package com.redmath.bankingapp.transaction;

import com.redmath.bankingapp.account.Account;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String description;
    private int amount;
    @Column(name = "debit_credit_indicator")
    private String debitCreditIndicator;
    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


    public Transaction(){}

    public Transaction(LocalDate date, String description, int amount, String debitCreditIndicator, Account account) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.debitCreditIndicator = debitCreditIndicator;
        this.account = new Account(account);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
