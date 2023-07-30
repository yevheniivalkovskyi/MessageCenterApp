package com.message.center.app.service;

import com.message.center.app.dto.group.CreateDeviceGroupRequest;
import com.message.center.app.dto.group.DeviceGroupDto;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface DeviceGroupService {

  DeviceGroupDto getById(String uuid);

  List<DeviceGroupDto> getAll();

  DeviceGroupDto create(CreateDeviceGroupRequest requestDto);

  HttpStatus deleteById(String uuid);
}
