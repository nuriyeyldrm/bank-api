package com.backend.bankapi.repository;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserId(User id) throws ResourceNotFoundException;

    Optional<Account> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    @Transactional
    @Query("SELECT new com.backend.bankapi.dao.AccountDao(a.id, a.description, a.balance, a.accountType, " +
            "a.accountStatusType, a.accModInfId) FROM Account a, AccountModifyInformation ai " +
            "WHERE a.userId = ?1 and ai.id = a.accModInfId.id")
    List<AccountDao> findAllByyUserId(User id) throws ResourceNotFoundException;
}