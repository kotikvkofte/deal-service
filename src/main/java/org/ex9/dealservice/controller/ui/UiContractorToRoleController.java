package org.ex9.dealservice.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.dto.ErrorResponse;
import org.ex9.dealservice.service.ContractorToRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ui/contractor-to-role")
public class UiContractorToRoleController {

    private final ContractorToRoleService contractorToRoleService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Add a role to contractor (protected)", description = "Adds a new role to an existing deal contractor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Add role to contractor"),
            @ApiResponse(responseCode = "404",
                    description = "Contractor id or role id is not fount",
                    content = @Content(
                            mediaType = "application/APPLICATION_JSON_VALUE",
                            schema = @Schema(implementation = ErrorResponse.class)
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
    public ResponseEntity<Void> addRoleToContractor(@Valid @RequestBody ContractorToRoleDto request) {
        contractorToRoleService.addNewRole(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Delete role from contractor (protected)", description = "Marks contractor's role as inactive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
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
    public ResponseEntity<Void> deleteRoleToContractor(@Valid @RequestBody ContractorToRoleDto contractorToRole) {
        contractorToRoleService.deleteRole(contractorToRole);
        return ResponseEntity.noContent().build();
    }

}
