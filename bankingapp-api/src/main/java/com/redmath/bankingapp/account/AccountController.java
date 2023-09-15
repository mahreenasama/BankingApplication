package com.redmath.bankingapp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;


    /*@PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, List<Account>>> getAllAccounts()
    {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", accountService.getAllAccounts()));
    }*/

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, List<Account>>> getAccountsByNameLike(@RequestParam(name="name") String name)
    {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", accountService.getAccountsByNameLike(name)));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Account>> getAccountById(@PathVariable("id") Long id, Authentication auth)
    {
        if(auth.getName().equals("admin")){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", accountService.getAccountById(id)));
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", accountService.getAccountDetailsById(id, auth)));
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Account>> createAccount(@RequestBody Account account)
    {
        Account createdAccount = accountService.createAccount(account);
        if (createdAccount == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();  //account with this Id already exists
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", createdAccount));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}")
    public ResponseEntity<Map<String, Account>> updateAccountById(@PathVariable("id") Long id, @RequestBody Account updatedAccountData)
    {
        Account updatedAccount = accountService.updateAccountById(id, updatedAccountData);
        if (updatedAccount != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", updatedAccount));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAccountById(@PathVariable("id") Long id)
    {
        accountService.deleteAccountById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
