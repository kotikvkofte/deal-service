package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for saving deal information")
public class DealSaveRequestDto {

    @Schema(
            description = "ID of the deal (required for update, null for create)",
            example = "8e13d5a0-4298-49f3-a262-ea77ec628ac3",
            nullable = true
    )
    private UUID id;

    @Schema(
            description = "Description of the deal"
    )
    @NotNull(message = "description must not be null")
    private String description;

    @Schema(
            description = "Agreement number",
            example = "CREDIT-2023-001"
    )
    private String agreementNumber;

    @Schema(
            description = "Date when agreement was signed (format: yyyy-MM-dd)",
            example = "2023-01-15",
            format = "date"
    )
    private LocalDate agreementDate;

    @Schema(
            description = "Date and time when agreement comes into effect (format: yyyy-MM-dd'T'HH:mm:ss)",
            example = "2023-01-16T10:00:00",
            format = "date-time"
    )
    private LocalDateTime agreementStartDt;

    @Schema(
            description = "Date when deal becomes available (format: yyyy-MM-dd)",
            example = "2023-01-20",
            format = "date"
    )
    private LocalDate availabilityDate;

    @Schema(
            description = "Type identifier of the deal",
            example = "CREDIT",
            allowableValues = {"CREDIT", "LOAN", "LEASE", "DERIVATIVE"}
    )
    private String typeId;

    @Schema(
            description = "Deal amount information",
            implementation = DealSumDto.class
    )
    @Valid
    private DealSumDto sum;

    @Schema(
            description = "Date and time when deal was closed (null if still active)",
            example = "2024-01-15T15:30:00",
            format = "date-time",
            nullable = true
    )
    private LocalDateTime closeDt;

}
