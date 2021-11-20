package com.backend.bankapi.service;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.domain.*;
import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.repository.AccModifyInformationRepository;
import com.backend.bankapi.repository.AccountRepository;
import com.backend.bankapi.repository.TransferRepository;
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

    private final TransferRepository transferRepository;

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

    public List<AccountDao> findAllBySsn(String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        return accountRepository.findAllByyUserId(user);
    }

    public AccountDao findBySsnId(Long id, String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        accountRepository.findByUserId(user)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Optional<Account> account = accountRepository.findById(id);

        return new AccountDao(id, account.get().getDescription(), account.get().getBalance(),
                account.get().getAccountType(), account.get().getAccountStatusType(),
                account.get().getAccModInfId().getCreatedDate());
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

        Account acc = accountRepository.findByIdAndUserId(id, user)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));

        Timestamp closedDate = null;
        if (account.getAccountStatusType().equals(AccountStatusType.CLOSED))
            closedDate = accountModifyInformation.setDate();

        String lastModifiedBy = accountModifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(),
                user.getRoles());

        Timestamp lastModifiedDate = accountModifyInformation.setDate();

        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(acc.getAccModInfId().getId(),
                lastModifiedBy, lastModifiedDate, closedDate);

        accModifyInformationRepository.save(accountModifyInformation);

        account.setUserId(user);
        account.setId(id);
        account.setAccModInfId(accountModifyInformation);

        accountRepository.save(account);
    }


    public void updateAccountAuth(String ssn, Long id, Account account) throws BadRequestException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));

        Timestamp closedDate = null;
        if (account.getAccountStatusType().equals(AccountStatusType.CLOSED))
            closedDate = accountModifyInformation.setDate();

        String lastModifiedBy = accountModifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(),
                user.getRoles());

        Timestamp lastModifiedDate = accountModifyInformation.setDate();

        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(acc.getAccModInfId().getId(),
                lastModifiedBy, lastModifiedDate, closedDate);

        accModifyInformationRepository.save(accountModifyInformation);

        account.setUserId(acc.getUserId());
        account.setId(id);
        account.setAccModInfId(accountModifyInformation);

        accountRepository.save(account);
    }

    public void removeByAccountId(Long id) throws ResourceNotFoundException {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));

        List<Transfer> transfer = transferRepository.findAllByFromAccountId(account);

        if (!transfer.isEmpty())
            transferRepository.deleteAllByFromAccountId(account);

        else {
            accModifyInformationRepository.deleteById(account.getAccModInfId().getId());
            accountRepository.deleteById(id);
        }

    }
}
