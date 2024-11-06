package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
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
    // Simulate call to acquiring bank
    PaymentStatus status = callAcquiringBank(payment);

    payment.setId(UUID.randomUUID());
    payment.setStatus(status);

    paymentsRepository.add(payment);
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
