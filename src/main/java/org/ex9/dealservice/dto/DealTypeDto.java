package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Deal Type dto")
public class DealTypeDto {

    @Schema(
            description = "Unique identifier of the deal type",
            example = "CREDIT",
            nullable = true
    )
    private String id;

    @Schema(
            description = "Name of the deal type",
            example = "Credit Agreement",
            nullable = false
    )
    @NotNull
    private String name;

}
