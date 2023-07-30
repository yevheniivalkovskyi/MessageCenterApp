package com.message.center.app.service;

import com.message.center.app.dto.CreateExchangeMessageRequest;
import com.message.center.app.dto.exchange.ExchangeMessageDto;

import java.util.List;

public interface ExchangeMessageService {

  void sendMessage(CreateExchangeMessageRequest request);

  List<ExchangeMessageDto> getAll();

  ExchangeMessageDto getById(String messageId);

  List<ExchangeMessageDto> getExchangeMessagesBySender(String senderUuid);

  List<ExchangeMessageDto> getExchangeMessagesByReceiver(String receiverUuid);

}
