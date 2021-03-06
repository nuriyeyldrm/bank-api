package com.backend.bankapi.repository;

import com.backend.bankapi.domain.ModifyInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModifyInformationRepository extends JpaRepository<ModifyInformation, Long> {
}
