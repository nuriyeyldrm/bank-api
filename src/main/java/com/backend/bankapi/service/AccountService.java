package com.backend.bankapi.service;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.dao.AdminAccountDao;
import com.backend.bankapi.domain.*;
import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.domain.enumeration.UserRole;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberRepository accountNumberRepository;
    private final UserRepository userRepository;
    private final TransferRepository transferRepository;
    private final AccModifyInformationRepository accModifyInformationRepository;
    private final UserService userService;
    private final AccountModifyInformation accountModifyInformation = new AccountModifyInformation();

    private final static String ACCOUNT_NOT_FOUND_MSG = "account with id %d not found";
    private final static String ACCOUNT_NO_NOT_FOUND_MSG = "account with accountNo %d not found";
    private final static String SSN_NOT_FOUND_MSG = "account with ssn %s not found";
    private final static String USER_NOT_FOUND_MSG = "user with id %d not found";

    public List<AdminAccountDao> fetchAllAccounts(String ssn){
        User admin = userRepository.findBySsn(ssn) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER))
            return accountRepository.findAllByOrderById();

        else
            return accountRepository.findAllByRole(UserRole.ROLE_CUSTOMER);
    }

    public List<AdminAccountDao> findAllByUserId(String ssn, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));

        User admin = userRepository.findBySsn(ssn) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);
        List<UserRole> rolesUser = userService.getRoleList(user);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || rolesUser.contains(UserRole.ROLE_CUSTOMER))
            return accountRepository.findAllByUserId(user);
        else
            return accountRepository.findAllByUserIdAndRole(user, UserRole.ROLE_CUSTOMER);
    }

    public AdminAccountDao findByAccountNoAuth(String ssn, Long accountNo) throws ResourceNotFoundException {
        User admin = userRepository.findBySsn(ssn) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        AdminAccountDao account = accountRepository.findByAccountNoOrderById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        User user = userRepository.findById(account.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,
                        account.getUserId())));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);
        List<UserRole> rolesUser = userService.getRoleList(user);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || rolesUser.contains(UserRole.ROLE_CUSTOMER))
            return account;

        else
            throw new BadRequestException(String.format("You dont have permission to access account " +
                    "with accountNo %d", accountNo));
    }

    public List<AccountDao> findAllBySsn(String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        return accountRepository.findAllByUserIdOrderById(user);
    }

    public AccountDao findBySsnAccountNo(Long accountNo, String ssn) throws ResourceNotFoundException {
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        return accountRepository.findByAccountNoAndUserIdOrderById(accountNumber, user).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));
    }

    public Long add(String ssn, Account account) throws BadRequestException {
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

        AccountNumber accountNumber = new AccountNumber();
        accountNumberRepository.save(accountNumber);
        account.setAccountNo(accountNumber);

        accountRepository.save(account);

        return account.getId();
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
        account.setAccountNo(acc.getAccountNo());

        accountRepository.save(account);
    }


    public void updateAccountAuth(String ssn, Long id, Account account) throws BadRequestException {
        User admin = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG, id)));

        User user = userRepository.findById(acc.getUserId().getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,
                        acc.getUserId().getId())));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);
        List<UserRole> rolesUser = userService.getRoleList(user);

        Timestamp closedDate = null;
        if (account.getAccountStatusType().equals(AccountStatusType.CLOSED))
            closedDate = accountModifyInformation.setDate();

        String lastModifiedBy = accountModifyInformation.setModifiedBy(admin.getFirstName(), admin.getLastName(),
                admin.getRoles());

        Timestamp lastModifiedDate = accountModifyInformation.setDate();

        AccountModifyInformation accountModifyInformation = new AccountModifyInformation(acc.getAccModInfId().getId(),
                lastModifiedBy, lastModifiedDate, closedDate);

        accModifyInformationRepository.save(accountModifyInformation);

        account.setUserId(acc.getUserId());
        account.setId(id);
        account.setAccModInfId(accountModifyInformation);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || rolesUser.contains(UserRole.ROLE_CUSTOMER))
            accountRepository.save(account);
        else
            throw new BadRequestException(String.format("You dont have permission to update account with id %d", id));
    }

    public void removeByAccountIdAuth(String ssn, Long accountNo) throws ResourceNotFoundException {
        User admin = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        Account account = accountRepository.findByAccountNo(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        User user = userRepository.findById(account.getUserId().getId()).orElseThrow(() ->
                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, account.getUserId().getId())));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);
        List<UserRole> rolesUser = userService.getRoleList(user);

        List<Transfer> transfer = transferRepository.findAllByFromAccountId(account);
        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || (rolesAdmin.contains(UserRole.ROLE_EMPLOYEE) &&
                (rolesUser.contains(UserRole.ROLE_CUSTOMER)))) {

            if (!transfer.isEmpty())
                transferRepository.deleteAllByFromAccountId(account);

            else {
                accModifyInformationRepository.deleteById(account.getAccModInfId().getId());
                accountRepository.deleteById(account.getId());
            }
        }
        else
            throw new BadRequestException("You don't have permission to delete account!");
    }


    public void removeByAccountId(String ssn, Long accountNo) throws BadRequestException {
        User user = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        AccountNumber accountNumber = accountNumberRepository.findById(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        Account account = accountRepository.findByAccountNoAndUserId(accountNumber, user)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NO_NOT_FOUND_MSG, accountNo)));

        List<Transfer> transfer = transferRepository.findAllByFromAccountId(account);

        if (!transfer.isEmpty())
            throw new BadRequestException("You cannot delete account because of existing of transfer!");

        else {
            accModifyInformationRepository.deleteById(account.getAccModInfId().getId());
            accountRepository.deleteById(account.getId());
        }
    }
}
