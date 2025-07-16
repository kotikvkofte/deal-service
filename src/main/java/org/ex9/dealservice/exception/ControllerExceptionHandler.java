package org.ex9.dealservice.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ex9.dealservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DealNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Deal not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{" +
                            "\"message\": \"Deal with id '11111111-2222-3333-4444-555555555555' not found\", " +
                            "\"timestamp\": \"2025-07-16T16:47:00\"" +
                            "}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DealTypeNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Deal type not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Deal Type with id 'TYPE_ID' not found\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealTypeNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CurrencyNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Currency not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Currency with id 'USD' not found\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(CurrencyNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DealStatusNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Deal status not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Deal Status with id 'APPROVED' not found\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealStatusNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DealContractorNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Deal contractor not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{" +
                            "\"message\": \"Deal contractor with id 'c9ddcc2a-d927-4904-89a0-7e666aae1644' not found\", " +
                            "\"timestamp\": \"2025-07-16T16:47:00\"" +
                            "}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(DealContractorNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ContractorRoleNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Contractor role not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Contractor role with id 'ROLE-001' not found\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(ContractorRoleNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ContractorToRoleNotFondException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(
            responseCode = "404",
            description = "Contractor to role relation not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"ContractorToRole not found\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleContractorNotFoundException(ContractorToRoleNotFondException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(
            responseCode = "500",
            description = "Unexpected server error",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Unexpected server error\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleGeneralException(Exception e) {
        return new ErrorResponse("Unexpected server error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data (missing required fields)",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Validation failed: contractorId must not be null\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = "Validation failed: " + (fieldError != null ? fieldError.getDefaultMessage() : "Invalid request");
        return new ErrorResponse(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Invalid UUID format\", \"timestamp\": \"2025-07-16T16:47:00\"}")
            )
    )
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(
            responseCode = "400",
            description = "Invalid input format",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Invalid UUID format\", \"timestamp\": \"2025-07-16T17:01:00\"}")
            )
    )
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = e.getMessage().contains("UUID") ? "Invalid UUID format" : "Invalid request format";
        return new ErrorResponse(message);
    }

}
