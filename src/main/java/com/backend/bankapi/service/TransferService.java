package com.backend.bankapi.service;

import com.backend.bankapi.domain.enumeration.UserRole;
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
    private final UserService userService;

    private final static String SSN_NOT_FOUND_MSG = "user with ssn %s not found";
    private final static String USER_NOT_FOUND_MSG = "user with id %d not found";
    private final static String ACCOUNT_NOT_FOUND_MSG = "account with id %s not found";
    private final static String TRANSFER_NOT_FOUND_MSG = "transfer with id %d not found";

    public List<ProjectTransferAdmin> fetchAllTransfers(String ssn){
        User admin = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER))
            return transferRepository.findAllBy();
        else
            return transferRepository.findAllByRole(UserRole.ROLE_CUSTOMER);
    }

    public List<ProjectTransferAdmin> findByUserId(String ssn, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));

        User admin = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);
        List<UserRole> rolesUser = userService.getRoleList(user);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || rolesUser.contains(UserRole.ROLE_CUSTOMER))
            return transferRepository.findAllByUserIdOrderById(user);
        else
            return transferRepository.findAllByUserIdAndRole(user, UserRole.ROLE_CUSTOMER);
    }

    public ProjectTransferAdmin findByIdAuth(String ssn, Long id) throws ResourceNotFoundException {
        User admin = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        ProjectTransferAdmin transfer = transferRepository.findByIdOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(TRANSFER_NOT_FOUND_MSG, id)));

        User user = userRepository.findById(transfer.getUserId().getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG,
                        transfer.getUserId().getId())));

        List<UserRole> rolesAdmin = userService.getRoleList(admin);
        List<UserRole> rolesUser = userService.getRoleList(user);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || rolesUser.contains(UserRole.ROLE_CUSTOMER))
            return transfer;
        else
            throw new BadRequestException(String.format("You dont have permission to access transfer with id %d", id));
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

        Account fromAccount = accountRepository.findByAccountNoAndUserId(transfer.getFromAccountId(), user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ACCOUNT_NOT_FOUND_MSG, transfer.getFromAccountId())));

        Account toAccount = accountRepository.findByAccountNo(transfer.getToAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ACCOUNT_NOT_FOUND_MSG,
                        transfer.getToAccountId().getId())));

        if (transfer.getFromAccountId().equals(transfer.getToAccountId())) {
            throw new BadRequestException("Money transfers cannot be made with the same accounts!");
        }

        if (!transfer.getCurrencyCode().equals(fromAccount.getCurrencyCode())) {
            throw new BadRequestException("Currency does not match with your account!");
        }

        if (!toAccount.getCurrencyCode().equals(fromAccount.getCurrencyCode())) {
            throw new BadRequestException("Currency does not match!");
        }

        if (transfer.getTransactionAmount() > fromAccount.getBalance()){
            throw new BadRequestException("not enough funds available for transfer");
        }

        final Double newFromBalance = fromAccount.getBalance() - transfer.getTransactionAmount();

        final Double newToBalance = toAccount.getBalance() + transfer.getTransactionAmount();


        Date date= new Date();
        long time = date.getTime();
        Timestamp transactionDate = new Timestamp(time);

        Transfer transfer1 = new Transfer(fromAccount, transfer.getToAccountId().getId(), user,
                transfer.getTransactionAmount(), newFromBalance, transfer.getCurrencyCode(),
                transactionDate, transfer.getDescription());

        fromAccount.setBalance(newFromBalance);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transferRepository.save(transfer1);
    }
}
