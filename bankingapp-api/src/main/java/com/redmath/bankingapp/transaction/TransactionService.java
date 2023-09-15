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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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


    @Transactional
    public Transaction transferAmount(Long fromAccountId, Long toAccountId, int amount, Authentication auth) {

        Optional<Account> senderAccount = Optional.ofNullable(accountService.getAccountById(fromAccountId));
        Optional<Account> receiverAccount = Optional.ofNullable(accountService.getAccountById(toAccountId));

        User user = userService.findByUname(auth.getName());
        boolean userAuthenticated = true;

        if(user.getRoles().equals("USER")){
            userAuthenticated = user.getAccount().getId().toString().equals(fromAccountId.toString());
        }
        if(userAuthenticated && senderAccount.isPresent() && receiverAccount.isPresent()){
            Transaction transactionForSender = new Transaction(LocalDate.now(), "Transferred to account # "+toAccountId, amount, "- DB", accountService.getAccountById(fromAccountId));
            this.depositOrWithdrawAmount(fromAccountId, transactionForSender);       //withdraw from that account

            Transaction transactionForRecipient = new Transaction(transactionForSender.getDate(), "Received from account # "+fromAccountId, amount, "+ CR", accountService.getAccountById(toAccountId));
            return this.depositOrWithdrawAmount(toAccountId, transactionForRecipient);         //return that who received
        }
        return null;
    }

    public Transaction depositOrWithdrawAmount(Long accountId, Transaction transaction) {

        Optional<Account> account = Optional.ofNullable(accountService.getAccountById(accountId));

        if(account.isPresent()) {
            transaction.setDate(LocalDate.now());

            transaction.setAccount(accountService.getAccountById(accountId));
            Transaction newTransaction = transactionRepository.save(transaction);

            //now update balances after transaction as well:
            Balance oldBalance = balanceService.getLatestBalanceByAccId(accountId);

            int currentBalance;
            if(newTransaction.getDebitCreditIndicator().equals("+ CR")){
                currentBalance = oldBalance.getAmount() + newTransaction.getAmount();
            }
            else{
                currentBalance = oldBalance.getAmount() - newTransaction.getAmount();
            }

            if(oldBalance.getDate().toString().equals(newTransaction.getDate().toString())){
                //if transaction is performed on the same day, then just update balance
                Balance updatedBalance = new Balance(oldBalance.getDate(), currentBalance, newTransaction.getDebitCreditIndicator(), oldBalance.getAccount());
                balanceService.updateBalanceById(oldBalance.getId(), updatedBalance);
            }
            else{
                //else enter a new record/row of balance
                Balance newBalance = new Balance(newTransaction.getDate(), currentBalance, newTransaction.getDebitCreditIndicator(), oldBalance.getAccount());
                balanceService.createBalance(newBalance);
            }
            return newTransaction;
        }
        return null;
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
