package org.ex9.dealservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Data
@Schema
public class ContractorToRoleDto {

    @Schema(
            description = "Unique identifier of the deal contractor",
            example = "80a791eb-ff29-47c1-b939-2b83397b6d1a"
    )
    @NotNull(message = "contractorId must not be null")
    private UUID contractorId;

    @Schema(
            description = "Unique identifier of the contractor role",
            example = "BORROWER"
    )
    @NotNull
    private String roleId;

}
