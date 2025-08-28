package org.ex9.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.dto.DealStatusDto;
import org.ex9.dealservice.service.DealStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("deal-status")
@Tag(name = "Deal Status API", description = "Endpoints for retrieving deal statuses")
public class DealStatusController {

    private final DealStatusService dealStatusService;

    @GetMapping("/all")
    @Operation(
            summary = "Get all deal statuses",
            description = "Retrieve the full list of available deal statuses",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(
                                    schema = @Schema(implementation = DealStatusDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<List<DealStatusDto>> getAll() {
        log.debug("get all deal-status request");
        return ResponseEntity.ok(dealStatusService.getAll());
    }

}
