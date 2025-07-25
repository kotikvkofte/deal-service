package org.ex9.dealservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.DealChangeStatusDto;
import org.ex9.dealservice.dto.DealResponseDto;
import org.ex9.dealservice.dto.DealSaveRequestDto;
import org.ex9.dealservice.dto.DealSearchRequestDto;
import org.ex9.dealservice.dto.DealSumDto;
import org.ex9.dealservice.exception.DealNotFondException;
import org.ex9.dealservice.exception.DealStatusNotFondException;
import org.ex9.dealservice.mapper.DealMapper;
import org.ex9.dealservice.model.Deal;
import org.ex9.dealservice.repository.DealRepository;
import org.ex9.dealservice.repository.DealSpecification;
import org.ex9.dealservice.repository.DealStatusRepository;
import org.ex9.dealservice.repository.DealSumRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервисный класс для управления сделками.
 *
 * <p>Содержит бизнес-логику по созданию, обновлению, поиску и изменению статуса сделок.</p>
 *
 * @author Краковцев Артём
 */
@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final DealStatusRepository dealStatusRepository;
    private final DealMapper dealMapper;
    private final DealSumRepository dealSumRepository;

    /**
     * Создает новую сделку или обновляет существующую.
     * При создании устанавливает статус по умолчанию.
     *
     * @param request DTO с данными сделки
     * @return UUID сохранённой сделки
     */
    @Transactional
    public UUID dealSave(DealSaveRequestDto request) {
        Deal deal;
        boolean isNewDeal = request.getId() == null;

        if (isNewDeal) {
            deal = dealMapper.toNewDeal(request);
        } else {
            var foundDeal = dealRepository.findById(request.getId())
                    .orElseThrow(() -> new DealNotFondException("Deal with id '" + request.getId() + "' not found"));
            deal = dealMapper.toUpdateDeal(request, foundDeal);
        }
        var result = dealRepository.save(deal);
        addDealSum(request.getSum(), deal);

        return result.getId();
    }

    /**
     * Создает новую сделку или обновляет существующую.
     * При создании устанавливает статус по умолчанию.
     *
     * @param request DTO с данными сделки
     * @return UUID сохранённой сделки
     */
    @Transactional
    public UUID dealSave(DealSaveRequestDto request, String userId) {
        Deal deal;
        boolean isNewDeal = request.getId() == null;

        if (isNewDeal) {
            deal = dealMapper.toNewDeal(request);
            deal.setCreateUserId(userId);
        } else {
            var foundDeal = dealRepository.findById(request.getId())
                    .orElseThrow(() -> new DealNotFondException("Deal with id '" + request.getId() + "' not found"));
            deal = dealMapper.toUpdateDeal(request, foundDeal);
            deal.setModifyUserId(userId);
        }
        var result = dealRepository.save(deal);
        addDealSum(request.getSum(), deal);

        return result.getId();
    }

    /**
     * Добавляет или обновляет сумму сделки.
     *
     * @param dto  DTO суммы сделки
     * @param deal объект сделки, к которой относится сумма
     */
    @Transactional
    protected void addDealSum(DealSumDto dto, Deal deal) {
        var dealSum = dealMapper.toDealSum(dto, deal);

        dealSumRepository.save(dealSum);
    }

    /**
     * Изменяет статус существующей сделки.
     *
     * @param request DTO с ID сделки и новым статусом
     */
    @Transactional
    public void changeStatus(DealChangeStatusDto request) {

        var deal = dealRepository.findByIdAndIsActiveTrue(request.getDealId())
                .orElseThrow(() -> new DealNotFondException("Deal with id '" + request.getDealId() + "' not found"));

        var status = dealStatusRepository.findByIdAndIsActiveIsTrue((request.getStatusId()))
                .orElseThrow(() -> new DealStatusNotFondException("Deal Status with id '" + request.getStatusId() + "' not found"));

        deal.setStatus(status);
        dealRepository.save(deal);
    }

    /**
     * Получает полную информацию о сделке по её ID.
     *
     * @param id идентификатор сделки
     * @return DTO сделки
     */
    @Transactional(readOnly = true)
    public DealResponseDto getDealById(UUID id) {
        Deal deal = dealRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new DealNotFondException("Deal with id '" + id + "' not found"));

        return dealMapper.toDealResponseDto(deal);
    }

    /**
     * Выполняет постраничный поиск активных сделок с поддержкой фильтрации и сортировки.
     *
     * @param request параметры поиска и пагинации
     * @return страница DTO сделок
     */
    @Transactional(readOnly = true)
    public Page<DealResponseDto> searchDeals(@Valid DealSearchRequestDto request) {
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            Sort.Direction direction = request.getSortDirection() != null && request.getSortDirection().equalsIgnoreCase("DESC")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getSortBy());
        }

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Deal> deals = dealRepository.findAll(DealSpecification.searchDeals(request), pageRequest);
        return deals.map(dealMapper::toDealResponseDto);
    }

    @Transactional(readOnly = true)
    public byte[] exportDealsToExcel(@Valid DealSearchRequestDto request) {
        Sort sort = Sort.unsorted();
        if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(request.getSortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getSortBy());
        }

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);
        Page<Deal> deals = dealRepository.findAll(DealSpecification.searchDeals(request), pageRequest);

        return dealMapper.toExcel(deals);
    }

}
