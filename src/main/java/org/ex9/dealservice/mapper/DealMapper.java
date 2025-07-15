package org.ex9.dealservice.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ex9.dealservice.dto.ContractorRoleDto;
import org.ex9.dealservice.dto.DealContractorDto;
import org.ex9.dealservice.dto.DealResponseDto;
import org.ex9.dealservice.dto.DealSaveRequestDto;
import org.ex9.dealservice.dto.DealStatusDto;
import org.ex9.dealservice.dto.DealSumDto;
import org.ex9.dealservice.dto.DealTypeDto;
import org.ex9.dealservice.exception.CurrencyNotFondException;
import org.ex9.dealservice.exception.DealStatusNotFondException;
import org.ex9.dealservice.exception.DealTypeNotFondException;
import org.ex9.dealservice.model.ContractorRole;
import org.ex9.dealservice.model.ContractorToRole;
import org.ex9.dealservice.model.Deal;
import org.ex9.dealservice.model.DealContractor;
import org.ex9.dealservice.model.DealStatus;
import org.ex9.dealservice.model.DealSum;
import org.ex9.dealservice.model.DealType;
import org.ex9.dealservice.repository.ContractorToRoleRepository;
import org.ex9.dealservice.repository.CurrencyRepository;
import org.ex9.dealservice.repository.DealContractorRepository;
import org.ex9.dealservice.repository.DealStatusRepository;
import org.ex9.dealservice.repository.DealSumRepository;
import org.ex9.dealservice.repository.DealTypeRepository;
import org.ex9.dealservice.util.ExcelStyleUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования сущностей сделок в DTO и обратно.
 *
 * <p>Содержит логику для преобразования данных сделок, сумм, контрагентов и их ролей.</p>
 *
 * @author Краковцев Артём
 */
@Component
@RequiredArgsConstructor
public class DealMapper {

    private final DealSumRepository dealSumRepository;
    private final DealContractorRepository dealContractorRepository;
    private final ContractorToRoleRepository contractorToRoleRepository;
    private final DealTypeRepository dealTypeRepository;
    private final DealStatusRepository dealStatusRepository;
    private final CurrencyRepository currencyRepository;

    private static final String DEFAULT_STATUS = "DRAFT";

    /**
     * Преобразует сущность сделки в DTO ответа.
     *
     * @param deal объект Deal
     * @return объект DealResponseDto
     */
    public DealResponseDto toDealResponseDto(Deal deal) {
        if (deal == null) {
            return null;
        }
        return DealResponseDto.builder()
                .id(deal.getId().toString())
                .description(deal.getDescription())
                .agreementNumber(deal.getAgreementNumber())
                .agreementDate(deal.getAgreementDate())
                .agreementStartDt(deal.getAgreementStartDt())
                .availabilityDate(deal.getAvailabilityDate())
                .closeDt(deal.getCloseDt())
                .type(toDealTypeDto(deal.getType()))
                .status(toDealStatusDto(deal.getStatus()))
                .sum(toDealSumDto(deal.getId()))
                .contractors(toDealContractorDtos(deal.getId()))
                .build();
    }

    /**
     * Преобразует страницу сделок в массив байт excel файла.
     *
     * @param deals страница сделок
     * @return массив байт excel файла
     */
    public byte[] toExcel(Page<Deal> deals) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
            CellStyle dealStyle = ExcelStyleUtil.createDealRowStyle(workbook);
            CellStyle sumStyle = ExcelStyleUtil.createSumRowStyle(workbook);
            CellStyle contractorStyle = ExcelStyleUtil.createContractorRowStyle(workbook);

            Row header = sheet.createRow(0);

            var headerText = List.of("ИД сделки", "Описание", "Номер договора", "Дата договора",
                    "Дата и время вступления соглашения в силу", "Срок действия сделки", "Тип сделки", "Статус сделки",
                    "Сумма сделки", "Наименование валюты", "Основная сумма сделки", "Наименование контрагента",
                    "ИНН контрагента", "Роли контрагента");

            for (int i = 0; i < headerText.size(); i++) {
                ExcelStyleUtil.createStyledCell(header, i, headerText.get(i), headerStyle);
            }

            int rowNum = 1;

            for (Deal deal : deals) {
                Row row1 = sheet.createRow(rowNum++);
                ExcelStyleUtil.createStyledCell(row1, 0, deal.getId().toString(), dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 1, deal.getDescription(), dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 2, deal.getAgreementNumber(), dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 3, deal.getAgreementDate().toString(), dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 4, deal.getAgreementStartDt().toString(), dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 5, deal.getAvailabilityDate().toString(), dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 6, deal.getType() != null ? deal.getType().getName() : "", dealStyle);
                ExcelStyleUtil.createStyledCell(row1, 7, deal.getStatus() != null ? deal.getStatus().getName() : "", dealStyle);

                var sums = dealSumRepository.findAllByDealIdAndIsActiveTrue(deal.getId());
                for (DealSum sum : sums) {
                    Row row2 = sheet.createRow(rowNum++);
                    ExcelStyleUtil.createStyledCell(row2, 8, sum.getSum().toString(), sumStyle);
                    ExcelStyleUtil.createStyledCell(row2, 9, sum.getCurrency() != null ? sum.getCurrency().getName() : "", sumStyle);
                    ExcelStyleUtil.createStyledCell(row2, 10, sum.getIsMain() ? "Да" : "Нет", sumStyle);
                }

                var contractors = dealContractorRepository.findAllByDealIdAndIsActiveTrue(deal.getId());
                for (DealContractor contractor : contractors) {
                    Row row3 = sheet.createRow(rowNum++);
                    ExcelStyleUtil.createStyledCell(row3, 11, contractor.getName(), contractorStyle);
                    ExcelStyleUtil.createStyledCell(row3, 12, contractor.getInn(), contractorStyle);
                    var roles = contractorToRoleRepository.findAllByIdContractorIdAndIsActiveTrue(contractor.getId())
                            .stream()
                            .map(ContractorToRole::getRole)
                            .filter(role -> role != null && role.getIsActive())
                            .map(ContractorRole::getName)
                            .collect(Collectors.joining(", "));
                    ExcelStyleUtil.createStyledCell(row3, 13, roles, contractorStyle);
                }
            }

            for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте Excel", e);
        }
    }

    /**
     * Создаёт новую сделку на основе входящего запроса.
     *
     * @param request данные новой сделки
     * @return объект Deal
     */
    public Deal toNewDeal(DealSaveRequestDto request) {
        var dealType = dealTypeRepository.findByIdAndIsActiveTrue(request.getTypeId())
                .orElseThrow(() -> new DealTypeNotFondException("Deal Type with id '" + request.getTypeId() + "' not found"));

        var status = dealStatusRepository.findByIdAndIsActiveIsTrue((DEFAULT_STATUS))
                .orElseThrow(() -> new DealStatusNotFondException("Deal Status with id '" + DEFAULT_STATUS + "' not found"));

        return Deal.builder()
                .description(request.getDescription())
                .agreementNumber(request.getAgreementNumber())
                .agreementDate(request.getAgreementDate())
                .agreementStartDt(request.getAgreementStartDt())
                .availabilityDate(request.getAvailabilityDate())
                .type(dealType)
                .closeDt(request.getCloseDt())
                .status(status)
                .createDate(LocalDate.now())
                .createUserId(null)
                .build();
    }

    /**
     * Обновляет существующую сделку на основе данных запроса.
     *
     * @param request DTO с новыми данными
     * @param deal    объект сделки, подлежащий обновлению
     * @return обновлённая сущность сделки
     */
    public Deal toUpdateDeal(DealSaveRequestDto request, Deal deal) {
        var dealType = dealTypeRepository.findByIdAndIsActiveTrue(request.getTypeId())
                .orElseThrow(() -> new DealTypeNotFondException("Deal Type with id '" + request.getTypeId() + "' not found"));

        deal.setDescription(request.getDescription());
        deal.setAgreementNumber(request.getAgreementNumber());
        deal.setAgreementDate(request.getAgreementDate());
        deal.setAgreementStartDt(request.getAgreementStartDt());
        deal.setAvailabilityDate(request.getAvailabilityDate());
        deal.setType(dealType);
        deal.setCloseDt(request.getCloseDt());
        deal.setModifyDate(LocalDate.now());
        deal.setModifyUserId(null);

        return deal;
    }

    /**
     * Преобразует DTO суммы в сущность DealSum.
     *
     * @param dto  DTO суммы
     * @param deal сделка, к которой относится сумма
     * @return объект DealSum
     */
    public DealSum toDealSum(DealSumDto dto, Deal deal) {
        DealSum dealSum = new DealSum();

        var currency = currencyRepository.findByIdAndIsActiveTrue(dto.getCurrency())
                .orElseThrow(() -> new CurrencyNotFondException("Currency with id '" + dto.getCurrency() + "' not found"));
        dealSum.setCurrency(currency);
        dealSum.setSum(new BigDecimal(dto.getValue()));
        dealSum.setDeal(deal);

        return dealSum;
    }

    private DealTypeDto toDealTypeDto(DealType type) {
        return type == null ? null : new DealTypeDto(type.getId(), type.getName());
    }

    private DealStatusDto toDealStatusDto(DealStatus status) {
        return status == null ? null : new DealStatusDto(status.getId(), status.getName());
    }

    private List<DealSumDto> toDealSumDto(UUID dealId) {
        return dealSumRepository.findAllByDealIdAndIsActiveTrue(dealId).stream()
                .map(sum -> new DealSumDto(sum.getSum().toString(), sum.getCurrency().getId()))
                .toList();
    }

    private List<DealContractorDto> toDealContractorDtos(UUID dealId) {
        return dealContractorRepository.findAllByDealIdAndIsActiveTrue(dealId)
                .stream()
                .map(this::toDealContractorDto)
                .toList();
    }

    private DealContractorDto toDealContractorDto(DealContractor contractor) {
        return DealContractorDto.builder()
                .id(contractor.getId().toString())
                .contractorId(contractor.getContractorId())
                .main(contractor.getMain())
                .name(contractor.getName())
                .roles(toContractorRoleDtos(contractor.getId()))
                .build();
    }

    private List<ContractorRoleDto> toContractorRoleDtos(UUID contractorId) {
        return contractorToRoleRepository.findAllByIdContractorIdAndIsActiveTrue(contractorId)
                .stream()
                .map(ContractorToRole::getRole)
                .filter(role -> role != null && role.getIsActive())
                .map(this::toContractorRoleDto)
                .toList();
    }

    private ContractorRoleDto toContractorRoleDto(ContractorRole role) {
        return new ContractorRoleDto(role.getId(), role.getName(), role.getCategory());
    }

}
