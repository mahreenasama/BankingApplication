package com.redmath.bankingapp.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redmath.bankingapp.account.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.*;


@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uname;
    private String password;
    private String roles;
    private String status;
    @OneToOne
    @JoinColumn(name = "account_id")
    //@JsonIgnore                             //will ignore 'account' attribute while creating json object of balance
    private Account account;

    public User(){}
    public User(User user){
        this.id=user.id;
        this.uname=user.uname;
        this.password=user.password;
        this.roles=user.roles;
        this.status=user.status;
        this.account=user.account;
    }
    public User(String uname, String password, String roles, String status, Account account) {
        this.uname = uname;
        this.password = password;
        this.roles = roles;
        this.status = status;
        this.account = new Account(account);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() {
        return new Account(account);
    }

    public void setAccount(Account account) {
        this.account = new Account(account);
    }
}