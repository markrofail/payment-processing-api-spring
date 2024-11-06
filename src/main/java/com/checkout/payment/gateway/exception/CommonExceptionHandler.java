package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(InvalidPaymentException.class)
  public ResponseEntity<Object> handleInvalidPaymentException(InvalidPaymentException ex) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getErrors());
    return new ResponseEntity<>(errorResponse, errorResponse.status());
  }

  @ExceptionHandler(PaymentNotFoundException.class)
  public ResponseEntity<Object> handlePaymentNotFoundException(PaymentNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND,
        new String[]{ex.getMessage()});
    return new ResponseEntity<>(errorResponse, errorResponse.status());
  }
}
