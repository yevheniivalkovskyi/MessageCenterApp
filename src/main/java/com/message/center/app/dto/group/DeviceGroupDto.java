package com.message.center.app.dto.group;

import com.message.center.app.dto.device.MessageDeviceDto;
import lombok.Data;

import java.util.List;

@Data
public class DeviceGroupDto {

  private String uuid;
  private String name;
  private List<MessageDeviceDto> devices;
}
