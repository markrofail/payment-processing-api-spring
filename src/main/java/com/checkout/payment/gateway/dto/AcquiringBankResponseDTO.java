package com.checkout.payment.gateway.dto;

import lombok.Data;

@Data
public class AcquiringBankResponseDTO {

  private boolean authorized;
  private String authorization_code;
}