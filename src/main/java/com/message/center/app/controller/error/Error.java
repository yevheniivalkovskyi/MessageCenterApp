package com.message.center.app.controller.error;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Valid
@Data
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"id", "status", "name", "message", "details"})
public class Error {
  private String id;
  private String status;
  private String name;
  private String message;
  private List<ErrorDetails> details;
  private HttpStatus httpStatus;
}
