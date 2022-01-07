package com.backend.bankapi.repository;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    Optional<AccountDao> findByIdAndUserIdOrderById(Long id, User user) throws ResourceNotFoundException;

    List<Account> findAllByUserId(User user) throws ResourceNotFoundException;

    List<AccountDao> findAllByUserIdOrderById(User user) throws ResourceNotFoundException;
}