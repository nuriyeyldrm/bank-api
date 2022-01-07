package com.backend.bankapi.repository;

import com.backend.bankapi.domain.Account;
import com.backend.bankapi.projection.ProjectTransferAdmin;
import com.backend.bankapi.projection.ProjectTransfer;
import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<ProjectTransferAdmin> findByIdOrderById(Long id) throws ResourceNotFoundException;

    Optional<ProjectTransfer> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    List<ProjectTransferAdmin> findAllBy() throws ResourceNotFoundException;

    List<ProjectTransferAdmin> findAllByUserIdOrderById(User id) throws ResourceNotFoundException;

    List<ProjectTransfer> findAllByUserId(User id) throws ResourceNotFoundException;

    List<Transfer> findAllByFromAccountId(Account id) throws ResourceNotFoundException;

    void deleteAllByFromAccountId(Account id) throws ResourceNotFoundException;
}
