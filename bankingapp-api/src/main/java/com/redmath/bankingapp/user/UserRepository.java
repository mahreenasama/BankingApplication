package com.redmath.bankingapp.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUname(String uname);

    @Query(value = "SELECT * FROM users WHERE account_id = ?", nativeQuery = true)
    User findByAccountId(Long accountId);


}
