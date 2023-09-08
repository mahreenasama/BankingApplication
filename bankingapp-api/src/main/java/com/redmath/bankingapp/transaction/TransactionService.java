package com.redmath.bankingapp.transaction;

import com.redmath.bankingapp.account.Account;
import com.redmath.bankingapp.account.AccountService;
import com.redmath.bankingapp.balance.Balance;
import com.redmath.bankingapp.balance.BalanceService;
import com.redmath.bankingapp.user.User;
import com.redmath.bankingapp.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    private final Logger logger= LoggerFactory.getLogger(getClass());


    public Transaction transferAmount(Long fromAccountId, Long toAccountId, int amount, Authentication auth) {

        Optional<Account> targetAccount = Optional.ofNullable(accountService.getAccountById(toAccountId));

        User user = userService.findByUname(auth.getName());
        boolean userAuthenticated = true;

        if(user.getRoles().equals("USER") && targetAccount.isPresent()){
            userAuthenticated = user.getAccount().getId().toString().equals(fromAccountId.toString());
        }
        if(userAuthenticated){
            Transaction transactionForSender = new Transaction(LocalDate.now(), "Transferred to account # "+toAccountId, amount, "- DB", accountService.getAccountById(fromAccountId));
            this.withdrawAmount(fromAccountId, transactionForSender, auth);       //withdraw from that account

            Transaction transactionForRecipient = new Transaction(transactionForSender.getDate(), "Received from account # "+fromAccountId, amount, "+ CR", accountService.getAccountById(toAccountId));
            return this.depositAmount(toAccountId, transactionForRecipient, auth);         //return that who received
        }
        return null;
    }

    public Transaction depositAmount(Long accountId, Transaction transaction, Authentication auth) {
        if(transaction.getDate()==null){
            transaction.setDate(LocalDate.now());
        }
        if(transaction.getDescription()==null){
            transaction.setDescription("Deposit");
        }
        if(transaction.getDebitCreditIndicator()==null){
            transaction.setDebitCreditIndicator("+ CR");
        }
        transaction.setAccount(accountService.getAccountById(accountId));
        Transaction newTransaction = transactionRepository.save(transaction);

        //now update balances after transaction as well:
        //Balance oldBalance = balanceService.getLatestBalanceByAccountId(accountId, auth);
        Balance oldBalance = balanceService.getLatestBalanceByAccountId(accountId, auth);


        if(oldBalance.getDate().toString().equals(newTransaction.getDate().toString())){
            //if transaction is performed on the same day, then just update balance
            Balance updatedBalance = new Balance(oldBalance.getDate(), oldBalance.getAmount() + newTransaction.getAmount(), "+ CR", oldBalance.getAccount());
            balanceService.updateBalanceById(oldBalance.getId(), updatedBalance);
        }
        else{
            //else enter a new record/row of balance
            Balance newBalance = new Balance(newTransaction.getDate(), oldBalance.getAmount() + newTransaction.getAmount(), "+ CR", oldBalance.getAccount());
            balanceService.createBalance(newBalance);
        }

        return newTransaction;
    }
    public Transaction withdrawAmount(Long accountId, Transaction transaction, Authentication auth) {
        if(transaction.getDate()==null){
            transaction.setDate(LocalDate.now());
        }
        if(transaction.getDescription()==null){
            transaction.setDescription("Withdraw");
        }
        if(transaction.getDebitCreditIndicator()==null){
            transaction.setDebitCreditIndicator("- DB");
        }
        transaction.setAccount(accountService.getAccountById(accountId));
        Transaction newTransaction = transactionRepository.save(transaction);

        //now update balances after transaction as well:
        Balance oldBalance = balanceService.getLatestBalanceByAccountId(accountId, auth);

        if(oldBalance.getDate().toString().equals(newTransaction.getDate().toString())){
            //if transaction is performed on the same day, then just update balance
            Balance updatedBalance = new Balance(oldBalance.getDate(), oldBalance.getAmount() - newTransaction.getAmount(), "- DB", oldBalance.getAccount());
            balanceService.updateBalanceById(oldBalance.getId(), updatedBalance);
        }
        else{
            //else enter a new record/row of balance
            Balance newBalance = new Balance(newTransaction.getDate(), oldBalance.getAmount() - newTransaction.getAmount(), "- DB", oldBalance.getAccount());
            balanceService.createBalance(newBalance);
        }

        return newTransaction;
    }

    public List<Transaction> getAllTransactionsByAccountId(Long accountId, Authentication auth){
        User user = userService.findByUname(auth.getName());
        if(user.getRoles().equals("ADMIN")){
            return transactionRepository.getTransactionsByAccountId(accountId);
        }
        else{
            if(user.getAccount().getId().toString().equals(accountId.toString())) {
                return transactionRepository.getTransactionsByAccountId(accountId);
            }
            return null;
        }
    }

    public List<Transaction> getAllTransactions(){
        logger.debug("findAll");
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactionRepository.findAll().forEach(Transaction -> transactions.add(Transaction));
        return transactions;
    }

}
