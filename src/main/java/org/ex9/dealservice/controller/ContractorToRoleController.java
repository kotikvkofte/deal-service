package org.ex9.dealservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.service.ContractorToRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ex9.dealservice.dto.ErrorResponse;

@RestController
@RequestMapping("contractor-to-role")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Contractor To Role Controller", description = "Endpoints for managing contractor roles in deals")
public class ContractorToRoleController {

    private final ContractorToRoleService service;

    @PostMapping("/add")
    @Operation(summary = "Add a role to contractor", description = "Adds a new role to an existing deal contractor.")

    public ResponseEntity<?> addRoleToContractor(@Valid @RequestBody ContractorToRoleDto request) {
        log.debug("Request to add role to contractor: {}", request);
        service.addNewRole(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete role from contractor", description = "Marks contractor's role as inactive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public ResponseEntity<?> deleteRoleToContractor(@Valid @RequestBody ContractorToRoleDto contractorToRole) {
        log.debug("Request to delete role from contractor: {}", contractorToRole);
        service.deleteRole(contractorToRole);
        return ResponseEntity.ok().build();
    }

}
