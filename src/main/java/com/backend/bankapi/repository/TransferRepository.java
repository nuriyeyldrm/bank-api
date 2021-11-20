package com.backend.bankapi.repository;

import com.backend.bankapi.dao.ProjectTransfer;
import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByUserId(User id) throws ResourceNotFoundException;

    Optional<Transfer> findByIdAndUserId(Long id, User userId) throws ResourceNotFoundException;

    List<ProjectTransfer> findAllByUserId(User id) throws ResourceNotFoundException;
}
