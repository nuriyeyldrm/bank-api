package com.backend.bankapi.controller;

import com.backend.bankapi.projection.ProjectTransferAdmin;
import com.backend.bankapi.projection.ProjectTransfer;
import com.backend.bankapi.dao.TransferDao;
import com.backend.bankapi.service.TransferService;
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
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RestController
@Produces(MediaType.APPLICATION_JSON)
@RequestMapping("/transfer")
public class TransferController {

    public TransferService transferService;

    @GetMapping("/auth/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<ProjectTransferAdmin>> getAllTransfers(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<ProjectTransferAdmin> transfers = transferService.fetchAllTransfers(ssn);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<ProjectTransferAdmin>> getTransfersByUserId(HttpServletRequest request,
                                                                           @PathVariable Long userId){
        String ssn = (String) request.getAttribute("ssn");
        List<ProjectTransferAdmin> transfer = transferService.findByUserId(ssn, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<ProjectTransferAdmin> getTransferById(HttpServletRequest request,
                                                                @PathVariable Long id){
        String ssn = (String) request.getAttribute("ssn");
        ProjectTransferAdmin transfer = transferService.findByIdAuth(ssn, id);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<ProjectTransfer>> getTransfersBySsn(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<ProjectTransfer> transfers = transferService.findAllBySsn(ssn);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Optional<ProjectTransfer>> getTransferBySsnId(@PathVariable Long id,
                                                        HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        Optional<ProjectTransfer> transfer = transferService.findBySsnId(id, ssn);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createTransfer(HttpServletRequest request,
                                                              @Valid @RequestBody TransferDao transfer) {
        String ssn = (String) request.getAttribute("ssn");
        Double currentBalance = transferService.create(ssn, transfer);

        Map<String, Object> map = new HashMap<>();
        map.put("Account created successfully!", true);
        map.put("currentBalance", currentBalance);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}
