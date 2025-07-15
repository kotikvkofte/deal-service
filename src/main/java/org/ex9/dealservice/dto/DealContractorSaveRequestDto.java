package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for saving deal contractor information")
public class DealContractorSaveRequestDto {

    @Schema(
            description = "ID of the deal-contractor relationship (null for new entries)",
            example = "a9a31dc0-cb82-42de-8882-b47de5af2b78",
            nullable = true
    )
    private String id;

    @Schema(
            description = "ID of the deal this contractor belongs to",
            example = "8e13d5a0-4298-49f3-a262-ea77ec628ac3",
            required = true
    )
    @NotNull
    private String dealId;

    @Schema(
            description = "ID of the contractor from the contractor service",
            example = "CTR119",
            required = true
    )
    @NotNull
    private String contractorId;

    @Schema(
            description = "Name of the contractor",
            example = "Contractor Ltd",
            required = true
    )
    private String name;

    @Schema(
            description = "INN of the contractor",
            example = "770123456789",
            nullable = true
    )
    private String inn;

    @Schema(
            description = "Whether this contractor is the main borrower in the deal",
            example = "true"
    )
    private boolean main;

    @Schema(
            description = "List of role IDs assigned to this contractor",
            example = "[\"BORROWER\", \"GUARANTOR\"]"
    )
    private List<String> roleIds;

}
