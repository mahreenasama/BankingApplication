package com.redmath.bankingapp.balance;

import com.redmath.bankingapp.user.User;
import com.redmath.bankingapp.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private UserService userService;
    private final Logger logger= LoggerFactory.getLogger(getClass());


    public List<Balance> getLatestBalances(){
        return balanceRepository.getLatestBalances();
    }

    public Balance getLatestBalanceByAccountId(Long accountId, Authentication auth){
        User user = userService.findByUname(auth.getName());
        if(user.getRoles().equals("ADMIN")){
            return balanceRepository.getLatestBalanceByAccountId(accountId, accountId);
        }
        else{
            if(user.getAccount().getId().toString().equals(accountId.toString())) {
                return balanceRepository.getLatestBalanceByAccountId(accountId, accountId);
            }
            return null;
        }
    }

    public List<Balance> getBalanceHistoryByAccountId(Long accountId, Authentication auth) {
        User user = userService.findByUname(auth.getName());
        if(user.getRoles().equals("ADMIN")){
            return balanceRepository.getBalanceHistoryByAccountId(accountId);
        }
        else{
            if(user.getAccount().getId().toString().equals(accountId.toString())) {
                return balanceRepository.getBalanceHistoryByAccountId(accountId);
            }
            return null;
        }
    }

    public Balance createBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    public Balance updateBalanceById(Long id, Balance updatedBalanceData){
        updatedBalanceData.setId(id);                                   //assigning the same id so that it updates, not creates
        return balanceRepository.save(updatedBalanceData);
    }
}
