package org.ex9.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.dto.ErrorResponse;
import org.ex9.dealservice.service.DealContractorService;
import org.springframework.http.MediaType;
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

    private final DealContractorService dealContractorService;

    @Operation(summary = "Save or update deal contractor", description = "Creates a new deal contractor or updates an existing one.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contractor saved successfully, returns UUID of the contractor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UUID.class),
                            examples = @ExampleObject(value = "\"c9ddcc2a-d927-4904-89a0-7e666aae1644\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (missing required fields or invalid format)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Validation failed: dealId must not be null\", \"timestamp\": \"2025-07-16T15:56:00\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contractor not found for update (DealContractorNotFondException) or deal not found (DealNotFondException)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "ContractorNotFound", value = "{" +
                                            "\"message\": \"Contractor with id 'c9ddcc2a-d927-4904-89a0-7e666aae1644' not found\", " +
                                            "\"timestamp\": \"2025-07-16T15:56:00\"" +
                                            "}"),
                                    @ExampleObject(name = "DealNotFound", value = "{" +
                                            "\"message\": \"Deal with id '11111111-2222-3333-4444-555555555555' not found\", " +
                                            "\"timestamp\": \"2025-07-16T15:56:00\"" +
                                            "}")
                            }
                    )
            )
    })
    @PutMapping("/save")
    public ResponseEntity<UUID> saveContractor(@Valid @RequestBody DealContractorSaveRequestDto request) {
        return ResponseEntity.ok(dealContractorService.saveDealContractor(request));
    }

    @Operation(summary = "Logically delete a deal contractor", description = "Marks a deal contractor as inactive by ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Contractor marked as inactive",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format in path parameter",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Invalid UUID format\", \"timestamp\": \"2025-07-16T15:56:00\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contractor not found or not active (DealContractorNotFondException)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{" +
                                    "\"message\": \"Deal contractor with id 'c9ddcc2a-d927-4904-89a0-7e666aae1644' not found\", " +
                                    "\"timestamp\": \"2025-07-16T15:56:00\"" +
                                    "}")
                    )
            )
    })
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteContractor(@PathVariable UUID id) {
        dealContractorService.deleteDealContractor(id);
        return ResponseEntity.noContent().build();
    }

}
