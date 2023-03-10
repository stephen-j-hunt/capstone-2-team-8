package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLTransactionRollbackException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao{
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(UserDao userDao, AccountDao accountDao, JdbcTemplate jdbcTemplate) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void sendMoney(Principal principal, int receiveId, BigDecimal amount) {
      /*
      Cant send to self *
      Cannot be negative or zero
      Amount can not surpass balance*
       */
        User currentUser = userDao.findByUsername(principal.getName());
        Account currentAccount = accountDao.getAccountByUserId(currentUser.getId());

        if(currentAccount.getId() != receiveId){
            BigDecimal zero = BigDecimal.ZERO;
            int res = amount.compareTo(zero);
            Account receiverAccount;
            if(res == 1){
                int result = amount.compareTo(currentAccount.getBalance());
                if(result == 0 || result == -1){
                    String sql = "SELECT * FROM account WHERE account_id = ?;";
                    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,receiveId);
                    if (sqlRowSet.next()){
                         receiverAccount = mapRowToAccount(sqlRowSet);
                         receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
                         currentAccount.setBalance(currentAccount.getBalance().subtract(amount));
                         String newTransaction = "INSERT INTO tenmo_transaction(\n" +
                                 "\t sender_id, receiver_id, is_request, status, amount, transaction_time)\n" +
                                 "\tVALUES ( ?, ?, ?, ?, ?, ?);";
                         jdbcTemplate.update(newTransaction,currentAccount.getId(),receiverAccount.getId(), false,true,amount, LocalDateTime.now());
                        String updateBalanceSql = "UPDATE account\n" +
                                "\tSET balance=?\n" +
                                "\tWHERE account_id =?;";
                        jdbcTemplate.update(updateBalanceSql, receiverAccount.getBalance(), receiverAccount.getId());
                        jdbcTemplate.update(updateBalanceSql, currentAccount.getBalance(), currentAccount.getId());
                    }
                }
            }
        }
    }
    @Override
    public List<Transaction> myTransfers(Principal principal) {
        User currentUser = userDao.findByUsername(principal.getName());
        Account currentAccount = accountDao.getAccountByUserId(currentUser.getId());
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * \n" +
                "FROM tenmo_transaction WHERE sender_id=? OR receiver_id=?;";
        SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, currentAccount.getId(), currentAccount.getId());
        while (rowset.next()) {
            Transaction transaction = mapRowToTransaction(rowset);
            transactions.add(transaction);
        }
        return transactions;
    }

    @Override
    public Transaction transferById(int id) {
        String sql = "Select * \n" +
                "FROM tenmo_transaction\n" +
                "WHERE transfer_id=?;";
        SqlRowSet results= jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()){
            return mapRowToTransaction(results);
        }
        return null;
    }

    @Override
    public void requestMoney(Principal principal, int receiveId, BigDecimal amount) {
        User currentUser = userDao.findByUsername(principal.getName());
        Account currentAccount = accountDao.getAccountByUserId(currentUser.getId());
        Account receiverAccount;
        /*
        cannot request from self
        cannot request zero or negative money
         */
        if(currentAccount.getId() != receiveId){
            BigDecimal zero = BigDecimal.ZERO;
            int res = amount.compareTo(zero);
            if (res == 1){
                String sql = "SELECT * FROM account WHERE account_id = ?;";
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,receiveId);
                if(sqlRowSet.next()){
                    receiverAccount= mapRowToAccount(sqlRowSet);
                    String newTransaction = "INSERT INTO tenmo_transaction(\n" +
                            "\t sender_id, receiver_id, is_request, status, amount, transaction_time)\n" +
                            "\tVALUES ( ?, ?, ?, ?, ?, ?);";
                    jdbcTemplate.update(newTransaction,currentAccount.getId(),receiverAccount.getId(),true,false,amount,LocalDateTime.now());
                }
            }
        }
    }

    @Override
    public List<Transaction> pendingTransfers(Principal principal) {
        //returns a list of transactions where status = false
        User currentUser = userDao.findByUsername(principal.getName());
        Account currentAccount = accountDao.getAccountByUserId(currentUser.getId());
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT *\n" +
                "\tFROM tenmo_transaction\n" +
                "\tWHERE status =false AND receiver_id=?;";
        SqlRowSet rowSet= jdbcTemplate.queryForRowSet(sql,currentAccount.getId());
        while (rowSet.next()) {
            Transaction pendingTransactions= mapRowToTransaction(rowSet);
            transactions.add(pendingTransactions);
        }
        return null;
    }

    @Override
    public void approveTransaction(int transferId) {
    // update transaction and then balances
        //  sql statement to find the transaction and the user ids* for balance updates
        Account userAccount = new Account();
        Account reqAccount = new Account();
String sql = "SELECT * \n" +
        "FROM tenmo_transaction\n" +
        "WHERE transfer_id =?;";
SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
if(rowSet.next()){
    Transaction pendingTransaction = mapRowToTransaction(rowSet);
    String requesterSql = "SELECT * \n" +
            "FROM account\n" +
            "WHERE account_id = ?;";
    SqlRowSet rs = jdbcTemplate.queryForRowSet(requesterSql, pendingTransaction.getSenderId());
    if(rs.next()){
        reqAccount = mapRowToAccount(rs);
    }
    String userSql = "SELECT * \n" +
            "FROM account\n" +
            "WHERE account_id = ?;";
    SqlRowSet sr = jdbcTemplate.queryForRowSet(userSql, pendingTransaction.getReceiverId());
    if(sr.next()){
        userAccount = mapRowToAccount(sr);
    }
    userAccount.setBalance(userAccount.getBalance().subtract(pendingTransaction.getAmount()));
    reqAccount.setBalance(reqAccount.getBalance().add(pendingTransaction.getAmount()));
    String updateBalanceSql = "UPDATE account\n" +
            "\tSET balance=?\n" +
            "\tWHERE account_id =?;";
    jdbcTemplate.update(updateBalanceSql, reqAccount.getBalance(), reqAccount.getId());
    jdbcTemplate.update(updateBalanceSql, userAccount.getBalance(), userAccount.getId());
}
    }

    @Override
    public void denyTransaction(int transferId) {
        //delete transaction
        String sql = "DELETE  \n" +
                "FROM tenmo_transaction \n" +
                "WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transferId);
    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account = new Account();
        account.setId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
    private Transaction mapRowToTransaction(SqlRowSet rs){
        Transaction transaction = new Transaction();
        transaction.setTransferId(rs.getInt("transfer_id"));
        transaction.setSenderId(rs.getInt("sender_id"));
        transaction.setReceiverId(rs.getInt("receiver_id"));
        transaction.setRequest(rs.getBoolean("is_request"));
        transaction.setStatus(rs.getBoolean("status"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDateTime(rs.getTimestamp("transaction_time").toLocalDateTime());
        return transaction;
    }

}
