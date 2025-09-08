package org.ex9.dealservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "Status of deal DTO")
public class DealStatusDto {

    @Schema(
            description = "Unique identifier of the deal status",
            example = "DRAFT"
    )
    private String id;

    @Schema(
            description = "Name of the deal status",
            example = "Черновик"
    )
    private String name;

}
