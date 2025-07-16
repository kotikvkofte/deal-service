package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for changing deal status")
public class DealChangeStatusDto {

    @Schema(
            description = "Unique identifier of the deal",
            example = "8e13d5a0-4298-49f3-a262-ea77ec628ac3"
    )
    @NotNull(message = "dealId must not be null")
    private UUID dealId;

    @Schema(
            description = "New status identifier for the deal",
            example = "ACTIVE",
            allowableValues = {"DRAFT", "ACTIVE", "CLOSED", "CANCELLED"}
    )
    @NotNull(message = "statusId must not be null")
    private String statusId;

}
