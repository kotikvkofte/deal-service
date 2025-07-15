package org.ex9.dealservice.service;

import org.ex9.dealservice.dto.*;
import org.ex9.dealservice.exception.DealNotFondException;
import org.ex9.dealservice.exception.DealStatusNotFondException;
import org.ex9.dealservice.mapper.DealMapper;
import org.ex9.dealservice.model.Deal;
import org.ex9.dealservice.model.DealStatus;
import org.ex9.dealservice.model.DealSum;
import org.ex9.dealservice.repository.DealRepository;
import org.ex9.dealservice.repository.DealStatusRepository;
import org.ex9.dealservice.repository.DealSumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private DealStatusRepository dealStatusRepository;

    @Mock
    private DealMapper dealMapper;

    @Mock
    private DealSumRepository dealSumRepository;

    @InjectMocks
    private DealService service;

    @Test
    void testDealSave_NewDeal_Success() {
        UUID dealId = UUID.randomUUID();
        var sumDto = new DealSumDto("100000", "RUB");

        var request = DealSaveRequestDto.builder()
                .description("Test Deal")
                .agreementNumber("AGR-001")
                .agreementDate(LocalDate.now())
                .agreementStartDt(LocalDateTime.now())
                .availabilityDate(LocalDate.now().plusDays(30))
                .typeId("CREDIT")
                .sum(sumDto)
                .build();

        Deal deal = new Deal();
        deal.setId(dealId);
        DealSum dealSum = new DealSum();

        when(dealMapper.toNewDeal(request)).thenReturn(deal);
        when(dealRepository.save(deal)).thenReturn(deal);
        when(dealMapper.toDealSum(sumDto, deal)).thenReturn(dealSum);
        when(dealSumRepository.save(dealSum)).thenReturn(dealSum);

        UUID result = service.dealSave(request);

        assertEquals(dealId, result);
        verify(dealMapper, times(1)).toNewDeal(request);
        verify(dealRepository, times(1)).save(deal);
        verify(dealMapper, times(1)).toDealSum(sumDto, deal);
        verify(dealSumRepository, times(1)).save(dealSum);
        verify(dealRepository, never()).findById(any());
    }

    @Test
    void testDealSave_updateDeal_success() {
        UUID dealId = UUID.randomUUID();
        var sumDto = new DealSumDto("200000", "USD");
        var request = DealSaveRequestDto.builder()
                .id(dealId.toString())
                .description("Updated Deal")
                .sum(sumDto)
                .build();

        Deal existingDeal = new Deal();
        existingDeal.setId(dealId);
        Deal updatedDeal = new Deal();
        updatedDeal.setId(dealId);
        DealSum dealSum = new DealSum();

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(existingDeal));
        when(dealMapper.toUpdateDeal(request, existingDeal)).thenReturn(updatedDeal);
        when(dealRepository.save(updatedDeal)).thenReturn(updatedDeal);
        when(dealMapper.toDealSum(sumDto, updatedDeal)).thenReturn(dealSum);
        when(dealSumRepository.save(dealSum)).thenReturn(dealSum);

        UUID result = service.dealSave(request);

        assertEquals(dealId, result);
        verify(dealRepository, times(1)).findById(dealId);
        verify(dealMapper, times(1)).toUpdateDeal(request, existingDeal);
        verify(dealRepository, times(1)).save(updatedDeal);
        verify(dealMapper, times(1)).toDealSum(sumDto, updatedDeal);
        verify(dealSumRepository, times(1)).save(dealSum);
    }

    @Test
    void testDealSave_updateDeal_notFound() {
        UUID dealId = UUID.randomUUID();
        DealSaveRequestDto request = new DealSaveRequestDto();
        request.setId(dealId.toString());

        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

        DealNotFondException exception = assertThrows(
                DealNotFondException.class,
                () -> service.dealSave(request)
        );
        assertEquals("Deal with id '" + dealId + "' not found", exception.getMessage());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void testChangeStatus_success() {
        DealChangeStatusDto request = new DealChangeStatusDto();
        UUID dealId = UUID.randomUUID();
        request.setDealId(dealId.toString());
        request.setStatusId("ACTIVE");

        Deal deal = new Deal();
        deal.setId(dealId);
        DealStatus status = new DealStatus();
        status.setId("ACTIVE");

        when(dealRepository.findByIdAndIsActiveTrue(dealId)).thenReturn(Optional.of(deal));
        when(dealStatusRepository.findByIdAndIsActiveIsTrue("ACTIVE")).thenReturn(Optional.of(status));
        when(dealRepository.save(deal)).thenReturn(deal);

        service.changeStatus(request);

        verify(dealRepository, times(1)).findByIdAndIsActiveTrue(dealId);
        verify(dealStatusRepository, times(1)).findByIdAndIsActiveIsTrue("ACTIVE");
        verify(dealRepository, times(1)).save(deal);
        assertEquals(status, deal.getStatus());
    }

    @Test
    void testChangeStatus_dealNotFound() {
        UUID dealId = UUID.randomUUID();
        DealChangeStatusDto request = new DealChangeStatusDto(dealId.toString(), "ACTIVE");

        when(dealRepository.findByIdAndIsActiveTrue(dealId)).thenReturn(Optional.empty());

        DealNotFondException exception = assertThrows(
                DealNotFondException.class,
                () -> service.changeStatus(request)
        );
        assertEquals("Deal with id '" + dealId + "' not found", exception.getMessage());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void testChangeStatus_statusNotFound() {
        UUID dealId = UUID.randomUUID();
        DealChangeStatusDto request = new DealChangeStatusDto(dealId.toString(), "ACTIVE");

        Deal deal = new Deal();
        deal.setId(dealId);

        when(dealRepository.findByIdAndIsActiveTrue(dealId)).thenReturn(Optional.of(deal));
        when(dealStatusRepository.findByIdAndIsActiveIsTrue("ACTIVE")).thenReturn(Optional.empty());

        DealStatusNotFondException exception = assertThrows(
                DealStatusNotFondException.class,
                () -> service.changeStatus(request)
        );
        assertEquals("Deal Status with id 'ACTIVE' not found", exception.getMessage());
        verify(dealRepository, never()).save(any());
    }

    @Test
    void testGetDealById_success() {
        String dealId = UUID.randomUUID().toString();
        Deal deal = Deal.builder().id(UUID.fromString(dealId)).build();
        var responseDto = DealResponseDto.builder().id(dealId).build();

        when(dealRepository.findByIdAndIsActiveTrue(UUID.fromString(dealId))).thenReturn(Optional.of(deal));
        when(dealMapper.toDealResponseDto(deal)).thenReturn(responseDto);

        DealResponseDto result = service.getDealById(dealId);

        assertEquals(responseDto, result);
        verify(dealRepository, times(1)).findByIdAndIsActiveTrue(UUID.fromString(dealId));
        verify(dealMapper, times(1)).toDealResponseDto(deal);
    }

    @Test
    void testGetDealById_notFound() {
        String dealId = UUID.randomUUID().toString();

        when(dealRepository.findByIdAndIsActiveTrue(UUID.fromString(dealId))).thenReturn(Optional.empty());

        DealNotFondException exception = assertThrows(
                DealNotFondException.class,
                () -> service.getDealById(dealId)
        );
        assertEquals("Deal with id '" + dealId + "' not found", exception.getMessage());
        verify(dealMapper, never()).toDealResponseDto(any());
    }

    @Test
    void testSearchDeals_success() {
        var request = DealSearchRequestDto.builder()
                .page(0)
                .size(10)
                .sortBy("agreementNumber")
                .sortDirection("ASC")
                .build();

        Deal deal = new Deal();
        DealResponseDto responseDto = new DealResponseDto();
        Page<Deal> dealPage = new PageImpl<>(Collections.singletonList(deal));

        when(dealRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(dealPage);
        when(dealMapper.toDealResponseDto(deal)).thenReturn(responseDto);

        Page<DealResponseDto> result = service.searchDeals(request);

        assertEquals(1, result.getContent().size());
        assertEquals(responseDto, result.getContent().get(0));
        verify(dealRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verify(dealMapper, times(1)).toDealResponseDto(deal);
    }

    @Test
    void testSearchDeals_withMultipleFiltersAndDescSort_success() {
        var request = DealSearchRequestDto.builder()
                .description("Кредитная")
                .statusIds(List.of("ACTIVE"))
                .page(1)
                .size(5)
                .sortBy("agreementDate")
                .sortDirection("DESC")
                .build();

        Deal deal = new Deal();
        DealResponseDto responseDto = new DealResponseDto();
        responseDto.setDescription("Кредитная сделка");
        responseDto.setStatus(new DealStatusDto("ACTIVE", "Утвержденная"));
        Page<Deal> dealPage = new PageImpl<>(Collections.singletonList(deal),
                PageRequest.of(
                        1, 5,
                        Sort.by(Sort.Direction.DESC, "agreementDate")), 1);

        when(dealRepository.findAll(
                any(Specification.class),
                eq(PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "agreementDate")))))
                .thenReturn(dealPage);
        when(dealMapper.toDealResponseDto(deal)).thenReturn(responseDto);

        Page<DealResponseDto> result = service.searchDeals(request);

        assertEquals(1, result.getContent().size());
        assertEquals("Кредитная сделка", result.getContent().get(0).getDescription());
        assertEquals("ACTIVE", result.getContent().get(0).getStatus().getId());
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        verify(dealRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verify(dealMapper, times(1)).toDealResponseDto(deal);
    }

    @Test
    void testSearchDeals_EmptyFiltersWithPagination_Success() {
        DealSearchRequestDto request = new DealSearchRequestDto();
        request.setPage(0);
        request.setSize(20);

        Deal deal1 = new Deal();
        Deal deal2 = new Deal();
        DealResponseDto responseDto1 = new DealResponseDto();
        DealResponseDto responseDto2 = new DealResponseDto();
        Page<Deal> dealPage = new PageImpl<>(Arrays.asList(deal1, deal2), PageRequest.of(0, 20, Sort.unsorted()), 2);

        when(dealRepository.findAll(any(Specification.class), eq(PageRequest.of(0, 20, Sort.unsorted())))).thenReturn(dealPage);
        when(dealMapper.toDealResponseDto(deal1)).thenReturn(responseDto1);
        when(dealMapper.toDealResponseDto(deal2)).thenReturn(responseDto2);

        Page<DealResponseDto> result = service.searchDeals(request);

        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertEquals(20, result.getSize());
        assertEquals(2, result.getTotalElements());
        verify(dealRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
        verify(dealMapper, times(1)).toDealResponseDto(deal1);
        verify(dealMapper, times(1)).toDealResponseDto(deal2);
    }

}