package com.checkout.payment.gateway.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PostPaymentRequestDTO {
  @Min(value = 0, message = "Card number last four digits must be between 0000 and 9999")
  @Max(value = 9999, message = "Card number last four digits must be between 0000 and 9999")
  private int cardNumberLastFour;

  @Min(value = 1, message = "Expiry month must be between 1 and 12")
  @Max(value = 12, message = "Expiry month must be between 1 and 12")
  private int expiryMonth;

  @Min(value = 2023, message = "Expiry year must be current year or later")
  private int expiryYear;

  @NotBlank(message = "Currency must be provided")
  private String currency;

  @Positive(message = "Amount must be greater than zero")
  private int amount;
}
