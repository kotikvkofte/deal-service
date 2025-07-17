package org.ex9.dealservice.repository;

import jakarta.persistence.criteria.Join;
import org.ex9.dealservice.dto.DealSearchRequestDto;
import org.ex9.dealservice.dto.DealSumDto;
import org.ex9.dealservice.model.ContractorRole;
import org.ex9.dealservice.model.ContractorToRole;
import org.ex9.dealservice.model.Deal;
import org.ex9.dealservice.model.DealContractor;
import org.ex9.dealservice.model.DealSum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;

public final class DealSpecification {

    private DealSpecification() {
    }

    public static Specification<Deal> searchDeals(DealSearchRequestDto request) {
        List<Specification<Deal>> specList = new ArrayList<>();

        specList.add(searchByIsActiveTrue());
        specList.add(searchByDealId(request.getDealId()));
        specList.add(searchByDescription(request.getDescription()));
        specList.add(searchByAgreementNumber(request.getAgreementNumber()));
        specList.add(searchByAgreementDate(request.getAgreementDateFrom(), request.getAgreementDateTo()));
        specList.add(searchByAvailabilityDate(request.getAgreementDateFrom(), request.getAgreementDateTo()));
        specList.add(searchByType(request.getTypeIds()));
        specList.add(searchByStatus(request.getStatusIds()));
        specList.add(searchByCloseDt(request.getCloseDtFrom(), request.getCloseDtTo()));
        specList.add(searchByBorrower(request.getBorrowerSearch()));
        specList.add(searchByWarranity(request.getWarrantySearch()));
        specList.add(searchBySum(request.getSum()));

        return specList.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);
    }

    private static Specification<Deal> searchByIsActiveTrue() {
        return (root, query, cb) -> cb.equal(root.get("isActive"), true);
    }

   private static Specification<Deal> searchByDealId(UUID dealId) {
        if (dealId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("id"), dealId);
   }

   private static Specification<Deal> searchByDescription(String description) {
        if (description == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("description"), description);
   }

   private static Specification<Deal> searchByAgreementNumber(String agreementNumber) {
        if (agreementNumber == null) {
            return null;
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("agreementNumber")),
                        "%" + agreementNumber + "%");
   }

   private static Specification<Deal> searchByAgreementDate(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return null;
        }
        return (root, query, cb) -> {
            var greater = cb.greaterThanOrEqualTo(root.get("agreementDate"), from);
            var less = cb.lessThanOrEqualTo(root.get("agreementDate"), to);
            return cb.and(greater, less);
        };
   }

    private static Specification<Deal> searchByAvailabilityDate(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            return null;
        }
        return (root, query, cb) -> {
            var greater = cb.greaterThanOrEqualTo(root.get("availabilityDate"), from);
            var less = cb.lessThanOrEqualTo(root.get("availabilityDate"), to);
            return cb.and(greater, less);
        };
    }

    private static Specification<Deal> searchByType(List<String> typesIds) {
        if (typesIds == null || typesIds.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get("type").get("id").in(typesIds);
    }

    private static Specification<Deal> searchByStatus(List<String> statusIds) {
        if (statusIds == null || statusIds.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.get("status").get("id").in(statusIds);
    }

    private static Specification<Deal> searchByCloseDt(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return null;
        }
        return (root, query, cb) -> {
            var greater = cb.greaterThanOrEqualTo(root.get("closeDt"), from);
            var less = cb.lessThanOrEqualTo(root.get("closeDt"), to);
            return cb.and(greater, less);
        };
    }

    private static Specification<Deal> searchByBorrower(String borrowerSearch) {
        if (borrowerSearch == null || borrowerSearch.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Deal, DealContractor> contractorJoin = root.join("contractor");
            Join<DealContractor, ContractorToRole> roleJoin = contractorJoin.join("contractorToRoles");
            Join<ContractorToRole, ContractorRole> role = roleJoin.join("role");
            var category = cb.equal(role.get("category"), "BORROWER");
            var search = cb.or(
                    cb.like(cb.lower(contractorJoin.get("contractorId")), "%" + borrowerSearch.toLowerCase() + "%"),
                    cb.like(cb.lower(contractorJoin.get("name")), "%" + borrowerSearch.toLowerCase() + "%"),
                    cb.like(cb.lower(contractorJoin.get("inn")), "%" + borrowerSearch.toLowerCase() + "%")
            );
            return cb.and(category, search);
        };
    }

    private static Specification<Deal> searchByWarranity(String warranitySearch) {
        if (warranitySearch == null || warranitySearch.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Deal, DealContractor> contractorJoin = root.join("contractor");
            Join<DealContractor, ContractorToRole> roleJoin = contractorJoin.join("contractorToRoles");
            Join<ContractorToRole, ContractorRole> role = roleJoin.join("role");
            var category = cb.equal(role.get("category"), "WARRANTY");
            var search = cb.or(
                    cb.like(cb.lower(contractorJoin.get("contractorId")), "%" + warranitySearch.toLowerCase() + "%"),
                    cb.like(cb.lower(contractorJoin.get("name")), "%" + warranitySearch.toLowerCase() + "%"),
                    cb.like(cb.lower(contractorJoin.get("inn")), "%" + warranitySearch.toLowerCase() + "%")
            );
            return cb.and(category, search);
        };
    }

    private static Specification<Deal> searchBySum(DealSumDto sumDto) {
        if (sumDto != null) {
            return (root, query, cb) -> {
                Join<Deal, DealSum> sumJoin = root.join("dealSums");
                List<Predicate> predicates = new ArrayList<>();

                if (sumDto.getValue() != null && !sumDto.getValue().isEmpty()) {
                    predicates.add(cb.equal(sumJoin.get("sum"), sumDto.getValue()));
                }
                if (sumDto.getCurrency() != null && !sumDto.getCurrency().isEmpty()) {
                    predicates.add(cb.equal(sumJoin.get("currency").get("id"), sumDto.getCurrency()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            };
        }
        return null;
    }

}
