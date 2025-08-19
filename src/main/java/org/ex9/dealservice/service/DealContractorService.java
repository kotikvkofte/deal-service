package org.ex9.dealservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.dto.rabbit.ContractorDto;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.mapper.DealContractorMapper;
import org.ex9.dealservice.model.DealContractor;
import org.ex9.dealservice.repository.DealContractorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления контрагентами, привязанными к сделке.
 *
 * <p>Позволяет создавать, обновлять и логически удалять контрагентов сделок.</p>
 *
 * @author Краковцев Артём
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class DealContractorService {

    private final DealContractorRepository dealContractorRepository;
    private final DealContractorMapper dealContractorMapper;

    /**
     * Сохраняет нового или обновляет существующего контрагента сделки.
     *
     * @param request DTO с данными контрагента сделки
     * @return UUID сохраненного контрагента
     */
    @Transactional
    public UUID saveDealContractor(DealContractorSaveRequestDto request) {

        DealContractor dealContractor;
        boolean isNewContractor = request.getId() == null;

        if (isNewContractor) {
            dealContractor = dealContractorMapper.toNewDealContractor(request);
        } else {
            var foundDealContractor = dealContractorRepository.findByIdAndIsActiveTrue(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new DealContractorNotFondException("Contractor with id '" + request.getId() + "' not found"));
            dealContractor = dealContractorMapper.toUpdateDealContractor(request, foundDealContractor);
        }

        return dealContractorRepository.save(dealContractor).getId();
    }

    /**
     * Сохраняет нового или обновляет существующего контрагента сделки.
     *
     * @param request DTO с данными контрагента сделки
     * @return UUID сохраненного контрагента
     */
    @Transactional
    public UUID saveDealContractor(DealContractorSaveRequestDto request, String userId) {

        DealContractor dealContractor;
        boolean isNewContractor = request.getId() == null;

        if (isNewContractor) {
            dealContractor = dealContractorMapper.toNewDealContractor(request);
            dealContractor.setCreateUserId(userId);
        } else {
            var foundDealContractor = dealContractorRepository.findByIdAndIsActiveTrue(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new DealContractorNotFondException("Contractor with id '" + request.getId() + "' not found"));
            dealContractor = dealContractorMapper.toUpdateDealContractor(request, foundDealContractor);
            dealContractor.setModifyUserId(userId);
        }

        return dealContractorRepository.save(dealContractor).getId();
    }

    /**
     * Логически удаляет контрагента сделки по его идентификатору.
     *
     * @param dealContractorId идентификатор контрагента
     */
    @Transactional
    public void deleteDealContractor(UUID dealContractorId) {

        dealContractorRepository.findByIdAndIsActiveTrue(dealContractorId)
                .orElseThrow(() -> new DealContractorNotFondException("Deal contractor with id '" + dealContractorId + "' not found"));

        dealContractorRepository.logicalDeleteById(dealContractorId);
    }

    @Transactional
    public void updateDealContractorFomRabbit(ContractorDto contractorDto) throws DealContractorNotFondException {
        String contractorId = contractorDto.getId();

        List<DealContractor> dealContractors = dealContractorRepository.findAllByContractorIdAndIsActiveTrue(contractorId);

        if (dealContractors.isEmpty()) {
            log.warn("No deal contractors found for contractorId '{}'", contractorId);
            throw new DealContractorNotFondException("Contractor with id '" + contractorId + "' not found");
        }

        dealContractors.forEach(dealContractor -> updateContractor(dealContractor, contractorDto));

    }

    private void updateContractor(DealContractor dealContractor, ContractorDto contractorDto) {

        if (dealContractor.getModifyDate().isAfter(contractorDto.getModifyDateTime())) {
            return;
        }

        dealContractor.setName(contractorDto.getName());
        dealContractor.setInn(contractorDto.getInn());
        dealContractor.setModifyDate(contractorDto.getModifyDateTime());
        dealContractor.setModifyUserId(contractorDto.getModifyUserId());

        dealContractorRepository.save(dealContractor);
    }

}
