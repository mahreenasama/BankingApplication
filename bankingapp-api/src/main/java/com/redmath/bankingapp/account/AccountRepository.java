package com.redmath.bankingapp.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value="select * from accounts a where a.name like ?", nativeQuery = true)
    List<Account> findByNameLike(String name);

}
