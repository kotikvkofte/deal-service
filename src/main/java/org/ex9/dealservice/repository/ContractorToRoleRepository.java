package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.ContractorToRole;
import org.ex9.dealservice.model.ContractorToRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractorToRoleRepository extends JpaRepository<ContractorToRole, ContractorToRoleId> {

    List<ContractorToRole> findAllByIdContractorIdAndIsActiveTrue(UUID contractorId);

    Optional<ContractorToRole> findByIdAndIsActiveTrue(ContractorToRoleId id);

    @Query("UPDATE ContractorToRole d SET d.isActive=false WHERE d.contractor.id=:contractorId AND d.role.id=:roleId")
    @Modifying
    void logicalDeleteByIds(UUID contractorId, String roleId);

}
