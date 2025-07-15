package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.DealContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealContractorRepository extends JpaRepository<DealContractor, UUID> {

    List<DealContractor> findAllByDealIdAndIsActiveTrue(UUID dealId);

    Optional<DealContractor> findByIdAndIsActiveTrue(UUID id);

    @Query("UPDATE DealContractor d SET d.isActive=false WHERE d.id=:id")
    @Modifying
    void logicalDeleteById(UUID id);

}
