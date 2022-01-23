package com.backend.bankapi.controller;

import com.backend.bankapi.dao.AccountDao;
import com.backend.bankapi.dao.AdminAccountDao;
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
    public ResponseEntity<List<AdminAccountDao>> getAllAccounts(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<AdminAccountDao> accounts = accountService.fetchAllAccounts(ssn);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<AdminAccountDao>> getAccountsByUserId(HttpServletRequest request, @PathVariable Long userId){
        String ssn = (String) request.getAttribute("ssn");
        List<AdminAccountDao> account = accountService.findAllByUserId(ssn, userId);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/{accountNo}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<AdminAccountDao> getAccountByAccountNo(HttpServletRequest request, @PathVariable Long accountNo){
        String ssn = (String) request.getAttribute("ssn");
        AdminAccountDao account = accountService.findByAccountNoAuth(ssn, accountNo);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<AccountDao>> getAccountsBySsn(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<AccountDao> account = accountService.findAllBySsn(ssn);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/{accountNo}/user")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<AccountDao> getAccountBySsnAccountNo(@PathVariable Long accountNo,
                                                        HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        AccountDao account = accountService.findBySsnAccountNo(accountNo, ssn);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createAccount(HttpServletRequest request,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        Long accId = accountService.add(ssn, account);

        Map<String, Object> map = new HashMap<>();
        map.put("Account created successfully!", true);
        map.put("AccountId", accId);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/create")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createAccountAuth(HttpServletRequest request,
                                                                 @PathVariable Long userId,
                                                             @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        Long accId = accountService.addAuth(ssn, userId, account);

        Map<String, Object> map = new HashMap<>();
        map.put("Account created successfully!", true);
        map.put("AccountId", accId);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PutMapping("/{accountNo}/update")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateAccount(HttpServletRequest request,
                                                              @PathVariable Long accountNo,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.updateAccount(ssn, accountNo, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("/{accountNo}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> updateAuthAccount(HttpServletRequest request,
                                                              @PathVariable Long accountNo,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.updateAccountAuth(ssn, accountNo, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/{accountNo}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> deleteAccountAuth(HttpServletRequest request, @PathVariable Long accountNo){
        String ssn = (String) request.getAttribute("ssn");
        accountService.removeByAccountIdAuth(ssn, accountNo);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/{accountNo}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Boolean>> deleteAccount(HttpServletRequest request, @PathVariable Long accountNo){
        String ssn = (String) request.getAttribute("ssn");
        accountService.removeByAccountId(ssn, accountNo);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
