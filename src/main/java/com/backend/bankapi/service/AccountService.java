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
import java.util.Optional;

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

    public Account findByIdAuth(Long id) throws ResourceNotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));
    }

    public AccountDao findAllBySsn(String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Account account = accountRepository.findByUserId(user)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        return new AccountDao(account.getDescription(), account.getBalance(), account.getAccountType().toString(),
                account.getAccountStatusType().toString(), account.getAccModInfId().getCreatedDate(),
                account.getAccModInfId().getClosedDate());
    }

    public AccountDao findBySsnId(Long id, String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        accountRepository.findByUserId(user)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Optional<Account> account = accountRepository.findById(id);

        return new AccountDao(account.get().getDescription(), account.get().getBalance(),
                account.get().getAccountType().toString(), account.get().getAccountStatusType().toString(),
                account.get().getAccModInfId().getCreatedDate(), account.get().getAccModInfId().getClosedDate());
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

    public void updateAccount(String ssn, Long id, Account account) throws BadRequestException {

        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));

        String lastModifiedBy = accountModifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(),
                user.getRoles());

        Timestamp lastModifiedDate = accountModifyInformation.setDate();

        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(acc.getAccModInfId().getId(),
                lastModifiedBy, lastModifiedDate);

        accModifyInformationRepository.save(accountModifyInformation);

        account.setUserId(user);
        account.setId(id);
        account.setAccModInfId(accountModifyInformation);

        accountRepository.save(account);
    }
}
