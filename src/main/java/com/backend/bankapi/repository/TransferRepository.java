package com.backend.bankapi.repository;

import com.backend.bankapi.dao.TransferAdminDao;
import com.backend.bankapi.dao.TransferUserDao;
import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.enumeration.UserRole;
import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<TransferAdminDao> findByIdOrderById(Long id) throws ResourceNotFoundException;

    Optional<TransferUserDao> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    List<TransferAdminDao> findAllBy() throws ResourceNotFoundException;

    List<TransferAdminDao> findAllByUserIdOrderById(User id) throws ResourceNotFoundException;

    @Query("SELECT t from Transfer t " +
            "LEFT JOIN FETCH t.userId u " +
            "LEFT JOIN FETCH u.roles r " +
            "WHERE t.userId = ?1 and r.name = ?2")
    List<TransferAdminDao> findAllByUserIdAndRole(User user, UserRole userRole) throws ResourceNotFoundException;

    @Query("SELECT t from Transfer t " +
            "LEFT JOIN FETCH t.userId u " +
            "LEFT JOIN FETCH u.roles r " +
            "WHERE r.name = ?1")
    List<TransferAdminDao> findAllByRole(UserRole userRole);

    List<TransferUserDao> findAllByUserId(User id) throws ResourceNotFoundException;

    List<TransferUserDao> findAllByUserIdAndFromAccountId(User user, Account account);

    List<Transfer> findAllByFromAccountId(Account id) throws ResourceNotFoundException;

//    void deleteAllByFromAccountId(Account id) throws ResourceNotFoundException;
}
