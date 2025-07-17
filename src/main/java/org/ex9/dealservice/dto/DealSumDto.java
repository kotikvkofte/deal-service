package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^\\d+\\.\\d{2}$",
            message = "value must be a valid decimal number with two decimal places (100000.00)"
    )
    private String value;

    @Schema(
            description = "Currency code",
            example = "RUB"
    )
    @NotNull(message = "currency must not be null")
    private String currency;

}
