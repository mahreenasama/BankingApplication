package com.redmath.bankingapp.account;

import com.redmath.bankingapp.balance.Balance;
import com.redmath.bankingapp.balance.BalanceService;
import com.redmath.bankingapp.user.User;
import com.redmath.bankingapp.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Logger logger= LoggerFactory.getLogger(getClass());
    @Value("${student.db.like.operator:%}")    //used to set default value of things
    private String likeOperator;


    /*public List<Account> getAllAccounts()
    {
        logger.debug("findAll");
        List<Account> accounts = new ArrayList<Account>();
        accountRepository.findAll().forEach(Account -> accounts.add(Account));
        return accounts;
    }*/

    public Account getAccountById(Long id)
    {
        logger.info("find byId: {},{}",id.toString().replaceAll("\r\n",""), "test");
        return accountRepository.findById(id).orElse(null);
    }

    public Account getAccountDetailsById(Long id, Authentication auth)
    {
        User user = userService.findByUname(auth.getName());

        if(user.getAccount().getId().toString().equals(id.toString())) {
            return accountRepository.findById(id).orElse(null);
        }
        return null;
    }

    public List<Account> getAccountsByNameLike(String name){
        logger.debug("findAllByNameLike");
        return accountRepository.findByNameLike(likeOperator+name+likeOperator);
    }

    public Account createAccount(Account account)
    {
        if (account.getId() != null && accountRepository.existsById(account.getId())) {
            logger.warn("Account already exist");
            return null;
        }
        Account newAccount= accountRepository.save(account);    //create account

        String hashedPassword = passwordEncoder.encode(newAccount.getName());
        User user = new User(newAccount.getName()+newAccount.getId(), hashedPassword, "USER", "ACTIVE", newAccount);
        userService.createUser(user);                           //create a user for this account

        Balance balance=new Balance(LocalDate.now(), 0, "+ CR", newAccount);
        balanceService.createBalance(balance);                  //created balance zero at the time of account creation

        return newAccount;
    }

    public Account updateAccountById(Long id, Account updatedAccountData){
        Optional<Account> oldAccountData=accountRepository.findById(id);

        if(oldAccountData.isPresent()){
            Account updatedAccount=oldAccountData.get();

            updatedAccount.setName(updatedAccountData.getName());
            updatedAccount.setEmail(updatedAccountData.getEmail());
            updatedAccount.setAddress(updatedAccountData.getAddress());

            updatedAccount = accountRepository.save(updatedAccount);

            //now update the user table as well (to update login info according to name)
            User oldUserData = updatedAccount.getUser();
            User updatedUser=new User(updatedAccount.getName()+updatedAccount.getId(), "{noop}"+updatedAccount.getName(), oldUserData.getRoles(), oldUserData.getStatus(), oldUserData.getAccount());
            userService.updateUserById(oldUserData.getId(), updatedUser);

            return updatedAccount;
        }
        return null;
    }

    public boolean deleteAccountById(Long id)
    {
        if(!accountRepository.existsById(id)){
            return false;
        }
        accountRepository.deleteById(id);
        return true;
    }
}
