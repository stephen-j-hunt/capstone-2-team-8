package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    public TenmoController(UserDao userDao, AccountDao accountDao, TransactionDao transactionDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @GetMapping(value = "/balance")
    public BigDecimal getBalance(Principal principal){
       User currentUser = userDao.findByUsername(principal.getName());

       Account account = accountDao.getAccountByUserId(currentUser.getId());
      return account.getBalance();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/transfer")
    public void transferMoney(Principal principal, @RequestParam int receiverId, @RequestParam BigDecimal amount, @RequestParam boolean isRequest){
        if(!isRequest){
            transactionDao.sendMoney(principal, receiverId, amount);
        } else {
            transactionDao.requestMoney(principal,receiverId,amount);
        }
    }

    @GetMapping(value="/transfer/list")
    public List<Transaction> transactionList(Principal principal){
      return transactionDao.myTransfers(principal);
    }
    @GetMapping(value="/transfer/list/{id}")
    public Transaction getTransactionById(@Valid @PathVariable int id){
        return transactionDao.transferById(id);
    }
    @GetMapping(value="/transfer/list/pending")
    public List<Transaction> pendingList(Principal principal) {
        return transactionDao.pendingTransfers(principal);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value= "/pending/approve")
    public void approveRequest(@RequestParam int id){
       transactionDao.approveTransaction(id);
    }

    @PostMapping(value = "/pending/deny")
    public void denyRequest(@RequestParam int id){
        transactionDao.denyTransaction(id);
    }
    @GetMapping(value = "/users")
    public List<Account> listAccounts(){
        return accountDao.listAccounts();
    }
}
