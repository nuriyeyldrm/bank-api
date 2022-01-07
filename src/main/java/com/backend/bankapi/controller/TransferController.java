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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectTransferAdmin>> getAllTransfers(){
        List<ProjectTransferAdmin> transfers = transferService.fetchAllTransfers();
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectTransferAdmin>> getTransfersByUserId(@PathVariable Long userId){
        List<ProjectTransferAdmin> transfer = transferService.findByUserId(userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/{id}/auth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectTransferAdmin> getTransferById(@PathVariable Long id){
        ProjectTransferAdmin transfer = transferService.findByIdAuth(id);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProjectTransfer>> getTransfersBySsn(HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        List<ProjectTransfer> transfers = transferService.findAllBySsn(ssn);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Optional<ProjectTransfer>> getTransferBySsnId(@PathVariable Long id,
                                                        HttpServletRequest request){
        String ssn = (String) request.getAttribute("ssn");
        Optional<ProjectTransfer> transfer = transferService.findBySsnId(id, ssn);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> createTransfer(HttpServletRequest request,
                                                              @Valid @RequestBody TransferDao transfer) {
        String ssn = (String) request.getAttribute("ssn");
        transferService.create(ssn, transfer);

        Map<String, Boolean> map = new HashMap<>();
        map.put("Account created successfully!", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }
}
