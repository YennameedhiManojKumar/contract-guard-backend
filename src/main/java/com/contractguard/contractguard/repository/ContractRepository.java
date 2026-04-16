package com.contractguard.contractguard.repository;

import com.contractguard.contractguard.entity.Contract;
import com.contractguard.contractguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // All contracts where user is client
    List<Contract> findByClient(User client);

    // All contracts where user is freelancer
    List<Contract> findByFreelancer(User freelancer);

    // All contracts for a user (either role)
    @Query("SELECT c FROM Contract c WHERE c.client = :user OR c.freelancer = :user")
    List<Contract> findAllByUser(User user);

    // Active contracts only
    List<Contract> findByStatus(Contract.ContractStatus status);
}