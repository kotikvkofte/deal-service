package org.ex9.dealservice.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ex9.dealservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DealNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Deal not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DealTypeNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Deal type not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealTypeNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CurrencyNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Currency not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(CurrencyNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DealStatusNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Deal status found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealStatusNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DealContractorNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Deal contractor found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealContractorNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ContractorRoleNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Contractor role found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(ContractorRoleNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ContractorToRoleNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Contractor to role found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(ContractorToRoleNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

}
