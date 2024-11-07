package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.dto.AcquiringBankRequestDTO;
import com.checkout.payment.gateway.dto.AcquiringBankResponseDTO;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.CreditCard;
import com.checkout.payment.gateway.model.Payment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AcquiringBankClient {

  private final WebClient webClient;

  public AcquiringBankClient(@Value("${acquiring.bank.base-url}") String acquiringBankBaseUrl, @Qualifier("acquiringBankWebClient") WebClient webClient) {
    this.webClient = webClient.mutate()
        .baseUrl(acquiringBankBaseUrl)
        .build();
  }

  public PaymentStatus authorizePayment(Payment payment, CreditCard creditCard) {
    try {
      AcquiringBankRequestDTO requestDTO = new AcquiringBankRequestDTO();
      requestDTO.setCard_number(creditCard.cardNumber());
      requestDTO.setCvv(creditCard.cvv());
      requestDTO.setExpiry_date(String.format("%02d/%d", payment.getExpiryMonth(), payment.getExpiryYear()));
      requestDTO.setCurrency(payment.getCurrency());
      requestDTO.setAmount(payment.getAmount());

      AcquiringBankResponseDTO responseDTO = webClient.post()
          .uri("/payments")
          .bodyValue(requestDTO)
          .retrieve()
          .bodyToMono(AcquiringBankResponseDTO.class)
          .block();

      if (responseDTO != null && responseDTO.isAuthorized()) {
        return PaymentStatus.AUTHORIZED;
      } else {
        return PaymentStatus.DECLINED;
      }
    } catch (Exception e) {
      // Handle exceptions (e.g., log the error)
      // For simplicity, we'll return DECLINED on error
      return PaymentStatus.DECLINED;
    }
  }
}
