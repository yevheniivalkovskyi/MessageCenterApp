package com.message.center.app.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.message.center.app.domain.DeviceGroup;
import com.message.center.app.domain.ExchangeMessage;
import com.message.center.app.domain.ExchangeMessageType;
import com.message.center.app.domain.MessageDevice;
import com.message.center.app.dto.CreateExchangeMessageRequest;
import com.message.center.app.dto.exchange.ExchangeMessageDto;
import com.message.center.app.exception.ResourceNotFoundException;
import com.message.center.app.repository.DeviceGroupRepository;
import com.message.center.app.repository.ExchangeMessageRepository;
import com.message.center.app.repository.MessageDeviceRepository;
import com.message.center.app.service.impl.ExchangeMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ExchangeMessageServiceImplTest {

  @Mock
  private ExchangeMessageRepository exchangeMessageRepository;

  @Mock
  private MessageDeviceRepository messageDeviceRepository;

  @Mock
  private DeviceGroupRepository deviceGroupRepository;

  @Mock
  private KeywordLoggingService keywordLoggingService;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private ExchangeMessageServiceImpl exchangeMessageService;

  // Test data
  private CreateExchangeMessageRequest createRequest;
  private MessageDevice senderDevice;
  private MessageDevice receiverDevice;
  private DeviceGroup deviceGroup;
  private ExchangeMessage exchangeMessage;

  @BeforeEach
  public void setup() {
    createRequest = new CreateExchangeMessageRequest();
    createRequest.setSenderId("senderUuid");
    createRequest.setReceiverId("receiverUuid");
    createRequest.setMessageType(ExchangeMessageType.P2P);
    createRequest.setContent("Test message content");

    senderDevice = new MessageDevice();
    senderDevice.setUuid("senderUuid");
    senderDevice.setDeviceName("Sender Device");
    senderDevice.setEmail("sender@example.com");

    receiverDevice = new MessageDevice();
    receiverDevice.setUuid("receiverUuid");
    receiverDevice.setDeviceName("Receiver Device");
    receiverDevice.setEmail("receiver@example.com");

    deviceGroup = new DeviceGroup();
    deviceGroup.setUuid("groupUuid");
    deviceGroup.setName("Test Group");
    deviceGroup.setDevices(List.of(receiverDevice));

    exchangeMessage = new ExchangeMessage();
    exchangeMessage.setUuid("messageUuid");
    exchangeMessage.setSender(senderDevice);
    exchangeMessage.setReceivers(List.of(receiverDevice));
    exchangeMessage.setContent("Test message content");
    exchangeMessage.setTimestamp(LocalDateTime.now());
    exchangeMessage.setMessageType(ExchangeMessageType.P2P);
  }

  @Test
  public void testSendMessage_ValidRequest_ExchangeMessageSaved() {
    // Arrange
    when(messageDeviceRepository.findByUuid("senderUuid")).thenReturn(Optional.of(senderDevice));
    when(messageDeviceRepository.findByUuid("receiverUuid")).thenReturn(Optional.of(receiverDevice));
    when(exchangeMessageRepository.save(any(ExchangeMessage.class))).thenReturn(exchangeMessage);

    // Act
    exchangeMessageService.sendMessage(createRequest);

    // Assert
    verify(messageDeviceRepository).findByUuid("senderUuid");
    verify(messageDeviceRepository).findByUuid("receiverUuid");
    verify(exchangeMessageRepository).save(any(ExchangeMessage.class));
    verify(keywordLoggingService).logExchangeMessage(any(ExchangeMessage.class));
  }

  @Test
  public void testSendMessage_InvalidSender_ThrowsResourceNotFoundException() {
    // Arrange
    when(messageDeviceRepository.findByUuid("senderUuid")).thenReturn(Optional.empty());

    // Act and Assert
    assertThrows(ResourceNotFoundException.class, () -> exchangeMessageService.sendMessage(createRequest));
    verify(messageDeviceRepository).findByUuid("senderUuid");
    verifyNoMoreInteractions(messageDeviceRepository, exchangeMessageRepository, keywordLoggingService);
  }

  @Test
  public void testSendMessage_InvalidReceiver_ThrowsResourceNotFoundException() {
    // Arrange
    when(messageDeviceRepository.findByUuid("senderUuid")).thenReturn(Optional.of(senderDevice));
    when(messageDeviceRepository.findByUuid("receiverUuid")).thenReturn(Optional.empty());

    // Act and Assert
    assertThrows(ResourceNotFoundException.class, () -> exchangeMessageService.sendMessage(createRequest));
    verify(messageDeviceRepository).findByUuid("senderUuid");
    verify(messageDeviceRepository).findByUuid("receiverUuid");
    verifyNoMoreInteractions(messageDeviceRepository, exchangeMessageRepository, keywordLoggingService);
  }

  @Test
  public void testGetAll_ReturnsListOfDtos() {
    // Arrange
    List<ExchangeMessage> exchangeMessages = List.of(exchangeMessage);
    when(exchangeMessageRepository.findAll()).thenReturn(exchangeMessages);
    when(modelMapper.map(any(ExchangeMessage.class), eq(ExchangeMessageDto.class))).thenReturn(
        new ExchangeMessageDto());

    // Act
    List<ExchangeMessageDto> result = exchangeMessageService.getAll();

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    verify(exchangeMessageRepository).findAll();
    verify(modelMapper).map(any(ExchangeMessage.class), eq(ExchangeMessageDto.class));
  }

  @Test
  public void testGetById_ExistingMessage_ReturnsDto() {
    // Arrange
    when(exchangeMessageRepository.findByUuid("messageUuid")).thenReturn(Optional.of(exchangeMessage));
    when(modelMapper.map(exchangeMessage, ExchangeMessageDto.class)).thenReturn(new ExchangeMessageDto());

    // Act
    ExchangeMessageDto result = exchangeMessageService.getById("messageUuid");

    // Assert
    assertNotNull(result);
    verify(exchangeMessageRepository).findByUuid("messageUuid");
    verify(modelMapper).map(exchangeMessage, ExchangeMessageDto.class);
  }

  @Test
  public void testGetById_NonExistingMessage_ThrowsResourceNotFoundException() {
    // Arrange
    when(exchangeMessageRepository.findByUuid("messageUuid")).thenReturn(Optional.empty());

    // Act and Assert
    assertThrows(ResourceNotFoundException.class, () -> exchangeMessageService.getById("messageUuid"));
    verify(exchangeMessageRepository).findByUuid("messageUuid");
  }

  @Test
  public void testGetExchangeMessagesBySender_ReturnsListOfDtos() {
    // Arrange
    List<ExchangeMessage> exchangeMessages = List.of(exchangeMessage);
    when(exchangeMessageRepository.findBySenderUuid("senderUuid")).thenReturn(exchangeMessages);
    when(modelMapper.map(any(ExchangeMessage.class), eq(ExchangeMessageDto.class))).thenReturn(
        new ExchangeMessageDto());

    // Act
    List<ExchangeMessageDto> result = exchangeMessageService.getExchangeMessagesBySender("senderUuid");

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    verify(exchangeMessageRepository).findBySenderUuid("senderUuid");
    verify(modelMapper).map(any(ExchangeMessage.class), eq(ExchangeMessageDto.class));
  }

  @Test
  public void testGetExchangeMessagesByReceiver_ReturnsListOfDtos() {
    // Arrange
    List<ExchangeMessage> exchangeMessages = List.of(exchangeMessage);
    when(exchangeMessageRepository.findByReceiversContaining("receiverUuid")).thenReturn(exchangeMessages);
    when(modelMapper.map(any(ExchangeMessage.class), eq(ExchangeMessageDto.class))).thenReturn(
        new ExchangeMessageDto());

    // Act
    List<ExchangeMessageDto> result = exchangeMessageService.getExchangeMessagesByReceiver("receiverUuid");

    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
    verify(exchangeMessageRepository).findByReceiversContaining("receiverUuid");
    verify(modelMapper).map(any(ExchangeMessage.class), eq(ExchangeMessageDto.class));
  }
}