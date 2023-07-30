package com.message.center.app.controller.error;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Valid
@Data
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"code", "field", "value", "issue", "expectedValue"})
public class ErrorDetails {
  private ErrorCodeEnum code;
  private String field;
  private String value;
  private String issue;
  private String expectedValue;
}
