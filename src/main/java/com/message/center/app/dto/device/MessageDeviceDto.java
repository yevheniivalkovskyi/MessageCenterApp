package com.message.center.app.dto.device;

import com.message.center.app.domain.DeviceType;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class MessageDeviceDto {

  private String name;
  @Email
  private String email;
  private String uuid;
  private DeviceType type;
  private String groupName;

}
