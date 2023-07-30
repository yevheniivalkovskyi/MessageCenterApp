package com.message.center.app.dto.keyword;

import lombok.Data;

@Data
public class AlertKeywordDto {
  private String uuid;
  private String keyword;

  public AlertKeywordDto(String keyword) {
    this.keyword = keyword;
  }
}
