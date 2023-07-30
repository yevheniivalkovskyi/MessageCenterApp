package com.message.center.app.service.impl;

import com.message.center.app.domain.DeviceGroup;
import com.message.center.app.domain.MessageDevice;
import com.message.center.app.dto.device.AssignMessageDeviceRequest;
import com.message.center.app.dto.device.CreateMessageDeviceRequest;
import com.message.center.app.dto.device.MessageDeviceDto;
import com.message.center.app.exception.ResourceNotFoundException;
import com.message.center.app.repository.DeviceGroupRepository;
import com.message.center.app.repository.MessageDeviceRepository;
import com.message.center.app.service.MessageDeviceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MessageDeviceServiceImpl implements MessageDeviceService {

  private final ModelMapper modelMapper;
  private final MessageDeviceRepository repository;
  private final DeviceGroupRepository deviceGroupRepository;

  @Override
  public MessageDeviceDto getById(String uuid) {
    MessageDevice messageDevice = repository.findByUuid(uuid).orElseThrow(
        () -> new ResourceNotFoundException(String.format("Message device with id %s is missing.", uuid)));
    return modelMapper.map(messageDevice, MessageDeviceDto.class);
  }

  @Override
  public List<MessageDeviceDto> getAll() {
    List<MessageDevice> messageDevices = repository.findAll();
    return messageDevices.stream()
        .map(device -> modelMapper.map(device, MessageDeviceDto.class))
        .collect(Collectors.toList());
  }

  @Override
  public MessageDeviceDto create(CreateMessageDeviceRequest requestDto) {
    MessageDevice device = new MessageDevice();
    device.setDeviceName(requestDto.getName());
    device.setEmail(requestDto.getEmail());
    device.setDeviceType(requestDto.getDeviceType());
    MessageDevice savedDevice = repository.save(device);
    return modelMapper.map(savedDevice, MessageDeviceDto.class);
  }

  @Override
  @Transactional
  public HttpStatus delete(String uuid) {
    Optional<MessageDevice> device = repository.findByUuid(uuid);
    if (device.isEmpty()) {
      return HttpStatus.NOT_FOUND;
    }
    return repository.deleteByUuid(uuid) ? HttpStatus.NO_CONTENT : HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public MessageDeviceDto assignDevice(AssignMessageDeviceRequest request, String deviceId) {
    MessageDevice device = repository.findByUuid(deviceId).orElseThrow(
        () -> new ResourceNotFoundException(String.format("Message device with id %s is missing.", deviceId)));
    DeviceGroup deviceGroup = deviceGroupRepository.findByUuid(request.getDeviceGroupUuid())
        .orElseThrow(
            () -> new ResourceNotFoundException(
                String.format("Device group with id %s is missing.", request.getDeviceGroupUuid())));

    device.setDeviceGroup(deviceGroup);
    repository.save(device);
    return modelMapper.map(device, MessageDeviceDto.class);
  }
}
