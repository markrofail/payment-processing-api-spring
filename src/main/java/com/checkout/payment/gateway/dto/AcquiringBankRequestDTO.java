package com.checkout.payment.gateway.dto;

import lombok.Data;

@Data
public class AcquiringBankRequestDTO {
  private String card_number;
  private String expiry_date;
  private String currency;
  private int amount;
  private String cvv;
}