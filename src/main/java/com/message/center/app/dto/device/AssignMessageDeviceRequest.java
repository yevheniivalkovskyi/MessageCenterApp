package com.message.center.app.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AssignMessageDeviceRequest {

  @NotBlank
  @Size(min = 36, max = 36)
  private String deviceGroupUuid;

}
