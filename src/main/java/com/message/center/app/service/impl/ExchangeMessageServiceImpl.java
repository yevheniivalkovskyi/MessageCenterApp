package com.message.center.app.service.impl;

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
import com.message.center.app.service.ExchangeMessageService;
import com.message.center.app.service.KeywordLoggingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExchangeMessageServiceImpl implements ExchangeMessageService {

  private final ExchangeMessageRepository exchangeMessageRepository;
  private final MessageDeviceRepository messageDeviceRepository;
  private final DeviceGroupRepository deviceGroupRepository;
  private final KeywordLoggingService keywordLoggingService;
  private final ModelMapper modelMapper;

  @Autowired
  public ExchangeMessageServiceImpl(ExchangeMessageRepository exchangeMessageRepository,
                                    MessageDeviceRepository messageDeviceRepository,
                                    DeviceGroupRepository deviceGroupRepository,
                                    KeywordLoggingService keywordLoggingService, ModelMapper modelMapper) {
    this.exchangeMessageRepository = exchangeMessageRepository;
    this.messageDeviceRepository = messageDeviceRepository;
    this.deviceGroupRepository = deviceGroupRepository;
    this.keywordLoggingService = keywordLoggingService;
    this.modelMapper = modelMapper;
  }

  public void sendMessage(CreateExchangeMessageRequest request) {
    ExchangeMessage exchangeMessage = new ExchangeMessage();
    exchangeMessage.setSender(extractDeviceById(request.getSenderId()));
    exchangeMessage.setReceivers(determineReceivers(request.getReceiverId(), request.getMessageType()));
    exchangeMessage.setContent(request.getContent());
    exchangeMessage.setTimestamp(LocalDateTime.now());
    exchangeMessage.setMessageType(request.getMessageType());
    keywordLoggingService.logExchangeMessage(exchangeMessage);
    exchangeMessageRepository.save(exchangeMessage);
  }

  private List<MessageDevice> determineReceivers(String receiverId, ExchangeMessageType messageType) {
    List<MessageDevice> receivers = new ArrayList<>();
    switch (messageType) {
      case P2P -> receivers.add(extractDeviceById(receiverId));
      case GROUP -> receivers.addAll(extractGroupById(receiverId).getDevices());
      case BROADCAST -> receivers.addAll(messageDeviceRepository.findAll());
    }
    return receivers;
  }

  public List<ExchangeMessageDto> getAll() {
    return exchangeMessageRepository.findAll().stream()
        .map(convertToDto())
        .collect(Collectors.toList());
  }

  public ExchangeMessageDto getById(String messageId) {
    ExchangeMessage exchangeMessage = exchangeMessageRepository.findByUuid(messageId)
        .orElseThrow(() -> new ResourceNotFoundException("ExchangeMessage not found with id: " + messageId));
    return modelMapper.map(exchangeMessage, ExchangeMessageDto.class);
  }

  public List<ExchangeMessageDto> getExchangeMessagesBySender(String senderUuid) {
    return exchangeMessageRepository.findBySenderUuid(senderUuid).stream()
        .map(exchangeMessage -> modelMapper.map(exchangeMessage, ExchangeMessageDto.class))
        .collect(Collectors.toList());
  }

  public List<ExchangeMessageDto> getExchangeMessagesByReceiver(String receiverUuid) {
    return exchangeMessageRepository.findByReceiversContaining(receiverUuid).stream()
        .map(exchangeMessage -> modelMapper.map(exchangeMessage, ExchangeMessageDto.class))
        .collect(Collectors.toList());
  }

  private MessageDevice extractDeviceById(String deviceId) {
    return messageDeviceRepository.findByUuid(deviceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                String.format("Device with id %s is not found.", deviceId)));
  }

  private DeviceGroup extractGroupById(String deviceId) {
    return deviceGroupRepository.findByUuid(deviceId)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                String.format("Device with id %s is not found.", deviceId)));
  }

  private Function<ExchangeMessage, ExchangeMessageDto> convertToDto() {
    return exchangeMessage -> {
      ExchangeMessageDto dto = modelMapper.map(exchangeMessage, ExchangeMessageDto.class);
      List<String> receiverEmails = exchangeMessage.getReceivers().stream().map(MessageDevice::getEmail)
          .collect(Collectors.toList());
      dto.setReceiverEmails(receiverEmails);
      return dto;
    };
  }
}
