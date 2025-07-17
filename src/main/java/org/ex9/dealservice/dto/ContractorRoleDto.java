package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contractor Role DTO")
public class ContractorRoleDto {

    @Schema(
            description = "Unique identifier of the contractor role",
            example = "BORROWER"
    )
    private String id;

    @Schema(
            description = "Name of the contractor role",
            example = "Заемщик"
    )
    private String name;

    @Schema(
            description = "Category of the contractor role",
            example = "BORROWER",
            allowableValues = {"BORROWER", "WARRANTY"}
    )
    private String category;

}
