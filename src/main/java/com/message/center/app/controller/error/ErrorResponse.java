package com.message.center.app.controller.error;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Valid
@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
  private Error error;
}
