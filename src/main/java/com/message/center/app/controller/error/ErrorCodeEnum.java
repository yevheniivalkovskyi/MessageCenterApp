package com.message.center.app.controller.error;

public enum ErrorCodeEnum {
  UNKNOWN("unknown"),
  NOT_FOUND("NOT_FOUND"),
  MISSING_FIELD("MISSING_FIELD"),
  INVALID_VALUE("INVALID_VALUE"),
  DEPENDENT_SERVICE_ERROR("DEPENDENT_SERVICE_ERROR"),
  ACCESS_DENIED("ACCESS_DENIED"),
  BAD_REQUEST("BAD_REQUEST");

  private final String codeText;

  ErrorCodeEnum(String codeText) {
    this.codeText = codeText;
  }

  /**
   * Convert string to ErrorCodeEnum.
   *
   * @param text String Input
   * @return ErrorCodeEnum
   */
  public static ErrorCodeEnum fromString(String text) {
    for (ErrorCodeEnum type : ErrorCodeEnum.values()) {
      if (type.codeText.equalsIgnoreCase(text)) {
        return type;
      }
    }
    return UNKNOWN;
  }

  @Override
  public String toString() {
    return this.codeText;
  }
}
