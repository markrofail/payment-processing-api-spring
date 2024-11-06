package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.PaymentNotFoundException;
import com.checkout.payment.gateway.model.CreditCard;
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

  private final AcquiringBankClient acquiringBankClient;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, AcquiringBankClient acquiringBankClient) {
    this.paymentsRepository = paymentsRepository;
    this.acquiringBankClient = acquiringBankClient;
  }

  public Payment getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.getById(id)
        .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
  }

  public void processPayment(Payment payment, CreditCard creditCard) {
    PaymentStatus status = acquiringBankClient.authorizePayment(payment, creditCard);

    payment.setId(UUID.randomUUID());
    payment.setStatus(status);

    paymentsRepository.add(payment);
  }
}
