package com.backend.bankapi.repository;

import com.backend.bankapi.domain.Account;
import com.backend.bankapi.projection.ProjectTransferAdmin;
import com.backend.bankapi.projection.ProjectTransfer;
import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<ProjectTransferAdmin> findByIdAndId(Long id, Long ids) throws ResourceNotFoundException;

    Optional<ProjectTransfer> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    List<ProjectTransferAdmin> findAllBy() throws ResourceNotFoundException;

    List<ProjectTransfer> findAllByUserId(User id) throws ResourceNotFoundException;

    void deleteByFromAccountId(Account id) throws ResourceNotFoundException;
}
