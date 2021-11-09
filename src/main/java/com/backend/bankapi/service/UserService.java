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
        User user = userRepository.findBySsn(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        return new UserDao(ssn, user.getFirstName(), user.getLastName(), user.getEmail(), user.getAddress(),
                user.getMobilePhoneNumber(), user.getModInfId());
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

        String createdBy = user.getFirstName().toLowerCase() + "_" + user.getLastName().toLowerCase() +
                "_" + user.getRoles();
        Date date= new Date();
        long time = date.getTime();
        Timestamp createdDate = new Timestamp(time);
        Timestamp lastModifiedDate = new Timestamp(time);

        ModifyInformation modifyInformation = new ModifyInformation(createdBy, createdDate,
                createdBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        user.setModInfId(modifyInformation);

        userRepository.save(user);
    }

    public void login(String ssn, String password) throws AuthException {
        try {
            Optional<User> user = userRepository.findBySsn(ssn);

            if (!BCrypt.checkpw(password, user.get().getPassword()))
                throw new AuthException("invalid credentials");
        } catch (Exception e) {
            throw new AuthException("invalid credentials");
        }
    }

    public void updateUser(String ssn, UserDao userDao) throws BadRequestException {

        boolean emailExists = userRepository.existsByEmail(userDao.getEmail());
        Optional<User> userDetails = userRepository.findBySsn(ssn);

        if (emailExists && !userDao.getEmail().equals(userDetails.get().getEmail())){
            throw new ConflictException("Error: Email is already in use!");
        }

        String lastModifiedBy = userDao.getFirstName().toLowerCase() + "_" + userDao.getLastName().toLowerCase() +
                "_" + userDetails.get().getRoles();
        Date date= new Date();
        long time = date.getTime();
        Timestamp lastModifiedDate = new Timestamp(time);

        ModifyInformation modifyInformation = new ModifyInformation(lastModifiedBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        userRepository.update(ssn, userDao.getFirstName(), userDao.getLastName(), userDao.getEmail(),
                userDao.getAddress(), userDao.getMobilePhoneNumber(), lastModifiedBy, lastModifiedDate);
    }

    public void updateUserAuth(Long id, AdminDao adminDao) throws BadRequestException {

        boolean emailExists = userRepository.existsByEmail(adminDao.getEmail());
        Optional<User> userDetails = userRepository.findById(id);

        if (emailExists && !adminDao.getEmail().equals(userDetails.get().getEmail())){
            throw new ConflictException("Error: Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(adminDao.getPassword());

        adminDao.setPassword(encodedPassword);

        Set<String> userRoles = adminDao.getRole();
        Set<Role> roles = addRoles(userRoles);

        String lastModifiedBy = adminDao.getFirstName().toLowerCase() + "_" + adminDao.getLastName().toLowerCase() +
                "_" + userDetails.get().getRoles();
        Date date= new Date();
        long time = date.getTime();
        Timestamp lastModifiedDate = new Timestamp(time);

        ModifyInformation modifyInformation = new ModifyInformation(lastModifiedBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        User user = new User(id, adminDao.getFirstName(), adminDao.getLastName(), adminDao.getEmail(),
                adminDao.getPassword(), adminDao.getAddress(), adminDao.getMobilePhoneNumber(),
                modifyInformation, roles);

        userRepository.save(user);
    }

    public void updatePassword(String ssn, String newPassword, String oldPassword) throws BadRequestException {
        Optional<User> user = userRepository.findBySsn(ssn);
        if (!(BCrypt.hashpw(oldPassword, user.get().getPassword()).equals(user.get().getPassword())))
            throw new BadRequestException("password does not match");

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.get().setPassword(hashedPassword);
        userRepository.save(user.get());
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
                if ("Administrator".equals(role)) {
                    Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(UserRole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        return roles;
    }
}
