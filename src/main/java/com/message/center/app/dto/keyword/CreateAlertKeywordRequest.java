package com.message.center.app.dto.keyword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAlertKeywordRequest {

  @NotBlank
  @Size(max = 128)
  private String name;
}
