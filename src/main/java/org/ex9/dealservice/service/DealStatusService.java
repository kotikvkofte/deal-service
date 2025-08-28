package org.ex9.dealservice.service;

import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.config.RedisConfig;
import org.ex9.dealservice.dto.DealStatusDto;
import org.ex9.dealservice.mapper.DealMapper;
import org.ex9.dealservice.repository.DealStatusRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealStatusService {

    private final DealStatusRepository dealStatusRepository;
    private final DealMapper dealMapper;

    @Transactional(readOnly = true)
    @Cacheable(key = "'deals-status-all'", cacheNames = RedisConfig.DEALS_SUB)
    public List<DealStatusDto> getAll() {

        var deals = dealStatusRepository.findAllByIsActiveIsTrue();

        return deals.stream()
                .map(dealMapper::toDealStatusDto)
                .collect(Collectors.toList());

    }

}
