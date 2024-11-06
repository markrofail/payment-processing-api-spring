package com.checkout.payment.gateway.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PostPaymentRequestDTO {
  @NotBlank(message = "Card number must be provided")
  @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
  private String cardNumber;

  @NotBlank(message = "CVV must be provided")
  @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
  private String cvv;

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
