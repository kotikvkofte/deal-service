package org.ex9.dealservice.service;

import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.exception.ContractorToRoleNotFondException;
import org.ex9.dealservice.mapper.ContractorToRoleMapper;
import org.ex9.dealservice.model.ContractorToRole;
import org.ex9.dealservice.model.ContractorToRoleId;
import org.ex9.dealservice.repository.ContractorToRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorToRoleServiceTest {

    @Mock
    private ContractorToRoleRepository repository;

    @Mock
    private ContractorToRoleMapper mapper;

    @InjectMocks
    private ContractorToRoleService service;

    @Test
    void testAddNewRole_Success() {
        ContractorToRoleDto dto = new ContractorToRoleDto();
        dto.setContractorId(UUID.randomUUID());
        dto.setRoleId("BORROWER");

        var id = new ContractorToRoleId();
        id.setContractorId(dto.getContractorId());
        id.setRoleId(dto.getRoleId());

        ContractorToRole contractorToRole = new ContractorToRole();
        contractorToRole.setId(id);
        contractorToRole.setIsActive(true);

        when(mapper.toContractorToRole(dto)).thenReturn(contractorToRole);
        when(repository.save(contractorToRole)).thenReturn(contractorToRole);

        service.addNewRole(dto);

        verify(mapper, times(1)).toContractorToRole(dto);
        verify(repository, times(1)).save(contractorToRole);
    }

    @Test
    void testDeleteRole_Success() {
        ContractorToRoleDto dto = new ContractorToRoleDto();
        dto.setContractorId(UUID.randomUUID());
        dto.setRoleId("WARRANTY");

        var id = new ContractorToRoleId();
        id.setContractorId(dto.getContractorId());
        id.setRoleId(dto.getRoleId());

        ContractorToRole contractorToRole = new ContractorToRole();
        contractorToRole.setId(id);
        contractorToRole.setIsActive(true);

        when(mapper.toContractorToRole(dto)).thenReturn(contractorToRole);
        when(repository.findByIdAndIsActiveTrue(id)).thenReturn(Optional.of(contractorToRole));
        doNothing().when(repository).logicalDeleteByIds(dto.getContractorId(), dto.getRoleId());

        service.deleteRole(dto);

        // Проверки
        verify(mapper, times(1)).toContractorToRole(dto);
        verify(repository, times(1)).findByIdAndIsActiveTrue(id);
        verify(repository, times(1)).logicalDeleteByIds(dto.getContractorId(), dto.getRoleId());
    }

    @Test
    void testDeleteRole_NotFound() {
        ContractorToRoleDto dto = new ContractorToRoleDto();
        dto.setContractorId(UUID.randomUUID());
        dto.setRoleId("WARRANTY");

        var id = new ContractorToRoleId();
        id.setContractorId(dto.getContractorId());
        id.setRoleId(dto.getRoleId());

        ContractorToRole contractorToRole = new ContractorToRole();
        contractorToRole.setId(id);
        contractorToRole.setIsActive(true);

        when(mapper.toContractorToRole(dto)).thenReturn(contractorToRole);
        when(repository.findByIdAndIsActiveTrue(id)).thenReturn(Optional.empty());

        assertThrows(ContractorToRoleNotFondException.class, () -> service.deleteRole(dto));

        // Проверки
        verify(mapper, times(1)).toContractorToRole(dto);
        verify(repository, times(1)).findByIdAndIsActiveTrue(id);
        verify(repository, never()).logicalDeleteByIds(dto.getContractorId(), dto.getRoleId());
    }
}