package com.message.center.app.dto.exchange;

import com.message.center.app.domain.ExchangeMessageType;
import com.message.center.app.dto.keyword.AlertKeywordDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeMessageDto {

  private String uuid;
  private ExchangeMessageType type;
  private String content;
  private LocalDateTime time;
  private String senderEmail;
  private List<String> receiverEmails;
  private List<AlertKeywordDto> keywords;

}
