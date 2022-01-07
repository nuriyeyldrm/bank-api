package com.backend.bankapi.service;

import com.backend.bankapi.dao.AdminDao;
import com.backend.bankapi.dao.UserDao;
import com.backend.bankapi.domain.ModifyInformation;
import com.backend.bankapi.domain.Role;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.domain.enumeration.UserRole;
import com.backend.bankapi.exception.AuthException;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ConflictException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.repository.ModifyInformationRepository;
import com.backend.bankapi.repository.RoleRepository;
import com.backend.bankapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ModifyInformationRepository modifyInfRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModifyInformation modifyInformation = new ModifyInformation();

    private final static String USER_NOT_FOUND_MSG = "user with id %d not found";

    private final static String SSN_NOT_FOUND_MSG = "user with ssn %s not found";

    public List<User> fetchAllUsers(){
        return userRepository.findAll();
    }

    public User findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));
    }

    public UserDao findBySsn(String ssn) throws ResourceNotFoundException {
        return  userRepository.findBySsnOrderById(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));
    }

    public void register(User user) throws BadRequestException {
        if (userRepository.existsBySsn(user.getSsn())) {
            throw new ConflictException("Error: SSN is already in use!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Error: Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        Set<Role> roles = new HashSet<>();
        Role customerRole = roleRepository.findByName(UserRole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
        roles.add(customerRole);

        user.setRoles(roles);

        String createdBy = modifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(), user.getRoles());

        Timestamp createdDate = modifyInformation.setDate();

        ModifyInformation modifyInformation = new ModifyInformation(createdBy, createdDate,
                createdBy, createdDate);

        modifyInfRepository.save(modifyInformation);

        user.setModInfId(modifyInformation);

        userRepository.save(user);
    }

    public void login(String ssn, String password) throws AuthException {
        try {
            User user = userRepository.findBySsn(ssn) .orElseThrow(() ->
                    new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

            if (!BCrypt.checkpw(password, user.getPassword()))
                throw new AuthException("invalid credentials");
        } catch (Exception e) {
            throw new AuthException("invalid credentials");
        }
    }

    public void updateUser(String ssn, UserDao userDao) throws BadRequestException {

        boolean emailExists = userRepository.existsByEmail(userDao.getEmail());
        User userDetails = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        if (emailExists && !userDao.getEmail().equals(userDetails.getEmail())){
            throw new ConflictException("Error: Email is already in use!");
        }

        String lastModifiedBy = modifyInformation.setModifiedBy(userDao.getFirstName(), userDao.getLastName(),
                userDetails.getRoles());

        Timestamp lastModifiedDate = modifyInformation.setDate();

        ModifyInformation modifyInformation = new ModifyInformation(userDetails.getModInfId().getId(),
                lastModifiedBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        userRepository.update(ssn, userDao.getFirstName(), userDao.getLastName(), userDao.getEmail(),
                userDao.getAddress(), userDao.getMobilePhoneNumber());
    }

    public void updateUserAuth(String adminSsn, Long id, AdminDao adminDao) throws BadRequestException {

        boolean emailExists = userRepository.existsByEmail(adminDao.getEmail());

        User userDetails = userRepository.findById(id) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        User adminDetails = userRepository.findBySsn(adminSsn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, adminSsn)));

        if (emailExists && !adminDao.getEmail().equals(userDetails.getEmail())){
            throw new ConflictException("Error: Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(adminDao.getPassword());

        adminDao.setPassword(encodedPassword);

        Set<String> userRoles = adminDao.getRole();
        Set<Role> roles = addRoles(userRoles);

        String lastModifiedBy = modifyInformation.setModifiedBy(adminDetails.getFirstName(),
                adminDetails.getLastName(), adminDetails.getRoles());

        Timestamp lastModifiedDate = modifyInformation.setDate();

        ModifyInformation modifyInformation = new ModifyInformation(userDetails.getModInfId().getId(),
                lastModifiedBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        User user = new User(id, userDetails.getSsn(), adminDao.getFirstName(), adminDao.getLastName(),
                adminDao.getEmail(), adminDao.getPassword(), adminDao.getAddress(), adminDao.getMobilePhoneNumber(),
                userDetails.getModInfId(), roles);

        userRepository.save(user);
    }

    public void updatePassword(String ssn, String newPassword, String oldPassword) throws BadRequestException {
        User user = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        if (!(BCrypt.hashpw(oldPassword, user.getPassword()).equals(user.getPassword())))
            throw new BadRequestException("password does not match");

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public void removeById(Long id) throws ResourceNotFoundException {
        boolean userExists = userRepository.existsById(id);

        if (!userExists){
            throw new ResourceNotFoundException("user does not exist");
        }

        userRepository.deleteById(id);
    }

    public Set<Role> addRoles(Set<String> userRoles) {
        Set<Role> roles = new HashSet<>();

        if (userRoles == null) {
            Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            userRoles.forEach(role -> {
                switch (role) {
                    case "Manager":
                        Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "Employee":
                        Role customerServiceRole = roleRepository.findByName(UserRole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(customerServiceRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        return roles;
    }
}
