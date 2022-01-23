package com.backend.bankapi.repository;

import com.backend.bankapi.domain.AccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface AccountNumberRepository extends JpaRepository<AccountNumber, Long> {
}
