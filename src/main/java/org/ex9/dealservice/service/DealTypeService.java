package org.ex9.dealservice.service;

import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.config.RedisConfig;
import org.ex9.dealservice.dto.DealTypeDto;
import org.ex9.dealservice.mapper.DealMapper;
import org.ex9.dealservice.repository.DealTypeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealTypeService {

    private final DealTypeRepository dealTypeRepository;
    private final DealMapper dealMapper;

    /**
     * Возвращает список активных типов сделок.
     *
     * @return список DTO активных типов сделок
     */
    @Transactional(readOnly = true)
    @Cacheable(key = "'deals-type-all'", cacheNames = RedisConfig.DEALS_SUB)
    public List<DealTypeDto> getAll() {
        return dealTypeRepository.findAllByIsActiveTrue().stream()
                .map(dealMapper::toDealTypeDto)
                .collect(Collectors.toList());
    }

    /**
     * Создает новый тип сделки или обновляет существующий.
     *
     * @param dealTypeDto DTO с данными типа сделки
     * @return Id сохранённого типа сделки
     */
    @Transactional
    @CacheEvict(key = "'deals-type-all'", cacheNames = RedisConfig.DEALS_SUB)
    public String save(DealTypeDto dealTypeDto) {
        var newDealType = dealTypeRepository.save(dealMapper.toDealType(dealTypeDto));
        return dealMapper.toDealTypeDto(newDealType).getId();
    }

}
