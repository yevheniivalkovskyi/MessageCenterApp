package com.message.center.app.service;

import com.message.center.app.dto.device.AssignMessageDeviceRequest;
import com.message.center.app.dto.device.CreateMessageDeviceRequest;
import com.message.center.app.dto.device.MessageDeviceDto;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface MessageDeviceService {

  MessageDeviceDto getById(String uuid);

  List<MessageDeviceDto> getAll();

  MessageDeviceDto create(CreateMessageDeviceRequest requestDto);

  HttpStatus delete(String uuid);

  MessageDeviceDto assignDevice(AssignMessageDeviceRequest request, String deviceId);

}
