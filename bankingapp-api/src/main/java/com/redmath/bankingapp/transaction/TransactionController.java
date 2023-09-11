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
    @PostMapping
    public ResponseEntity<Map<String, Transaction>> depositOrWithdrawAmount(@RequestParam(name = "accountId") Long accountId, @RequestBody Transaction transaction)
    {
        Transaction transactionMade = transactionService.depositOrWithdrawAmount(accountId, transaction);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", transactionMade));
    }


    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Transaction>> transferAmount(@RequestParam(name = "fromAccountId") Long fromAccountId,
                                                      @RequestParam(name = "toAccountId") Long toAccountId,
                                                      @RequestBody int amount,
                                                      Authentication auth)
    {
        Transaction transferredTo = transactionService.transferAmount(fromAccountId, toAccountId, amount, auth);
        if (transferredTo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", transferredTo));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, List<Transaction>>> getAllTransactions()
    {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", transactions));
    }

    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/transaction-history")
    public ResponseEntity<Map<String, List<Transaction>>> getAllTransactionsByAccountId(@RequestParam(name = "accountId") Long accountId, Authentication auth)
    {
        List<Transaction> transactions = transactionService.getAllTransactionsByAccountId(accountId, auth);
        if (transactions==null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        else if(transactions.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("content", transactions));
    }
}
