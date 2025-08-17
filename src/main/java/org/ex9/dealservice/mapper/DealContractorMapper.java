package org.ex9.dealservice.mapper;

import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.exception.DealNotFondException;
import org.ex9.dealservice.model.DealContractor;
import org.ex9.dealservice.repository.DealRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Маппер для преобразования DTO контрагента сделки в сущность и обратно.
 *
 * <p>Используется для создания и обновления записей о контрагентах в сделках.</p>
 *
 * @author Краковцев Артём
 */
@Component
@RequiredArgsConstructor
public class DealContractorMapper {

    private final DealRepository dealRepository;

    /**
     * Преобразует DTO в нового контрагента сделки.
     *
     * @param request DTO с данными контрагента
     * @return сущность {@link DealContractor}
     * @throws DealNotFondException если сделка не найдена
     */
    public DealContractor toNewDealContractor(DealContractorSaveRequestDto request) {

        var deal = dealRepository.findByIdAndIsActiveTrue(request.getDealId())
                .orElseThrow(() -> new DealNotFondException("Deal with id " + request.getDealId() + " not found"));

        return DealContractor.builder()
                .deal(deal)
                .contractorId(request.getContractorId())
                .name(request.getName())
                .inn(request.getInn())
                .main(request.isMain())
                .createDate(LocalDate.now())
                .createUserId(null)
                .build();
    }

    /**
     * Обновляет существующего контрагента сделки на основе данных из DTO.
     *
     * @param request    DTO с обновлёнными данными контрагента
     * @param contractor текущая сущность контрагента
     * @return обновлённая сущность {@link DealContractor}
     * @throws DealNotFondException если сделка не найдена
     */
    public DealContractor toUpdateDealContractor(DealContractorSaveRequestDto request, DealContractor contractor) {
        var deal = dealRepository.findByIdAndIsActiveTrue(request.getDealId())
                .orElseThrow(() -> new DealNotFondException("Deal with id " + request.getDealId() + " not found"));
        contractor.setDeal(deal);
        contractor.setContractorId(request.getContractorId());
        contractor.setName(request.getName());
        contractor.setInn(request.getInn());
        contractor.setMain(request.isMain());
        contractor.setModifyDate(LocalDateTime.now());
        contractor.setModifyUserId(null);

        return contractor;
    }

}
