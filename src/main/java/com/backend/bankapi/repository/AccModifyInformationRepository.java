package com.backend.bankapi.repository;

import com.backend.bankapi.domain.AccountModifyInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccModifyInformationRepository extends JpaRepository<AccountModifyInformation, Long> {
}
