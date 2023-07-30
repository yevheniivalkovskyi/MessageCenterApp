package com.message.center.app.dto;

import com.message.center.app.domain.ExchangeMessageType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateExchangeMessageRequest {

  @NotBlank
  private String senderId;
  private String receiverId;
  @NotBlank
  private String content;
  @NotBlank
  private ExchangeMessageType messageType;

}
