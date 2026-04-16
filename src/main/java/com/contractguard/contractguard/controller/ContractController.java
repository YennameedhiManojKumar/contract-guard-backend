package com.contractguard.contractguard.controller;

import com.contractguard.contractguard.dto.request.ContractRequest;
import com.contractguard.contractguard.dto.response.*;
import com.contractguard.contractguard.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // Create contract
    @PostMapping
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody ContractRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
            contractService.createContract(request, userDetails.getUsername()));
    }

    // Get my contracts
    @GetMapping
    public ResponseEntity<List<ContractResponse>> getMyContracts(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
            contractService.getMyContracts(userDetails.getUsername()));
    }

    // Get single contract
    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContract(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
            contractService.getContractById(id, userDetails.getUsername()));
    }

    // Complete a milestone
    @PatchMapping("/milestones/{milestoneId}/complete")
    public ResponseEntity<MilestoneResponse> completeMilestone(
            @PathVariable Long milestoneId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
            contractService.completeMilestone(
                milestoneId, userDetails.getUsername()));
    }
}