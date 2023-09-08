package com.redmath.bankingapp.balance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query(value = "SELECT b1.* FROM balances b1 INNER JOIN (SELECT account_id, MAX(date) AS max_date FROM balances GROUP BY account_id) b2 ON b1.account_id = b2.account_id AND b1.date = b2.max_date ORDER BY account_id", nativeQuery = true)
    List<Balance> getLatestBalances();

    @Query(value = "SELECT * FROM balances where date = (SELECT MAX(date) FROM balances WHERE account_id = ?) and account_id = ?", nativeQuery = true)
    Balance getLatestBalanceByAccountId(Long accountId, Long accountId2);

    @Query(value = "SELECT * FROM balances where account_id = ?", nativeQuery = true)
    List<Balance> getBalanceHistoryByAccountId(Long accountId);

}
