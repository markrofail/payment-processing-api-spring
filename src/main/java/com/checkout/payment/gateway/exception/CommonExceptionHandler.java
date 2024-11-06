package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CommonExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(InvalidPaymentException.class)
  @ResponseBody
  public ResponseEntity<Object> handleInvalidPaymentException(InvalidPaymentException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.httpStatus());
  }

  @ExceptionHandler(PaymentNotFoundException.class)
  @ResponseBody
  public ResponseEntity<Object> handlePaymentNotFoundException(PaymentNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorResponse, new HttpHeaders(), errorResponse.httpStatus());
  }
}
