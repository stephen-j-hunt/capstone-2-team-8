package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
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
                          jdbcTemplate.update(newTransaction,currentAccount.getId(),receiverAccount.getId(),amount, LocalDateTime.now());
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
    public List<Transaction> myTransfers(int id) {
        return null;
    }

    @Override
    public Transaction transferById(int id) {
        return null;
    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account = new Account();
        account.setId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
