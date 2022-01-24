package com.backend.bankapi.repository;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.dao.AdminAccountDao;
import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.AccountNumber;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.domain.enumeration.UserRole;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNoAndUserId(AccountNumber accountNo, User userId) throws ResourceNotFoundException;

    Optional<Account> findByAccountNo(AccountNumber accountNo) throws ResourceNotFoundException;

    Optional<AdminAccountDao> findByAccountNoOrderById(AccountNumber accountNo) throws ResourceNotFoundException;

    Optional<AccountDao> findByAccountNoAndUserIdOrderById(AccountNumber accountNo, User user) throws ResourceNotFoundException;

    List<AdminAccountDao> findAllByOrderById();

    List<AdminAccountDao> findAllByUserId(User user) throws ResourceNotFoundException;

//    @Query("SELECT a from Account a " +
//            "LEFT JOIN FETCH a.userId u " +
//            "LEFT JOIN FETCH u.roles r " +
//            "WHERE a.userId = ?1 and r.name = ?2")
//    List<AdminAccountDao> findAllByUserIdAndRole(User user, UserRole userRole) throws ResourceNotFoundException;

    @Query("SELECT a from Account a " +
            "LEFT JOIN FETCH a.userId u " +
            "LEFT JOIN FETCH u.roles r " +
            "WHERE r.name = ?1")
    List<AdminAccountDao> findAllByRole(UserRole userRole);

    List<AccountDao> findAllByUserIdAndAccountStatusType(User user, AccountStatusType accountStatusType)
            throws ResourceNotFoundException;
}