package com.backend.bankapi.service;

import com.backend.bankapi.dao.*;
import com.backend.bankapi.domain.ModifyInformation;
import com.backend.bankapi.domain.Role;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.domain.enumeration.PagingHeaders;
import com.backend.bankapi.domain.enumeration.UserRole;
import com.backend.bankapi.exception.AuthException;
import com.backend.bankapi.exception.BadRequestException;
import com.backend.bankapi.exception.ConflictException;
import com.backend.bankapi.exception.ResourceNotFoundException;
import com.backend.bankapi.projection.ProjectAdmin;
import com.backend.bankapi.projection.ProjectUser;
import com.backend.bankapi.repository.ModifyInformationRepository;
import com.backend.bankapi.repository.RoleRepository;
import com.backend.bankapi.repository.UserPageableRepository;
import com.backend.bankapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPageableRepository userPageableRepository;
    private final RoleRepository roleRepository;
    private final ModifyInformationRepository modifyInfRepository;
    private final PasswordEncoder passwordEncoder;

    private final ModifyInformation modifyInformation = new ModifyInformation();

    private final static String USER_NOT_FOUND_MSG = "user with id %d not found";
    private final static String SSN_NOT_FOUND_MSG = "user with ssn %s not found";

    public List<ProjectAdmin> fetchAllUsers(String ssn){
        User admin = userRepository.findBySsn(ssn) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        List<UserRole> rolesAdmin = getRoleList(admin);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER))
            return userRepository.findAllBy();
        else
            return userRepository.findAllByRole(UserRole.ROLE_CUSTOMER);
    }

    public ProjectAdmin findById(String ssn, Long id) throws ResourceNotFoundException {
        User admin = userRepository.findBySsn(ssn) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        ProjectAdmin projectUser = userRepository.findByIdOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        List<UserRole> rolesAdmin = getRoleList(admin);
        List<UserRole> rolesUser = getRoleList(user);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || rolesUser.contains(UserRole.ROLE_CUSTOMER))
            return projectUser;

        else
            throw new BadRequestException(String.format("You dont have permission to access user with id %d", id));
    }

    public ProjectUser findBySsn(String ssn) throws ResourceNotFoundException {
        return  userRepository.findBySsnOrderById(ssn)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));
    }

    public PagingResponse get(Specification<User> spec, Pageable pageable) {
        Page<User> page = userPageableRepository.findAll(spec, pageable);
        List<User> content = page.getContent();
        return new PagingResponse(page.getTotalElements(), (long) page.getNumber(),
                (long) page.getNumberOfElements(), pageable.getOffset(), (long) page.getTotalPages(), content);
    }

    public List<User> get(Specification<User> spec, Sort sort) {
        return userPageableRepository.findAll(spec, sort);
    }

    public PagingResponse get(Specification<User> spec, HttpHeaders headers, Sort sort) {
        if (isRequestPaged(headers)) {
            return get(spec, buildPageRequest(headers, sort));
        } else {
            List<User> entities = get(spec, sort);
            return new PagingResponse((long) entities.size(), 0L,
                    0L, 0L, 0L, entities);
        }
    }

    public PagingResponseAdmin searchAll(PagingResponse pagingResponse) {
        List<User> elements = pagingResponse.getElements();

        List<SearchDao> search = new ArrayList<>();

        for (User u : elements){
            search.add(new SearchDao(u));
        }

        return new PagingResponseAdmin(pagingResponse.getCount(),
                pagingResponse.getPageNumber(), pagingResponse.getPageSize(), pagingResponse.getPageOffset(),
                pagingResponse.getPageTotal(), search);
    }

    private boolean isRequestPaged(HttpHeaders headers) {
        return headers.containsKey(PagingHeaders.PAGE_NUMBER.getName()) &&
                headers.containsKey(PagingHeaders.PAGE_SIZE.getName());
    }

    private Pageable buildPageRequest(HttpHeaders headers, Sort sort) {
        int page = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_NUMBER.getName())).get(0));
        int size = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_SIZE.getName())).get(0));
        return PageRequest.of(page, size, sort);
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
        Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
        roles.add(customerRole);

        user.setRoles(roles);

        String createdBy = modifyInformation.setModifiedBy(user.getFirstName(), user.getLastName(), user.getRole());

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

        if (userDetails.getBuildIn()){
            throw new ConflictException("Error: You dont have permission to update user!");
        }

        String lastModifiedBy = modifyInformation.setModifiedBy(userDao.getFirstName(), userDao.getLastName(),
                userDetails.getRole());

        Timestamp lastModifiedDate = modifyInformation.setDate();

        ModifyInformation modifyInformation = new ModifyInformation(userDetails.getModInfId().getId(),
                lastModifiedBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        userRepository.update(ssn, userDao.getFirstName(), userDao.getLastName(), userDao.getEmail(),
                userDao.getAddress(), userDao.getMobilePhoneNumber());
    }

    public void updateUserAuth(String adminSsn, Long id, AdminDao adminDao) throws BadRequestException {

        boolean emailExists = userRepository.existsByEmail(adminDao.getEmail());
        boolean ssnExists = userRepository.existsBySsn(adminDao.getSsn());

        User userDetails = userRepository.findById(id) .orElseThrow(() ->
                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        User adminDetails = userRepository.findBySsn(adminSsn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, adminSsn)));

        if (emailExists && !adminDao.getEmail().equals(userDetails.getEmail())){
            throw new ConflictException("Error: Email is already in use!");
        }

        if (ssnExists && !adminDao.getSsn().equals(userDetails.getSsn())){
            throw new ConflictException("Error: Ssn is already in use!");
        }

        if (userDetails.getBuildIn()){
            throw new ConflictException("Error: You dont have permission to update user!");
        }

        List<UserRole> rolesAdmin = getRoleList(adminDetails);
        List<UserRole> rolesUser = getRoleList(userDetails);

        if (adminDao.getPassword() == null) {
            adminDao.setPassword(userDetails.getPassword());
        }

        else {
            String encodedPassword = passwordEncoder.encode(adminDao.getPassword());
            adminDao.setPassword(encodedPassword);
        }

        Set<String> userRoles = adminDao.getRoles();
        Set<Role> roles = addRoles(userRoles);

        String lastModifiedBy = modifyInformation.setModifiedBy(adminDetails.getFirstName(),
                adminDetails.getLastName(), adminDetails.getRole());

        Timestamp lastModifiedDate = modifyInformation.setDate();

        ModifyInformation modifyInformation = new ModifyInformation(userDetails.getModInfId().getId(),
                lastModifiedBy, lastModifiedDate);

        modifyInfRepository.save(modifyInformation);

        User user;

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER)) {
            user = new User(id, adminDao.getSsn(), adminDao.getFirstName(), adminDao.getLastName(),
                    adminDao.getEmail(), adminDao.getPassword(), adminDao.getAddress(), adminDao.getMobilePhoneNumber(),
                    userDetails.getModInfId(), roles);
        }
        else if (rolesUser.contains(UserRole.ROLE_CUSTOMER)){
            user = new User(id, adminDao.getSsn(), adminDao.getFirstName(), adminDao.getLastName(),
                    adminDao.getEmail(), adminDao.getPassword(), adminDao.getAddress(), adminDao.getMobilePhoneNumber(),
                    userDetails.getModInfId(), userDetails.getRole());
        }
        else
            throw new BadRequestException(String.format("You dont have permission to update user with id %d", id));

        userRepository.save(user);
    }

    public void updatePassword(String ssn, String newPassword, String oldPassword) throws BadRequestException {
        User user = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        if (!(BCrypt.hashpw(oldPassword, user.getPassword()).equals(user.getPassword())))
            throw new BadRequestException("password does not match");

        if (user.getBuildIn()){
            throw new ConflictException("Error: You dont have permission to update password!");
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public void removeById(Long id, String ssn) throws BadRequestException {
        User admin = userRepository.findBySsn(ssn).orElseThrow(() ->
                new ResourceNotFoundException(String.format(SSN_NOT_FOUND_MSG, ssn)));

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        if (user.getBuildIn()){
            throw new ConflictException("Error: You dont have permission to delete user!");
        }

        List<UserRole> rolesAdmin = getRoleList(admin);
        List<UserRole> rolesUser = getRoleList(user);

        if (rolesAdmin.contains(UserRole.ROLE_MANAGER) || (rolesAdmin.contains(UserRole.ROLE_EMPLOYEE) &&
                (rolesUser.contains(UserRole.ROLE_CUSTOMER))))
            userRepository.deleteById(id);

        else
            throw new BadRequestException("You don't have permission to delete user!");
    }

    public List<UserRole> getRoleList(User user) {
        List<UserRole> roles = new ArrayList<>();
        for (Role role: user.getRole()){
            roles.add(role.getName());
        }
        return roles;
    }

    public Set<Role> addRoles(Set<String> userRoles) {
        Set<Role> roles = new HashSet<>();

        if (userRoles == null) {
            Role userRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            userRoles.forEach(role -> {
                switch (role) {
                    case "Manager":
                        Role adminRole = roleRepository.findByName(UserRole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "Employee":
                        Role customerServiceRole = roleRepository.findByName(UserRole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(customerServiceRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        return roles;
    }
}
