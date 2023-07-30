package com.message.center.app.service.impl;

import com.message.center.app.domain.DeviceGroup;
import com.message.center.app.dto.group.CreateDeviceGroupRequest;
import com.message.center.app.dto.group.DeviceGroupDto;
import com.message.center.app.exception.ResourceNotFoundException;
import com.message.center.app.repository.DeviceGroupRepository;
import com.message.center.app.service.DeviceGroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DeviceGroupServiceImpl implements DeviceGroupService {

  private final DeviceGroupRepository repository;
  private final ModelMapper modelMapper;

  @Override
  public DeviceGroupDto getById(String uuid) {
    DeviceGroup deviceGroup = repository.findByUuid(uuid).orElseThrow(
        () -> new ResourceNotFoundException(String.format("Device group with id %s is missing.", uuid)));
    return modelMapper.map(deviceGroup, DeviceGroupDto.class);
  }

  @Override
  public List<DeviceGroupDto> getAll() {
    return repository.findAll().stream()
        .map(deviceGroup -> modelMapper.map(deviceGroup, DeviceGroupDto.class))
        .collect(Collectors.toList());
  }

  @Override
  public DeviceGroupDto create(CreateDeviceGroupRequest requestDto) {
    DeviceGroup group = new DeviceGroup();
    group.setName(requestDto.getName());
    return modelMapper.map(repository.save(group), DeviceGroupDto.class);
  }

  @Override
  public HttpStatus deleteById(String uuid) {
    DeviceGroup group = repository.deleteByUuid(uuid);
    return group.getUuid().equalsIgnoreCase(uuid) ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND;
  }
}
