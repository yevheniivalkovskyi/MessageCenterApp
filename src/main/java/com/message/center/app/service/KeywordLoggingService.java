package com.message.center.app.service;

import com.message.center.app.domain.ExchangeMessage;

public interface KeywordLoggingService {

  void logExchangeMessage(ExchangeMessage exchangeMessage);
}
