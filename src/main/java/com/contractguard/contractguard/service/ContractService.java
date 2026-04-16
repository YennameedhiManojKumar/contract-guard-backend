package com.contractguard.contractguard.service;

import com.contractguard.contractguard.dto.request.*;
import com.contractguard.contractguard.dto.response.*;
import com.contractguard.contractguard.entity.*;
import com.contractguard.contractguard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;

    // Create contract (only CLIENT can create)
    @Transactional
    public ContractResponse createContract(ContractRequest request,
                                           String clientEmail) {
        // Load client
        User client = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Load freelancer
        User freelancer = userRepository.findById(request.getFreelancerId())
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        // Build contract
        Contract contract = Contract.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .totalValue(request.getTotalValue())
                .client(client)
                .freelancer(freelancer)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(Contract.ContractStatus.ACTIVE)
                .build();

        Contract saved = contractRepository.save(contract);

        // Save milestones if provided
        if (request.getMilestones() != null) {
            List<Milestone> milestones = request.getMilestones()
                    .stream()
                    .map(m -> Milestone.builder()
                            .contract(saved)
                            .title(m.getTitle())
                            .description(m.getDescription())
                            .amount(m.getAmount())
                            .deadline(m.getDeadline())
                            .status(Milestone.MilestoneStatus.PENDING)
                            .build())
                    .collect(Collectors.toList());

            milestoneRepository.saveAll(milestones);
            saved.setMilestones(milestones);
        }

        return mapToResponse(saved);
    }

    // Get all contracts for logged in user
    public List<ContractResponse> getMyContracts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return contractRepository.findAllByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get single contract by ID
    public ContractResponse getContractById(Long id, String email) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Security check — only client or freelancer can view
        boolean isClient = contract.getClient().getEmail().equals(email);
        boolean isFreelancer = contract.getFreelancer().getEmail().equals(email);

        if (!isClient && !isFreelancer) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(contract);
    }

    // Mark milestone as completed
    @Transactional
    public MilestoneResponse completeMilestone(Long milestoneId,
                                                String email) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found"));

        // Only freelancer can mark as complete
        String freelancerEmail = milestone.getContract()
                                          .getFreelancer().getEmail();
        if (!freelancerEmail.equals(email)) {
            throw new RuntimeException(
                "Only the freelancer can complete milestones");
        }

        milestone.setStatus(Milestone.MilestoneStatus.COMPLETED);
        milestone.setCompletedAt(LocalDateTime.now());
        milestoneRepository.save(milestone);

        return mapMilestoneToResponse(milestone);
    }

    // ─── Mappers ─────────────────────────────────────

    private ContractResponse mapToResponse(Contract contract) {
        List<MilestoneResponse> milestoneResponses = null;

        if (contract.getMilestones() != null) {
            milestoneResponses = contract.getMilestones()
                    .stream()
                    .map(this::mapMilestoneToResponse)
                    .collect(Collectors.toList());
        }

        return ContractResponse.builder()
                .id(contract.getId())
                .title(contract.getTitle())
                .description(contract.getDescription())
                .totalValue(contract.getTotalValue())
                .status(contract.getStatus().name())
                .clientName(contract.getClient().getName())
                .freelancerName(contract.getFreelancer().getName())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .createdAt(contract.getCreatedAt())
                .milestones(milestoneResponses)
                .build();
    }

    private MilestoneResponse mapMilestoneToResponse(Milestone milestone) {
        return MilestoneResponse.builder()
                .id(milestone.getId())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .amount(milestone.getAmount())
                .deadline(milestone.getDeadline())
                .status(milestone.getStatus().name())
                .completedAt(milestone.getCompletedAt())
                .build();
    }
}