package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {


    List<Account> listAccounts();

    BigDecimal viewBalance(int id);

    Account getAccountById(int id);

    Account getAccountByUserId(int id);

}
