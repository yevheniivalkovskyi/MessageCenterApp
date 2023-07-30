package com.message.center.app.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDeviceGroupRequest {

  @NotBlank
  @Size(max = 128)
  private String name;
}
