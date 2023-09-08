package com.redmath.bankingapp.account;

import com.redmath.bankingapp.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, List<Account>>> getAllAccounts()
    {
        List<Account> accounts = accountService.getAllAccounts();
        if (accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("content", accounts));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Account>> getAccountById(@PathVariable("id") Long id, Authentication auth)
    {
        Account account;
        if(auth.getName().equals("admin")){
            account = accountService.getAccountById(id);
            if(account == null){
                return ResponseEntity.notFound().build();
            }
        }
        else{
            account = accountService.getAccountDetailsById(id, auth);
            if(account == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(Map.of("content", account));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Map<String, List<Account>>> getAccountsByNameLike(@RequestParam(name="name") String name)
    {
        List<Account> accounts = accountService.getAccountsByNameLike(name);
        if (accounts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("content", accounts));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Account>> createAccount(@RequestBody Account account)
    {
        Account createdAccount = accountService.createAccount(account);
        if (createdAccount == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  //account with this Id already exists
        }
        return ResponseEntity.ok(Map.of("content", createdAccount));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Account>> updateAccountById(@PathVariable("id") Long id, @RequestBody Account updatedAccountData)
    {
        Account updatedAccount = accountService.updateAccountById(id, updatedAccountData);
        if (updatedAccount != null) {
            return ResponseEntity.ok(Map.of("content", updatedAccount));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAccountById(@PathVariable("id") Long id)
    {
        boolean accountExistedAndDeleted = accountService.deleteAccountById(id);
        if(accountExistedAndDeleted){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
