package com.checkout.payment.gateway.dto;

import com.checkout.payment.gateway.enums.PaymentStatus;
import lombok.Data;
import java.util.UUID;

@Data
public class PostPaymentRequestDTO {
  private UUID id;
  private String status;
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;
}