package com.checkout.payment.gateway.dto;

import lombok.Data;

@Data
public class PostPaymentResponseDTO {
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;
}
