package com.backend.bankapi.repository;

import com.backend.bankapi.dao.UserDao;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.domain.enumeration.UserRole;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ConflictException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.projection.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<ProjectUser> findBySsnOrderById(String ssn) throws ResourceNotFoundException;

    Optional<User> findBySsn(String ssn) throws ResourceNotFoundException;

    @Query("SELECT u from User u LEFT JOIN FETCH u.roles r WHERE r.name = ?1")
    List<User> findAllByRole(UserRole userRole);

    Boolean existsBySsn(String ssn) throws ConflictException;

    Boolean existsByEmail(String email) throws ConflictException;

    @Modifying
    @Query("UPDATE User u " +
            "SET u.firstName = ?2, u.lastName = ?3, u.email = ?4, u.address = ?5, u.mobilePhoneNumber = ?6 " +
            "WHERE u.ssn = ?1")
    void update(String ssn, String firstName, String lastName, String email, String address,
                String mobilePhoneNumber) throws BadRequestException;

}
