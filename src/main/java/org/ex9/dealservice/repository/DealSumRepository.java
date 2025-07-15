package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.DealSum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DealSumRepository extends JpaRepository<DealSum, Long> {

    List<DealSum> findAllByDealIdAndIsActiveTrue(UUID dealId);

}
