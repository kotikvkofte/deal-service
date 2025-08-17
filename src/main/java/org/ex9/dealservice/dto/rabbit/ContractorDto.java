package org.ex9.dealservice.dto.rabbit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContractorDto {

    private String id;

    private String name;

    private String inn;

    private String modifyUserId;

    private LocalDateTime modifyDateTime;

}
