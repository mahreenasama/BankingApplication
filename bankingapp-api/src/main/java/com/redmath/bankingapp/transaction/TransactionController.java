package com.redmath.bankingapp.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{accountId}")
    public ResponseEntity<Map<String, Transaction>> depositOrWithdrawAmount(@PathVariable("accountId") Long accountId, @RequestBody Transaction transaction, Authentication auth)
    {
        Transaction transactionMade = transactionService.depositAmount(accountId, transaction, auth);
        return ResponseEntity.ok(Map.of("content", transactionMade));

    }


    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PostMapping("/transfer/{fromAccountId}/{toAccountId}")
    public ResponseEntity<Map<String, Transaction>> transferAmount(@PathVariable("fromAccountId") Long fromAccountId,
                                                      @PathVariable("toAccountId") Long toAccountId,
                                                      @RequestBody int amount,
                                                      Authentication auth)
    {
        Transaction transferredTo = transactionService.transferAmount(fromAccountId, toAccountId, amount, auth);
        if (transferredTo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("content", transferredTo));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, List<Transaction>>> getAllTransactions()
    {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("content", transactions));
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/allTransactions/{accountId}")
    public ResponseEntity<Map<String, List<Transaction>>> getAllTransactionsByAccountId(@PathVariable("accountId") Long accountId, Authentication auth)
    {
        List<Transaction> transactions = transactionService.getAllTransactionsByAccountId(accountId, auth);
        if (transactions==null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        else if(transactions.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("content", transactions));
    }
}
