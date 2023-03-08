package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Account> listAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id \n" +
                "FROM account;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }

        return accounts;
    }

    @Override
    public BigDecimal viewBalance(int id) {
        BigDecimal balance;
        String sql = "SELECT balance From account WHERE account_id=?;";
        balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
        return balance;


    }

    @Override
    public Account getAccountById(int id) {
        String sql = "select * \n" +
                "from account \n" +
                "where account_id =?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            return mapRowToAccount(results);
        } else {
            return null;
        }
    }

    @Override
    public Account getAccountByUserId(int id) {
        String sql = "select * \n" +
                "from account \n" +
                "where user_id =?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            return mapRowToAccount(results);
        } else {
            return null;
        }
    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account = new Account();
        account.setId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }

}


