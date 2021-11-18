package com.backend.bankapi.service;

import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.repository.AccountRepository;
import com.backend.bankapi.repository.TransferRepository;
import com.backend.bankapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    private final static String SSN_NOT_FOUND_MSG = "user with ssn %s not found";
    private final static String ACC_NOT_FOUND_MSG = "account with id %d not found";

    public void create(String ssn, Transfer transfer) {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Optional<Account> fromAccount = accountRepository.findByIdAndUserId(transfer.getFromAccountId(), user);
        Account toAccount = accountRepository.findById(transfer.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACC_NOT_FOUND_MSG,
                        transfer.getToAccountId())));

        if (transfer.getTransactionAmount() > fromAccount.get().getBalance()){
            throw new BadRequestException("not enough funds available for transfer");
        }

        final Double newFromBalance = fromAccount.get().getBalance() - transfer.getTransactionAmount();

        final Double newToBalance = toAccount.getBalance() + transfer.getTransactionAmount();


        Date date= new Date();
        long time = date.getTime();
        Timestamp transactionDate = new Timestamp(time);
        transfer.setTransactionDate(transactionDate);
        transfer.setUserId(user);
        transfer.setNewBalance(newFromBalance);

        fromAccount.get().setBalance(newFromBalance);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount.get());
        accountRepository.save(toAccount);

        transferRepository.save(transfer);
    }
}
