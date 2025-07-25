package org.ex9.dealservice.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.DealChangeStatusDto;
import org.ex9.dealservice.dto.DealResponseDto;
import org.ex9.dealservice.dto.DealSaveRequestDto;
import org.ex9.dealservice.dto.DealSearchRequestDto;
import org.ex9.dealservice.dto.ErrorResponse;
import org.ex9.dealservice.service.DealService;
import org.ex9.dealservice.util.AuthInfo;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ui/deal")
public class UiDealController {

    private final DealService dealService;

    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Save or update a deal (protected)",
            description = "Creates a new deal or updates an existing one if the ID is provided.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deal saved successfully, returns UUID of the deal",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UUID.class),
                            examples = @ExampleObject(value = "\"c9ddcc2a-d927-4904-89a0-7e666aae1644\"")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data, e.g., missing required fields or invalid format",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{" +
                                    "\"message\": \"Validation failed: description must not be null\", " +
                                    "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                    "}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Deal not found for update (DealNotFondException) or invalid deal type/currency (DealTypeNotFondException/CurrencyNotFondException)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "DealNotFound", value = "{" +
                                            "\"message\": \"Deal with id 'c9ddcc2a-d927-4904-89a0-7e666aae1644' not found\", " +
                                            "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                            "}"),
                                    @ExampleObject(name = "DealTypeNotFound", value = "{" +
                                            "\"message\": \"Deal Type with id 'TYPE_ID' not found\", " +
                                            "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                            "}"),
                                    @ExampleObject(name = "CurrencyNotFound", value = "{" +
                                            "\"message\": \"Currency with id 'USD' not found\", " +
                                            "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                            "}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/save")
    public ResponseEntity<UUID> save(@Valid @RequestBody DealSaveRequestDto request) {
        return ResponseEntity.ok(dealService.dealSave(request, AuthInfo.getUsername()));
    }

    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Change deal status (protected)",
            description = "Updates the status of an existing deal.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status changed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (missing dealId or statusId)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{" +
                                    "\"message\": \"Validation failed: dealId must not be null\", " +
                                    "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                    "}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Deal not found (DealNotFondException) or status not found (DealStatusNotFondException)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "DealNotFound", value = "{" +
                                            "\"message\": \"Deal with id '11111111-2222-3333-4444-555555555555' not found\", " +
                                            "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                            "}"),
                                    @ExampleObject(name = "StatusNotFound", value = "{" +
                                            "\"message\": \"Deal Status with id 'APPROVED' not found\", " +
                                            "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                            "}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PatchMapping("/change/status")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody DealChangeStatusDto request) {
        dealService.changeStatus(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get deal by ID (protected)",
            description = "Retrieves full deal details by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deal found"),
            @ApiResponse(responseCode = "404", description = "Deal not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DealResponseDto> getDeal(@PathVariable UUID id) {
        return ResponseEntity.ok(dealService.getDealById(id));
    }

    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER', 'CREDIT_USER', 'OVERDRAFT_USER')")
    @Operation(summary = "Search deals (protected)",
            description = "Searches active deals with filtering and pagination.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deal found, returns DealResponseDto",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DealResponseDto.class),
                            examples = @ExampleObject(value = "{" +
                                    "\"id\": \"11111111-2222-3333-4444-555555555555\", " +
                                    "\"description\": \"Test Deal\", " +
                                    "\"agreementNumber\": \"AGR-001\", " +
                                    "\"agreementDate\": \"2025-07-16\", " +
                                    "\"status\": {" +
                                    "\"id\": \"ACTIVE\", " +
                                    "\"name\": \"Active\"" +
                                    "}" +
                                    "}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format in path parameter",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"message\": \"Invalid UUID format\", \"timestamp\": \"2025-07-16T11:37:00\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Deal not found (DealNotFondException)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{" +
                                    "\"message\": \"Deal with id '11111111-2222-3333-4444-555555555555' not found\", " +
                                    "\"timestamp\": \"2025-07-16T11:37:00\"" +
                                    "}")
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/search")
    public ResponseEntity<Page<DealResponseDto>> searchDeals(@Valid @RequestBody DealSearchRequestDto request) {
        var allowedTypes = AuthInfo.getAllowedTypes(request.getTypeIds());
        request.setTypeIds(allowedTypes);
        Page<DealResponseDto> deals = dealService.searchDeals(request);
        return ResponseEntity.ok(deals);
    }

    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Export deals to Excel (protected)",
            description = "Exports filtered and sorted deals to an Excel file with pagination.")
    @ApiResponse(responseCode = "200", description = "Excel file generated successfully")
    @PostMapping(value = "/search/export")
    public ResponseEntity<byte[]> exportDealsToExcel(@Valid @RequestBody DealSearchRequestDto request) {
        var allowedTypes = AuthInfo.getAllowedTypes(request.getTypeIds());
        request.setTypeIds(allowedTypes);
        byte[] excelFile = dealService.exportDealsToExcel(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("deals_export.xlsx").build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }

}
