package com.redmath.bankingapp.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM transactions where account_id = ?", nativeQuery = true)
    List<Transaction> getTransactionsByAccountId(long accountId);
}
