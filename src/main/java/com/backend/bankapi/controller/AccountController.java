package com.backend.bankapi.controller;

import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@Produces(MediaType.APPLICATION_JSON)
@RequestMapping("/account")
public class AccountController {

    public AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Boolean>> createAccount(HttpServletRequest request,
                                                              @Valid @RequestBody Account account) {
        String ssn = (String) request.getAttribute("ssn");
        accountService.add(ssn, account);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Account created successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}
