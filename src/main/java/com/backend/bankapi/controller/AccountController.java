package com.backend.bankapi.controller;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.domain.Account;
import com.backend.bankapi.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/account")
public class AccountController {

    public AccountService accountService;

    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Account>> getAllAccounts(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<Account> accounts = accountService.fetchAllAccounts(ssn);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Account>> getAccountsByUserId(HttpServletRequest request, @PathVariable Long userId){
        String ssn = (String) request.getAttribute("ssn");
        List<Account> account = accountService.findAllByUserId(ssn, userId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Account> getAccountById(HttpServletRequest request, @PathVariable Long id){
        String ssn = (String) request.getAttribute("ssn");
        Account account = accountService.findByIdAuth(ssn, id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<AccountDao>> getAccountsBySsn(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<AccountDao> account = accountService.findAllBySsn(ssn);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/{id}/user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<AccountDao> getAccountBySsnId(@PathVariable Long id,
                                                        HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        AccountDao account = accountService.findBySsnId(id, ssn);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> createAccount(HttpServletRequest request,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.add(ssn, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Account created successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateAccount(HttpServletRequest request,
                                                              @PathVariable Long id,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.updateAccount(ssn, id, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateAuthAccount(HttpServletRequest request,
                                                              @PathVariable Long id,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.updateAccountAuth(ssn, id, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> deleteAccount(HttpServletRequest request, @PathVariable Long id){
        String ssn = (String) request.getAttribute("ssn");
        accountService.removeByAccountId(ssn, id);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
