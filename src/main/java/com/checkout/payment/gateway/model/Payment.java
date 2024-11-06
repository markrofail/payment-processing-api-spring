package com.checkout.payment.gateway.model;

import lombok.Data;

import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;


@Data
public class Payment {
  private UUID id;
  private PaymentStatus status;
  private int cardNumberLastFour;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;
}
