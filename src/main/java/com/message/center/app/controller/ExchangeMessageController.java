package com.message.center.app.controller;

import com.message.center.app.dto.CreateExchangeMessageRequest;
import com.message.center.app.dto.exchange.ExchangeMessageDto;
import com.message.center.app.service.ExchangeMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/exchange-messages")
public class ExchangeMessageController {

  private final ExchangeMessageService exchangeMessageService;

  @Autowired
  public ExchangeMessageController(ExchangeMessageService exchangeMessageService) {
    this.exchangeMessageService = exchangeMessageService;
  }

  @PostMapping
  public ResponseEntity<Void> createExchangeMessage(@RequestBody CreateExchangeMessageRequest request) {
    exchangeMessageService.sendMessage(request);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public List<ExchangeMessageDto> getAllExchangeMessages() {
    return exchangeMessageService.getAll();
  }

  @GetMapping("/{message-id}")
  public ExchangeMessageDto getExchangeMessageById(@PathVariable("message-id") String messageId) {
    return exchangeMessageService.getById(messageId);
  }

  @GetMapping("/by-sender/{sender-id}")
  public List<ExchangeMessageDto> getExchangeMessagesBySender(@PathVariable("sender-id") String senderId) {
    return exchangeMessageService.getExchangeMessagesBySender(senderId);
  }

  @GetMapping("/by-receiver/{receiver-id}")
  public List<ExchangeMessageDto> getExchangeMessagesByReceiver(@PathVariable("receiver-id") String receiverId) {
    return exchangeMessageService.getExchangeMessagesByReceiver(receiverId);
  }

}