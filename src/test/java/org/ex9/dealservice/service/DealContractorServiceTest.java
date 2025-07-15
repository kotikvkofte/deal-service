package org.ex9.dealservice.service;

import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.mapper.DealContractorMapper;
import org.ex9.dealservice.model.Deal;
import org.ex9.dealservice.model.DealContractor;
import org.ex9.dealservice.repository.DealContractorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealContractorServiceTest {

    @Mock
    private DealContractorRepository dealContractorRepository;

    @Mock
    private DealContractorMapper dealContractorMapper;

    @InjectMocks
    private DealContractorService service;

    @Test
    void testSaveDealContractor_addNewContractor_success() {
        UUID dealId = UUID.randomUUID();
        var id = UUID.randomUUID();
        var request = DealContractorSaveRequestDto.builder()
                .dealId(dealId.toString())
                .contractorId("CONTR1")
                .name("John Doe")
                .inn("1234567890")
                .main(true)
                .build();
        DealContractor contractor = DealContractor.builder()
                .id(id)
                .contractorId("CONTR1")
                .deal(Deal.builder().id(dealId).build())
                .build();

        when(dealContractorMapper.toNewDealContractor(request)).thenReturn(contractor);
        when(dealContractorRepository.save(contractor)).thenReturn(contractor);

        UUID result = service.saveDealContractor(request);

        assertEquals(id, result);
        verify(dealContractorMapper, times(1)).toNewDealContractor(request);
        verify(dealContractorRepository, times(1)).save(contractor);
        verify(dealContractorRepository, never()).findByIdAndIsActiveTrue(any());
    }

    @Test
    void testSaveDealContractor_updateContractor_success() {
        UUID contractorId = UUID.randomUUID();
        DealContractorSaveRequestDto request = DealContractorSaveRequestDto.builder()
                .id(contractorId.toString())
                .dealId(UUID.randomUUID().toString())
                .contractorId("CONTR1")
                .name("John Doe")
                .inn("1234567890")
                .main(true)
                .build();

        DealContractor existingContractor = DealContractor.builder()
                .id(contractorId)
                .isActive(true)
                .build();

        DealContractor updatedContractor = DealContractor.builder()
                .id(contractorId)
                .isActive(true)
                .build();

        when(dealContractorRepository.findByIdAndIsActiveTrue(contractorId)).thenReturn(Optional.of(existingContractor));
        when(dealContractorMapper.toUpdateDealContractor(request, existingContractor)).thenReturn(updatedContractor);
        when(dealContractorRepository.save(updatedContractor)).thenReturn(updatedContractor);

        UUID result = service.saveDealContractor(request);

        assertEquals(contractorId, result);
        verify(dealContractorRepository, times(1)).findByIdAndIsActiveTrue(contractorId);
        verify(dealContractorMapper, times(1)).toUpdateDealContractor(request, existingContractor);
        verify(dealContractorRepository, times(1)).save(updatedContractor);
        verify(dealContractorMapper, never()).toNewDealContractor(any());
    }

    @Test
    void testSaveDealContractor_updateContractor_notFound() {
        UUID contractorId = UUID.randomUUID();
        var request = DealContractorSaveRequestDto.builder()
                .id(contractorId.toString())
                .dealId(UUID.randomUUID().toString())
                .build();

        when(dealContractorRepository.findByIdAndIsActiveTrue(contractorId)).thenReturn(Optional.empty());

        DealContractorNotFondException exception = assertThrows(
                DealContractorNotFondException.class,
                () -> service.saveDealContractor(request)
        );
        assertEquals("Contractor with id '" + contractorId + "' not found", exception.getMessage());
        verify(dealContractorRepository, never()).save(any());
    }

    @Test
    void testDeleteDealContractor_success() {
        UUID contractorId = UUID.randomUUID();
        DealContractor contractor = DealContractor.builder()
                .id(contractorId)
                .isActive(true)
                .build();

        when(dealContractorRepository.findByIdAndIsActiveTrue(contractorId)).thenReturn(Optional.of(contractor));
        doNothing().when(dealContractorRepository).logicalDeleteById(contractorId);

        service.deleteDealContractor(contractorId);

        verify(dealContractorRepository, times(1)).findByIdAndIsActiveTrue(contractorId);
        verify(dealContractorRepository, times(1)).logicalDeleteById(contractorId);
    }

    @Test
    void testDeleteDealContractor_notFound() {
        UUID contractorId = UUID.randomUUID();

        when(dealContractorRepository.findByIdAndIsActiveTrue(contractorId)).thenReturn(Optional.empty());

        DealContractorNotFondException exception = assertThrows(
                DealContractorNotFondException.class,
                () -> service.deleteDealContractor(contractorId)
        );
        assertEquals("Deal contractor with id '" + contractorId + "' not found", exception.getMessage());
        verify(dealContractorRepository, never()).logicalDeleteById(any());
    }

}