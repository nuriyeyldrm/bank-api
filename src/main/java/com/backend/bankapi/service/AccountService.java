package com.backend.bankapi.service;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.domain.*;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.repository.AccModifyInformationRepository;
import com.backend.bankapi.repository.AccountRepository;
import com.backend.bankapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    private final AccModifyInformationRepository accModifyInformationRepository;

    private final AccountModifyInformation accountModifyInformation = new AccountModifyInformation();

    private final static String ACCOUNT_NOT_FOUND_MSG = "account with id %d not found";

    private final static String SSN_NOT_FOUND_MSG = "account with ssn %s not found";

    public List<Account> fetchAllAccounts(){
        return accountRepository.findAll();
    }

    public Account findById(Long id) throws ResourceNotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));
    }

    public AccountDao findBySsn(String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Account account = accountRepository.getById(user.getId());

        return new AccountDao(account.getDescription(), account.getBalance(), account.getAccountType().toString(),
                account.getAccountStatusType().toString());
    }

    public void add(String ssn, Account account) throws BadRequestException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        String createdBy = accountModifyInformation.setModifiedBy(user.getFirstName(),
                user.getLastName(), user.getRoles());

        Timestamp createdDate = accountModifyInformation.setDate();

        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(createdBy, createdDate,
                createdBy, createdDate);

        accModifyInformationRepository.save(accountModifyInformation);

        account.setAccModInfId(accountModifyInformation);
        account.setUserId(user);

        accountRepository.save(account);
    }
}
