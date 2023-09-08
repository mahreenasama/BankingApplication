package com.redmath.bankingapp.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redmath.bankingapp.balance.Balance;
import com.redmath.bankingapp.transaction.Transaction;
import com.redmath.bankingapp.user.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;                //name will be used to create username while login
    private String email;
    private String address;
    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)    //cascade All is to crud the balances associated with this account
    private List<Balance> balances;
    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private User user;


    public Account(){}

    public Account(Account account){
        this.id=account.id;
        this.name=account.name;
        this.email=account.email;
        this.address=account.address;
        this.balances=account.balances;
        this.transactions=account.transactions;
        this.user=account.user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Balance> getBalances() {
        return Collections.unmodifiableList(balances);
    }

    public void setBalances(List<Balance> balances) {
        this.balances = new ArrayList<>(balances);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    public User getUser() {
        return new User(user);
    }

    public void setUser(User user) {
        this.user = new User(user);
    }
}
