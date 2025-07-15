package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Deal amount information with currency")
public class DealSumDto {

    @Schema(
            description = "Numeric value of the amount",
            example = "100000.00"
    )
    private String value;

    @Schema(
            description = "Currency code",
            example = "RUB"
    )
    private String currency;

}
