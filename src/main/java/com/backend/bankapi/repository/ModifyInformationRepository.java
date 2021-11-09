package com.backend.bankapi.repository;

import com.backend.bankapi.domain.ModifyInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModifyInformationRepository extends JpaRepository<ModifyInformation, Long>  {
}
