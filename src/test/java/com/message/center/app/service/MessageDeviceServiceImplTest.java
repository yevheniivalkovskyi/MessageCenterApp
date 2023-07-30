package com.message.center.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.message.center.app.domain.DeviceGroup;
import com.message.center.app.domain.DeviceType;
import com.message.center.app.domain.MessageDevice;
import com.message.center.app.dto.device.AssignMessageDeviceRequest;
import com.message.center.app.dto.device.CreateMessageDeviceRequest;
import com.message.center.app.dto.device.MessageDeviceDto;
import com.message.center.app.exception.ResourceNotFoundException;
import com.message.center.app.repository.DeviceGroupRepository;
import com.message.center.app.repository.MessageDeviceRepository;
import com.message.center.app.service.impl.MessageDeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MessageDeviceServiceImplTest {

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private MessageDeviceRepository messageDeviceRepository;

  @Mock
  private DeviceGroupRepository deviceGroupRepository;

  @InjectMocks
  private MessageDeviceServiceImpl messageDeviceService;

  private MessageDevice device;
  private DeviceGroup deviceGroup;
  private CreateMessageDeviceRequest createRequest;
  private AssignMessageDeviceRequest assignRequest;

  @BeforeEach
  public void setup() {
    device = new MessageDevice();
    device.setUuid("deviceUuid");
    device.setDeviceName("Test Device");
    device.setEmail("test@example.com");
    device.setDeviceType(DeviceType.MOBILE);

    deviceGroup = new DeviceGroup();
    deviceGroup.setUuid("groupUuid");
    deviceGroup.setName("Test Group");

    createRequest = new CreateMessageDeviceRequest();
    createRequest.setName("Test Device");
    createRequest.setEmail("test@example.com");
    createRequest.setDeviceType(DeviceType.MOBILE);

    assignRequest = new AssignMessageDeviceRequest();
    assignRequest.setDeviceGroupUuid("groupUuid");
  }

  @Test
  public void testGetById_ExistingDevice_ReturnsDto() {

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.of(device));
    when(modelMapper.map(device, MessageDeviceDto.class)).thenReturn(new MessageDeviceDto());

    MessageDeviceDto result = messageDeviceService.getById("deviceUuid");

    assertNotNull(result);
    verify(messageDeviceRepository).findByUuid("deviceUuid");
    verify(modelMapper).map(device, MessageDeviceDto.class);
  }

  @Test
  public void testGetById_NonExistingDevice_ThrowsResourceNotFoundException() {

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> messageDeviceService.getById("deviceUuid"));
    verify(messageDeviceRepository).findByUuid("deviceUuid");
  }

  @Test
  public void testGetAll_ReturnsListOfDtos() {

    when(messageDeviceRepository.findAll()).thenReturn(List.of(device));
    when(modelMapper.map(device, MessageDeviceDto.class)).thenReturn(new MessageDeviceDto());

    List<MessageDeviceDto> result = messageDeviceService.getAll();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    verify(messageDeviceRepository).findAll();
    verify(modelMapper).map(device, MessageDeviceDto.class);
  }

  @Test
  public void testCreate_ValidRequest_ReturnsCreatedDto() {

    when(messageDeviceRepository.save(any(MessageDevice.class))).thenReturn(device);
    when(modelMapper.map(device, MessageDeviceDto.class)).thenReturn(new MessageDeviceDto());

    MessageDeviceDto result = messageDeviceService.create(createRequest);

    assertNotNull(result);
    verify(messageDeviceRepository).save(any(MessageDevice.class));
    verify(modelMapper).map(device, MessageDeviceDto.class);
  }

  @Test
  public void testDelete_ExistingDevice_ReturnsNoContent() {

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.of(device));
    when(messageDeviceRepository.deleteByUuid("deviceUuid")).thenReturn(true);

    HttpStatus result = messageDeviceService.delete("deviceUuid");

    assertEquals(HttpStatus.NO_CONTENT, result);
    verify(messageDeviceRepository).findByUuid("deviceUuid");
    verify(messageDeviceRepository).deleteByUuid("deviceUuid");
  }

  @Test
  public void testDelete_NonExistingDevice_ReturnsNotFound() {

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.empty());

    HttpStatus result = messageDeviceService.delete("deviceUuid");

    assertEquals(HttpStatus.NOT_FOUND, result);
    verify(messageDeviceRepository).findByUuid("deviceUuid");
    verifyNoMoreInteractions(messageDeviceRepository);
  }

  @Test
  public void testAssignDevice_ExistingDeviceAndGroup_ReturnsDtoWithAssignedGroup() {
    MessageDeviceDto deviceDto = new MessageDeviceDto();
    deviceDto.setGroupName(deviceGroup.getName());

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.of(device));
    when(deviceGroupRepository.findByUuid("groupUuid")).thenReturn(Optional.of(deviceGroup));
    when(messageDeviceRepository.save(any(MessageDevice.class))).thenReturn(device);
    when(modelMapper.map(device, MessageDeviceDto.class)).thenReturn(deviceDto);

    MessageDeviceDto result = messageDeviceService.assignDevice(assignRequest, "deviceUuid");

    assertNotNull(result);
    assertNotNull(result.getGroupName());
    assertEquals("Test Group", result.getGroupName());
    verify(messageDeviceRepository).findByUuid("deviceUuid");
    verify(deviceGroupRepository).findByUuid("groupUuid");
    verify(messageDeviceRepository).save(any(MessageDevice.class));
    verify(modelMapper).map(device, MessageDeviceDto.class);
  }

  @Test
  public void testAssignDevice_NonExistingDevice_ThrowsResourceNotFoundException() {

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> messageDeviceService.assignDevice(assignRequest, "deviceUuid"));
    verify(messageDeviceRepository).findByUuid("deviceUuid");
    verifyNoMoreInteractions(deviceGroupRepository, messageDeviceRepository, modelMapper);
  }

  @Test
  public void testAssignDevice_NonExistingGroup_ThrowsResourceNotFoundException() {

    when(messageDeviceRepository.findByUuid("deviceUuid")).thenReturn(Optional.of(device));
    when(deviceGroupRepository.findByUuid("groupUuid")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> messageDeviceService.assignDevice(assignRequest, "deviceUuid"));
    verify(messageDeviceRepository).findByUuid("deviceUuid");
    verify(deviceGroupRepository).findByUuid("groupUuid");
    verifyNoMoreInteractions(messageDeviceRepository, modelMapper);
  }
}