package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed deal information response")
public class DealResponseDto {

    @Schema(
            description = "Unique identifier of the deal",
            example = "8e13d5a0-4298-49f3-a262-ea77ec628ac3"
    )
    private UUID id;

    @Schema(
            description = "Description of the deal"
    )
    private String description;

    @Schema(
            description = "Contract/agreement number",
            example = "1000-2023-001"
    )
    private String agreementNumber;

    @Schema(
            description = "Date when agreement was signed",
            example = "2023-01-15",
            format = "date"
    )
    private LocalDate agreementDate;

    @Schema(
            description = "Date and time when agreement comes into effect",
            example = "2023-01-16T10:00:00",
            format = "date-time"
    )
    private LocalDateTime agreementStartDt;

    @Schema(
            description = "Date when deal becomes available/active",
            example = "2023-01-20",
            format = "date"
    )
    private LocalDate availabilityDate;

    @Schema(
            description = "Type of the deal",
            implementation = DealTypeDto.class
    )
    private DealTypeDto type;

    @Schema(
            description = "Current status of the deal",
            implementation = DealStatusDto.class
    )
    private DealStatusDto status;

    @Schema(
            description = "List of deal amounts in different currencies",
            implementation = DealSumDto.class
    )
    private List<DealSumDto> sum;

    @Schema(
            description = "Date and time when deal was closed (null if still active)",
            example = "2024-01-15T15:30:00",
            format = "date-time",
            nullable = true
    )
    private LocalDateTime closeDt;

    @Schema(
            description = "List of contractors associated with the deal",
            implementation = DealContractorDto.class
    )
    private List<DealContractorDto> contractors;

}
