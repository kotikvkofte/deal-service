package org.ex9.dealservice.mapper;

import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.exception.ContractorRoleNotFondException;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.model.ContractorToRole;
import org.ex9.dealservice.model.ContractorToRoleId;
import org.ex9.dealservice.repository.ContractorRoleRepository;
import org.ex9.dealservice.repository.DealContractorRepository;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования DTO роли контрагента в сущность.
 *
 * <p>Создаёт связь между контрагентом сделки и его ролью.</p>
 *
 * @author Краковцев Артём
 */
@Component
@RequiredArgsConstructor
public class ContractorToRoleMapper {

    private final ContractorRoleRepository contractorRoleRepository;
    private final DealContractorRepository dealContractorRepository;

    /**
     * Преобразует DTO в сущность {@link ContractorToRole}, устанавливая связь между контрагентом и ролью.
     *
     * @param dto DTO с идентификаторами роли и контрагента
     * @return объект {@link ContractorToRole}
     * @throws ContractorRoleNotFondException если роль не найдена
     * @throws DealContractorNotFondException если контрагент не найден
     */
    public ContractorToRole toContractorToRole(ContractorToRoleDto dto) {
        var role = contractorRoleRepository.findByIdAndIsActiveTrue(dto.getRoleId())
                .orElseThrow(() -> new ContractorRoleNotFondException("Contractor role with id '" + dto.getRoleId() + "' not found"));
        var contractor = dealContractorRepository.findByIdAndIsActiveTrue(dto.getContractorId())
                .orElseThrow(() -> new DealContractorNotFondException("Deal contractor with id '" + dto.getContractorId() + "' not found"));

        var id = new ContractorToRoleId();
        id.setContractorId(contractor.getId());
        id.setRoleId(role.getId());

        return ContractorToRole.builder()
                .id(id)
                .role(role)
                .contractor(contractor)
                .build();
    }

}
