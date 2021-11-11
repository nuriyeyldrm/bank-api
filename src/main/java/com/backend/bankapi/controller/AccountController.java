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

    @GetMapping("/admin/auth/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts(){
        List<Account> accounts = accountService.fetchAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/admin/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> getAccountByIdAuth(@PathVariable Long id){
        Account account = accountService.findByIdAuth(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/user/auth")
    public ResponseEntity<AccountDao> getAccountsBySsn(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        AccountDao account = accountService.findAllBySsn(ssn);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/user/{id}/auth")
    public ResponseEntity<AccountDao> getAccountBySsnId(@PathVariable Long id,
                                                        HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        AccountDao account = accountService.findBySsnId(id, ssn);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Boolean>> createAccount(HttpServletRequest request,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.add(ssn, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Account created successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PutMapping("/user/{id}/auth")
    public ResponseEntity<Map<String, Boolean>> updateAccount(HttpServletRequest request,
                                                              @PathVariable Long id,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.updateAccount(ssn, id, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
