package de.muspellheim.activitysampling.application.shared;

public class Exceptions {
  public static String joinExceptionMessages(String errorMessage, Throwable cause) {
    if (cause == null) {
      return errorMessage;
    }

    return joinExceptionMessages(errorMessage + " " + cause.getMessage(), cause.getCause());
  }
}
