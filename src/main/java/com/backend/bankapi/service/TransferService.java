package com.backend.bankapi.service;

import com.backend.bankapi.projection.ProjectTransferAdmin;
import com.backend.bankapi.projection.ProjectTransfer;
import com.backend.bankapi.dao.TransferDao;
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
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    private final static String SSN_NOT_FOUND_MSG = "user with ssn %s not found";
    private final static String ACCOUNT_NOT_FOUND_MSG = "account with id %s not found";
    private final static String TRANSFER_NOT_FOUND_MSG = "transfer with id %d not found";

    public List<ProjectTransferAdmin> fetchAllTransfers(){
        return transferRepository.findAllBy();
    }

    public ProjectTransferAdmin findByIdAuth(Long id) throws ResourceNotFoundException {
        return transferRepository.findByIdAndId(id, id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TRANSFER_NOT_FOUND_MSG, id)));
    }

    public List<ProjectTransfer> findAllBySsn(String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        return transferRepository.findAllByUserId(user);
    }

    public Optional<ProjectTransfer> findBySsnId(Long id, String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        return transferRepository.findByIdAndUserId(id, user);
    }

    public void create(String ssn, TransferDao transfer) {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Optional<Account> fromAccount = accountRepository.findByIdAndUserId(transfer.getFromAccountId(), user);
        Account toAccount = accountRepository.findById(transfer.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG,
                        transfer.getToAccountId())));

        if (transfer.getFromAccountId().equals(transfer.getToAccountId())) {
            throw new BadRequestException("Money transfers cannot be made with the same accounts!");
        }

        if (!transfer.getCurrencyCode().equals(fromAccount.get().getCurrencyCode())) {
            throw new BadRequestException("Currency does not match with your account!");
        }

        if (!toAccount.getCurrencyCode().equals(fromAccount.get().getCurrencyCode())) {
            throw new BadRequestException("Currency does not match!");
        }

        if (transfer.getTransactionAmount() > fromAccount.get().getBalance()){
            throw new BadRequestException("not enough funds available for transfer");
        }

        final Double newFromBalance = fromAccount.get().getBalance() - transfer.getTransactionAmount();

        final Double newToBalance = toAccount.getBalance() + transfer.getTransactionAmount();


        Date date= new Date();
        long time = date.getTime();
        Timestamp transactionDate = new Timestamp(time);

        Transfer transfer1 = new Transfer(fromAccount.get(), transfer.getToAccountId(), user,
                transfer.getTransactionAmount(), newFromBalance, transfer.getCurrencyCode(),
                transactionDate, transfer.getDescription());

        fromAccount.get().setBalance(newFromBalance);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount.get());
        accountRepository.save(toAccount);

        transferRepository.save(transfer1);
    }
}
