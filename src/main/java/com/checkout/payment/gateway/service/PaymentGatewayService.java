package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.exception.InvalidPaymentException;
import com.checkout.payment.gateway.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.Year;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;

  public PaymentGatewayService(PaymentsRepository paymentsRepository) {
    this.paymentsRepository = paymentsRepository;
  }

  public Payment getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.getById(id)
        .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
  }

  public void processPayment(Payment payment) {
    validatePayment(payment);

    // Simulate call to acquiring bank
    PaymentStatus status = callAcquiringBank(payment);

    // Assign ID and status
    payment.setId(UUID.randomUUID());
    payment.setStatus(status);

    paymentsRepository.add(payment);
  }

  private void validatePayment(Payment payment) {
    ArrayList<String> errors = new ArrayList<>();

    if (payment.getCardNumberLastFour() < 0 || payment.getCardNumberLastFour() > 9999) {
      errors.add("Card number last four digits must be between 0000 and 9999");
    }
    if (payment.getExpiryMonth() < 1 || payment.getExpiryMonth() > 12) {
      errors.add("Expiry month must be between 1 and 12");
    }
    int currentYear = Year.now().getValue();
    if (payment.getExpiryYear() < currentYear) {
      errors.add("Expiry year must be current year or later");
    }
    if (payment.getAmount() <= 0) {
      errors.add("Amount must be greater than zero");
    }
    if (payment.getCurrency() == null || payment.getCurrency().isEmpty()) {
      errors.add("Currency must be provided");
    }

    if (!errors.isEmpty()) {
      throw new InvalidPaymentException("Invalid payment data", errors.toArray(new String[]{}));
    }
  }

  private PaymentStatus callAcquiringBank(Payment payment) {
    // Simulate bank authorization
    // For demonstration, payments with amount divisible by 5 are authorized
    if (payment.getAmount() % 5 == 0) {
      return PaymentStatus.AUTHORIZED;
    } else {
      return PaymentStatus.DECLINED;
    }
  }
}
