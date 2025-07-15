package org.ex9.dealservice.service;

import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.exception.ContractorToRoleNotFondException;
import org.ex9.dealservice.mapper.ContractorToRoleMapper;
import org.ex9.dealservice.model.ContractorToRole;
import org.ex9.dealservice.repository.ContractorToRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления ролями контрагентов в сделке.
 *
 * <p>Позволяет добавлять и логически удалять роли у контрагентов сделок.</p>
 *
 * @author Краковцев Артём
 */
@Service
@RequiredArgsConstructor
public class ContractorToRoleService {

    private final ContractorToRoleRepository repository;
    private final ContractorToRoleMapper mapper;

    /**
     * Добавляет новую роль контрагенту сделки.
     *
     * @param dto DTO с данными роли и контрагента
     */
    @Transactional
    public void addNewRole(ContractorToRoleDto dto) {
        ContractorToRole contractorToRole = mapper.toContractorToRole(dto);
        repository.save(contractorToRole);
    }

    /**
     * Логически удаляет роль у контрагента сделки.
     *
     * @param dto DTO с идентификаторами контрагента и роли
     */
    @Transactional
    public void deleteRole(ContractorToRoleDto dto) {
        ContractorToRole contractorToRole = mapper.toContractorToRole(dto);
        repository.findByIdAndIsActiveTrue(contractorToRole.getId())
                .orElseThrow(() -> new ContractorToRoleNotFondException("ContractorToRole not found"));
        repository.logicalDeleteByIds(contractorToRole.getId().getContractorId(), contractorToRole.getId().getRoleId());
    }

}
