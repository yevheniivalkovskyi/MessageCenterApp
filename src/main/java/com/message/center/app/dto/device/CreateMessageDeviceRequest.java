package com.message.center.app.dto.device;

import com.message.center.app.domain.DeviceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMessageDeviceRequest {

  @NotBlank
  @Size(max = 128)
  private String name;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private DeviceType deviceType;

}
