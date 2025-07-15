package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.ContractorRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractorRoleRepository extends JpaRepository<ContractorRole, String> {

    Optional<ContractorRole> findByIdAndIsActiveTrue(String id);

}
