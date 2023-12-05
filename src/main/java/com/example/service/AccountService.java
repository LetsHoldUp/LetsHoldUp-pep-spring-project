package com.example.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
@Transactional
public class AccountService {

    // Account Repository
    AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    /***
     * 
     * @param account 
     * @return Account object with its {account_id} filled in, if we can create the account. Or null if we cannot
     */
    public Account registerUser(Account account) {
        // Check if the user name is not black
        if(account.getUsername().length() == 0)return null;

        // Check if the password is long enough
        if(account.getPassword().length() < 4) return null;

        // Check if the account already exists
        // If we do already have an account with the provided username, return an account with an id of -1
        if(accountRepository.findAccountByUsername(account.getUsername()).isPresent()){
            return new Account(-1, "", "");
        }

        // Persist this account
        accountRepository.save(account);

        // Finally, return the account that we just made
        return accountRepository.findAccountByUsername(account.getUsername()).get();
    }

    public Account loginUser(Account inAccount) {
        // Find the account we are trying to log into 
        Optional<Account> secureAccount = accountRepository.findAccountByUsername(inAccount.getUsername());

        // Check if the account even exists
        if(secureAccount.isPresent()){
            Account realAccount = secureAccount.get();
            // Reaching here means that our usernames match
            // Now we just need to compare the passwords
            String inPassword = inAccount.getPassword().strip().toString();
            String realPassword = realAccount.getPassword().strip().toString();
            if(inPassword.equals(realPassword)){
                // Both usernames and passwords matched, return our secureAccount
                return realAccount;
            }
        }

        // If either of the above checks fail, we will drop here
        return null;
    }

}
