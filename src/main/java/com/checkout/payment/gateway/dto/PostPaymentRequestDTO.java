package com.checkout.payment.gateway.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.YearMonth;

@Data
public class PostPaymentRequestDTO {

  @NotBlank(message = "Card number must be provided")
  @Pattern(regexp = "\\d{14,19}", message = "Card number must be between 14 and 19 digits")
  private String cardNumber;

  @NotBlank(message = "CVV must be provided")
  @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
  private String cvv;

  @Min(value = 1, message = "Expiry month must be between 1 and 12")
  @Max(value = 12, message = "Expiry month must be between 1 and 12")
  private int expiryMonth;

  @Min(value = 2024, message = "Expiry year must be current year or later")
  private int expiryYear;

  @NotBlank(message = "Currency must be provided")
  @Pattern(regexp = "USD|EUR|GBP", message = "Currency must be one of USD, EUR, or GBP")
  private String currency;

  @Positive(message = "Amount must be greater than zero")
  private int amount;

  @AssertTrue(message = "Expiry date must be in the future")
  public boolean isExpiryDateValid() {
    YearMonth expiry;
    try {
      expiry = YearMonth.of(expiryYear, expiryMonth);
    } catch (Exception e) {
      return true; // other validations handle invalid values
    }

    YearMonth now = YearMonth.now();
    return expiry.isAfter(now);
  }
}
