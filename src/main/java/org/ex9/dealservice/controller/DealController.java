package org.ex9.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.dto.DealChangeStatusDto;
import org.ex9.dealservice.dto.DealResponseDto;
import org.ex9.dealservice.dto.DealSaveRequestDto;
import org.ex9.dealservice.dto.DealSearchRequestDto;
import org.ex9.dealservice.service.DealService;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("deal")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Deal Controller", description = "Endpoints for managing financial deals")
public class DealController {

    private final DealService service;

    @Operation(summary = "Save or update a deal",
            description = "Creates a new deal or updates an existing one if the ID is provided.")
    @ApiResponse(responseCode = "200",
            description = "Deal saved successfully")
    @PutMapping("/save")
    public ResponseEntity<UUID> save(@Valid @RequestBody DealSaveRequestDto request) {
        log.debug("Request to save Deal : {}", request);
        return ResponseEntity.ok(service.dealSave(request));
    }

    @Operation(summary = "Change deal status", description = "Updates the status of an existing deal.")
    @ApiResponse(responseCode = "200", description = "Status changed successfully")
    @PatchMapping("/change/status")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody DealChangeStatusDto request) {
        log.debug("Request to change status : {}", request);
        service.changeStatus(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get deal by ID",
            description = "Retrieves full deal details by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal found"),
            @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @GetMapping("/deal/{id}")
    public ResponseEntity<DealResponseDto> getDeal(@PathVariable String id) {
        log.debug("Request to get Deal : {}", id);
        return ResponseEntity.ok(service.getDealById(id));
    }

    @Operation(summary = "Search deals", description = "Searches active deals with filtering and pagination.")
    @ApiResponse(responseCode = "200", description = "Deals retrieved successfully")
    @PostMapping("/search")
    public ResponseEntity<Page<DealResponseDto>> searchDeals(@Valid @RequestBody DealSearchRequestDto request) {
        Page<DealResponseDto> deals = service.searchDeals(request);
        return ResponseEntity.ok(deals);
    }

    @Operation(summary = "Export deals to Excel", description = "Exports filtered and sorted deals to an Excel file with pagination.")
    @ApiResponse(responseCode = "200", description = "Excel file generated successfully")
    @PostMapping(value = "/search/export")
    public ResponseEntity<byte[]> exportDealsToExcel(@Valid @RequestBody DealSearchRequestDto request) {
        byte[] excelFile = service.exportDealsToExcel(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("deals_export.xlsx").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }

}
