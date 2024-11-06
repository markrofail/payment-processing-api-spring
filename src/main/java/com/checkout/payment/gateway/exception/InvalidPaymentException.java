package com.checkout.payment.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPaymentException extends RuntimeException {

  @Getter
  private final String[] errors;

  public InvalidPaymentException(String message, String[] errors) {
    super(message);
    this.errors = errors;
  }
}
