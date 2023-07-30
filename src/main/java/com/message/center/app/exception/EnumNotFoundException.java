package com.message.center.app.exception;


public class EnumNotFoundException extends RuntimeException {

  private static final String ENUM_ERROR_MESSAGE = "%s enum value '%s' is not exists.";

  public EnumNotFoundException(Class<?> enumType, String value) {
    super(String.format("%s enum value '%s' is not exists.", enumType.getName(), value));
  }

  public EnumNotFoundException(Class<?> enumType, String value, Throwable cause) {
    super(String.format("%s enum value '%s' is not exists.", enumType.getSimpleName(), value), cause);
  }
}
