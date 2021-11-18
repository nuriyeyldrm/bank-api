package com.backend.bankapi.controller;

import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.service.TransferService;
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
@RequestMapping("/transfer")
public class TransferController {

    public TransferService transferService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Boolean>> createTransfer(HttpServletRequest request,
                                                              @Valid @RequestBody Transfer transfer) {
        String ssn = (String) request.getAttribute("ssn");
        transferService.create(ssn, transfer);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Account created successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}
