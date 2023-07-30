package com.message.center.app.controller;

import com.message.center.app.controller.error.Error;
import com.message.center.app.controller.error.ErrorCodeEnum;
import com.message.center.app.controller.error.ErrorDetails;
import com.message.center.app.controller.error.ErrorResponse;
import com.message.center.app.exception.BadRequestException;
import com.message.center.app.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String BAD_REQUEST_ERROR = "BAD_REQUEST_ERROR";
  private static final String NOT_FOUND_ERROR = "NOT_FOUND_ERROR";
  private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
  private static final String RESOURCE_NOT_FOUND_MSG = "Resource Not Found";

  /**
   * A general exception handler that deals with runtime exception.
   *
   * @return A http 500 response
   */
  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleGeneralException(Exception ex) {
    String errorId = UUID.randomUUID().toString();

    Error error =
        Error.builder()
            .id(errorId)
            .message(ex.getMessage())
            .name(HttpStatus.INTERNAL_SERVER_ERROR.name())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
            .build();

    logError(errorId, ex, err -> log.error("Internal Server Error", err));
    ErrorResponse response = ErrorResponse.builder().error(error).build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * An exception handler to deal with general invalid argument.
   *
   * @param ex IllegalArgumentException Exception
   * @return A http 400 response
   */
  @ExceptionHandler(value = {IllegalArgumentException.class})
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    ErrorDetails detail = ErrorDetails.builder()
        .code(ErrorCodeEnum.INVALID_VALUE)
        .issue(ex.getMessage())
        .build();

    return getBadRequestErrorResponse(List.of(detail), ex);
  }

  /**
   * An exception handler to deal with general invalid state.
   *
   * @param ex IllegalArgumentException Exception
   * @return A http 400 response
   */
  @ExceptionHandler(value = {IllegalStateException.class})
  protected ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
    String errorId = UUID.randomUUID().toString();

    Error error =
        Error.builder()
            .id(errorId)
            .message(ex.getMessage())
            .name(NOT_FOUND_ERROR)
            .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
            .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
            .details(Collections.singletonList(getErrorDetails(ex, ErrorCodeEnum.UNKNOWN)))
            .build();
    logError(errorId, ex, err -> log.error(ex.getMessage(), err));

    ErrorResponse response = ErrorResponse.builder().error(error).build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * An exception handler to deal with general invalid argument.
   *
   * @param ex MethodArgumentNotValidException Exception
   * @return A http 400 response
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode httpStatusCode,
      WebRequest request) {
    List<ErrorDetails> details = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> ErrorDetails.builder()
            .field(fieldError.getField())
            .code(ErrorCodeEnum.INVALID_VALUE)
            .value(Optional.ofNullable(fieldError.getRejectedValue()).map(Object::toString).orElse(null))
            .issue(fieldError.getDefaultMessage())
            .build())
        .collect(Collectors.toList());

    return getBadRequestErrorResponse(details, ex);
  }

  /**
   * Handle ENUM conversion exception.
   *
   * @param ex Exception
   * @return A Http 400 response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleConversionFailedException(
      MethodArgumentTypeMismatchException ex) {
    ErrorDetails detail =
        ErrorDetails.builder()
            .code(ErrorCodeEnum.INVALID_VALUE)
            .issue("Request parameter has invalid type")
            .build();
    String errorId = UUID.randomUUID().toString();
    String errorMessage = String.format("Invalid %s value: %s", ex.getName(), ex.getValue());

    Error error =
        Error.builder()
            .id(errorId)
            .message(errorMessage)
            .name(BAD_REQUEST_ERROR)
            .status(HttpStatus.BAD_REQUEST.toString())
            .details(Collections.singletonList(detail))
            .build();

    logError(errorId, ex, err -> log.error(errorMessage, err));
    ErrorResponse response = ErrorResponse.builder().error(error).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handle Constraint Violation Exception.
   *
   * @param ex ConstraintViolationException
   * @return Http 400 response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {

    List<ErrorDetails> details = ex.getConstraintViolations().stream()
        .map(fieldError -> ErrorDetails.builder()
            .field(Optional.ofNullable(fieldError.getPropertyPath())
                .map(field -> field.toString().replaceAll("([A-Z][a-z]+)", "-$1").toLowerCase())
                .orElse(null))
            .code(ErrorCodeEnum.INVALID_VALUE)
            .value(Optional.ofNullable(fieldError.getInvalidValue()).map(Object::toString).orElse(null))
            .issue(processRequiredParameterSizeMessage(fieldError.getMessage()))
            .build())
        .collect(Collectors.toList());

    return getBadRequestErrorResponse(details, ex);
  }

  private ResponseEntity<Object> getBadRequestErrorResponse(List<ErrorDetails> details, Exception ex) {
    String errorId = UUID.randomUUID().toString();
    String errorMessage = "Invalid Parameter Provided";

    Error error =
        Error.builder()
            .id(errorId)
            .message(errorMessage)
            .name(VALIDATION_ERROR)
            .status(HttpStatus.BAD_REQUEST.toString())
            .details(details)
            .build();
    logError(errorId, ex, err -> log.error(errorMessage, err));

    ErrorResponse response = ErrorResponse.builder().error(error).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  private void logError(String errorId, Exception exception, Consumer<Exception> logStatement) {
    try (var mdc = MDC.putCloseable("errorId", errorId)) {
      logStatement.accept(exception);
    }
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    String errorId = UUID.randomUUID().toString();

    Error error =
        Error.builder()
            .id(errorId)
            .message(ex.getMessage())
            .name(RESOURCE_NOT_FOUND_MSG)
            .status(HttpStatus.NOT_FOUND.toString())
            .httpStatus(HttpStatus.NOT_FOUND)
            .details(Collections.singletonList(getErrorDetails(ex, ErrorCodeEnum.NOT_FOUND)))
            .build();
    logError(errorId, ex, err -> log.error(ex.getMessage(), err));

    ErrorResponse response = ErrorResponse.builder().error(error).build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
    String errorId = UUID.randomUUID().toString();

    Error error =
        Error.builder()
            .id(errorId)
            .message(ex.getMessage())
            .name(BAD_REQUEST_ERROR)
            .status(HttpStatus.BAD_REQUEST.toString())
            .httpStatus(HttpStatus.BAD_REQUEST)
            .details(Collections.singletonList(getErrorDetails(ex, ErrorCodeEnum.BAD_REQUEST, ex.getCause())))
            .build();
    logError(errorId, ex, err -> log.error(ex.getMessage(), err));

    ErrorResponse response = ErrorResponse.builder().error(error).build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode httpStatus,
      WebRequest request) {

    ErrorDetails build = ErrorDetails.builder()
        .field(ex.getParameterName())
        .code(ErrorCodeEnum.BAD_REQUEST)
        .value(null)
        .issue(ex.getMessage())
        .build();
    List<ErrorDetails> errorDetails = Collections.singletonList(build);

    return getBadRequestErrorResponse(errorDetails, ex);
  }

  private ErrorDetails getErrorDetails(RuntimeException ex, ErrorCodeEnum errorCode) {
    return getErrorDetails(errorCode, ex.getMessage());
  }

  private ErrorDetails getErrorDetails(RuntimeException ex, ErrorCodeEnum errorCode, Throwable throwable) {
    return getErrorDetails(errorCode, ex.getMessage(), throwable.getMessage());
  }

  private ErrorDetails getErrorDetails(ErrorCodeEnum errorCode, String message, String fieldMessage) {
    return ErrorDetails.builder()
        .code(errorCode)
        .field(fieldMessage)
        .issue(message)
        .build();
  }

  private ErrorDetails getErrorDetails(ErrorCodeEnum errorCode, String message) {
    return ErrorDetails.builder()
        .code(errorCode)
        .issue(message)
        .build();
  }

  private String processRequiredParameterSizeMessage(String message) {
    return message.contains("size must be between 36 and 36") ? "" : message;
  }
}