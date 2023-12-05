package com.example.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{

    Optional<Account> findAccountByUsername(String username);

    //Optional<Account> findAccountByAccount_id(int account_id);

    //@Modifying
    //@Query("INSERT INTO Account(username, password) VALUES (:UN, :PW)")
    //void createAccount(@Param("UN") String username, @Param("PW") String password);

    //void createAccount(String username, String password);



}
