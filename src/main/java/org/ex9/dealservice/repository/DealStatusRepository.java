package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealStatusRepository extends JpaRepository<DealStatus, String> {

    Optional<DealStatus> findByIdAndIsActiveIsTrue(String id);

}
