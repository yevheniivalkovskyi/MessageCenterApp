package com.message.center.app.domain;

import com.message.center.app.exception.EnumNotFoundException;

public enum ExchangeMessageType {
  P2P("P2P"),
  GROUP("GROUP"),
  BROADCAST("BROADCAST");

  private final String value;

  ExchangeMessageType(String value) {
    this.value = value;
  }

  public static ExchangeMessageType fromString(String text) {
    ExchangeMessageType[] var1 = values();
    int var2 = var1.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      ExchangeMessageType ExchangeMessageType = var1[var3];
      if (ExchangeMessageType.value.equalsIgnoreCase(text)) {
        return ExchangeMessageType;
      }
    }

    throw new EnumNotFoundException(ExchangeMessageType.class, text);
  }

  public String toString() {
    return this.value;
  }
}
