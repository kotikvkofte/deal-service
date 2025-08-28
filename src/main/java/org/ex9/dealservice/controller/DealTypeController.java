package org.ex9.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.dto.DealTypeDto;
import org.ex9.dealservice.service.DealTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("deal-type")
@Tag(name = "Deal Type API", description = "Endpoints for managing deal types")
public class DealTypeController {

    private final DealTypeService dealTypeService;

    @Operation(
            summary = "Get all deal types",
            description = "Retrieve the full list of available deal types",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval",
                            content = @Content(schema = @Schema(implementation = DealTypeDto.class))
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<DealTypeDto>> getAll() {
        log.debug("get all deal-type request");
        return ResponseEntity.ok(dealTypeService.getAll());
    }

    @PutMapping("/save")
    @Operation(
            summary = "Save a deal type",
            description = "Create or update a deal type. Returns if of saved type.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully saved",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<String> save(@RequestBody @Valid DealTypeDto dealTypeDto) {
        log.debug("save deal-type request");
        return ResponseEntity.ok(dealTypeService.save(dealTypeDto));
    }

}
