package com.backend.bankapi.controller;

import com.backend.bankapi.dao.AdminDao;
import com.backend.bankapi.dao.UserDao;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.security.jwt.JwtUtils;
import com.backend.bankapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@Produces(MediaType.APPLICATION_JSON)
@RequestMapping()
public class UserController {

    public UserService userService;

    public AuthenticationManager authenticationManager;

    public JwtUtils jwtUtils;

    @GetMapping("/user/auth/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<User> users = userService.fetchAllUsers(ssn);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/user/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<UserDao> getUserBySsn(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        UserDao userDao = userService.findBySsn(ssn);
        return new ResponseEntity<>(userDao, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Boolean>> registerUser(@Valid @RequestBody User user) {
        userService.register(user);

        Map<String, Boolean> map = new HashMap<>();
        map.put("User registered successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody Map<String, Object> userMap){
        String ssn = (String) userMap.get("ssn");
        String password = (String) userMap.get("password");

        userService.login(ssn, password);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(ssn, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/user/update")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateUser(HttpServletRequest request,
                                                           @Valid @RequestBody UserDao userDao) {
        String ssn = (String) request.getAttribute("ssn");
        userService.updateUser(ssn, userDao);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/user/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateUserAuth(HttpServletRequest request,
                                                               @PathVariable Long id,
                                                               @Valid @RequestBody AdminDao adminDao) {
        String adminSsn = (String) request.getAttribute("ssn");
        userService.updateUserAuth(adminSsn, id, adminDao);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PatchMapping("/user/password")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updatePassword(HttpServletRequest request,
                                                               @RequestBody Map<String, Object> userMap) {
        String ssn = (String) request.getAttribute("ssn");
        String newPassword = (String) userMap.get("newPassword");
        String oldPassword = (String) userMap.get("oldPassword");
        userService.updatePassword(ssn, newPassword, oldPassword);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> deleteUser(HttpServletRequest request, @PathVariable Long id){
        String ssn = (String) request.getAttribute("ssn");
        userService.removeById(id, ssn);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
