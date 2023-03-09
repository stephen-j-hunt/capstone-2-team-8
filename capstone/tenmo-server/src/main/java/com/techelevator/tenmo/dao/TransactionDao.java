package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransactionDao {

    void sendMoney(Principal principal, int receiveId, BigDecimal amount);

    List<Transaction> myTransfers(int id);

    Transaction transferById(int id);

}
