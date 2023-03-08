package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDao {

    BigDecimal sendMoney(int sendId, int receiveId, BigDecimal amount);

    List<Transaction> myTransfers(int id);

    Transaction transferById(int id);

}
