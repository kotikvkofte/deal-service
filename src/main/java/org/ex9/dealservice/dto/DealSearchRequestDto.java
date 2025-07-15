package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Request DTO for searching deals with filters and pagination")
public class DealSearchRequestDto {

    @Schema(
            description = "Exact match deal ID",
            example = "8e13d5a0-4298-49f3-a262-ea77ec628ac3",
            nullable = true
    )
    private UUID dealId;

    @Schema(
            description = "Exact match deal description",
            example = "Corporate credit line",
            nullable = true
    )
    private String description;

    @Schema(
            description = "Partial match agreement number (contains search string)",
            example = "CREDIT-2023",
            nullable = true
    )
    private String agreementNumber;

    @Schema(
            description = "Agreement date range start",
            example = "2023-01-01",
            format = "date",
            nullable = true
    )
    private LocalDate agreementDateFrom;

    @Schema(
            description = "Agreement date range end",
            example = "2023-12-31",
            format = "date",
            nullable = true
    )
    private LocalDate agreementDateTo;

    @Schema(
            description = "Availability date range start",
            example = "2023-06-01",
            format = "date",
            nullable = true
    )
    private LocalDate availabilityDateFrom;

    @Schema(
            description = "Availability date range end",
            example = "2024-06-01",
            format = "date",
            nullable = true
    )
    private LocalDate availabilityDateTo;

    @Schema(
            description = "List of deal type IDs to include",
            example = "[\"CREDIT\", \"LOAN\"]",
            nullable = true
    )
    private List<String> typeIds;

    @Schema(
            description = "List of status IDs to include",
            example = "[\"ACTIVE\", \"DRAFT\"]",
            nullable = true
    )
    private List<String> statusIds;

    @Schema(
            description = "Close date/time range start",
            example = "2023-01-01T00:00:00",
            format = "date-time",
            nullable = true
    )
    private LocalDateTime closeDtFrom;

    @Schema(
            description = "Close date/time range end",
            example = "2023-12-31T23:59:59",
            format = "date-time",
            nullable = true
    )
    private LocalDateTime closeDtTo;

    @Schema(
            description = "Search string for borrower information (partial match on name/INN)",
            nullable = true
    )
    private String borrowerSearch;

    @Schema(
            description = "Search string for warranty information (partial match on name/INN)",
            nullable = true
    )
    private String warrantySearch;

    @Schema(
            description = "Sum filter criteria (value and/or currency)",
            implementation = DealSumDto.class,
            nullable = true
    )
    private DealSumDto sum;

    @Schema(
            description = "Page number (0-based)",
            example = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "page must not be null")
    @Min(value = 0, message = "page must be greater than or equal to 0")
    private int page;

    @Schema(
            description = "Number of items per page",
            example = "20",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "size must not be null")
    @Min(value = 1, message = "size must be greater than or equal to 1")
    private int size;

    @Schema(
            description = "Field to sort by",
            allowableValues = {
                    "agreementDate", "agreementNumber", "availabilityDate",
                    "closeDt", "createDate"
            },
            nullable = true
    )
    private String sortBy;

    @Schema(
            description = "Sort direction",
            allowableValues = {"ASC", "DESC"},
            nullable = true
    )
    private String sortDirection;

}
