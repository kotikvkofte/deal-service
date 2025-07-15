package org.ex9.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.service.DealContractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/deal-contractor")
@RequiredArgsConstructor
@Tag(name = "Deal Contractor Controller", description = "Endpoints for managing contractors in deals")
public class DealContractorController {

    private final DealContractorService service;

    @Operation(summary = "Save or update deal contractor", description = "Creates a new deal contractor or updates an existing one.")
    @ApiResponse(responseCode = "200", description = "Contractor saved successfully")
    @PutMapping("/save")
    public ResponseEntity<UUID> saveContractor(@Valid @RequestBody DealContractorSaveRequestDto request) {
        return ResponseEntity.ok(service.saveDealContractor(request));
    }

    @Operation(summary = "Logically delete a deal contractor", description = "Marks a deal contractor as inactive by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Contractor marked as inactive"),
            @ApiResponse(responseCode = "404", description = "Contractor not found")
    })
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteContractor(@PathVariable UUID id) {
        service.deleteDealContractor(id);
        return ResponseEntity.accepted().build();
    }

}
