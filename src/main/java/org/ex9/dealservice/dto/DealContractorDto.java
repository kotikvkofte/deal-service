package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Deal Contractor information DTO")
public class DealContractorDto {

    @Schema(
            description = "Unique identifier of the deal contractor relationship",
            example = "a9a31dc0-cb82-42de-8882-b47de5af2b78"
    )
    private String id;

    @Schema(
            description = "Contractor ID from the contractor service",
            example = "Contractor89"
    )
    private String contractorId;

    @Schema(
            description = "Name of the contractor",
            example = "Konstantinopolsky Ltd"
    )
    private String name;

    @Schema(
            description = "Flag indicating if this is the main contractor",
            example = "true"
    )
    private boolean main;

    @Schema(
            description = "List of roles assigned to this contractor in the deal",
            implementation = ContractorRoleDto.class
    )
    private List<ContractorRoleDto> roles;

}
