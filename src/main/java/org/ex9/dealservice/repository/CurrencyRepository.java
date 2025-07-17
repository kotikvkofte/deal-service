package org.ex9.dealservice.repository;

import jakarta.validation.constraints.Size;
import org.ex9.dealservice.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    Optional<Currency> findByIdAndIsActiveTrue(@Size(max = 3) String id);

}
