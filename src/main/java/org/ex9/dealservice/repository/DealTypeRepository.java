package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.DealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DealTypeRepository extends JpaRepository<DealType, String> {

    Optional<DealType> findByIdAndIsActiveTrue(String typeId);

    List<DealType> findAllByIsActiveTrue();

}
