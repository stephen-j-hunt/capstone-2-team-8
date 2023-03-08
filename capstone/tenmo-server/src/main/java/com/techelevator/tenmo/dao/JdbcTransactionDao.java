package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransactionDao implements TransactionDao{
    @Override
    public BigDecimal sendMoney(int sendId, int receiveId, BigDecimal amount) {
      /*
      Cant send to self
      Cannot be negative or zero
      Amount can not surpas balance
       */
        return null;
    }

    @Override
    public List<Transaction> myTransfers(int id) {
        return null;
    }

    @Override
    public Transaction transferById(int id) {
        return null;
    }
}
