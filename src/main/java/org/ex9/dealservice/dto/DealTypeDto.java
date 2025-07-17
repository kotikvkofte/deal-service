package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Deal Type dto")
public class DealTypeDto {

    @Schema(
            description = "Unique identifier of the deal type",
            example = "CREDIT"
    )
    private String id;

    @Schema(
            description = "Name of the deal type",
            example = "Credit Agreement"
    )
    private String name;

}
