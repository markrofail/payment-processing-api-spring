package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.dto.AcquiringBankResponseDTO;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.CreditCard;
import com.checkout.payment.gateway.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AcquiringBankClientTest {

  private AcquiringBankClient acquiringBankClient;
  private ExchangeFunction exchangeFunctionMock;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${acquiring.bank.base-url}")
  private String acquiringBankBaseUrl;

  @BeforeEach
  public void setUp() {
    exchangeFunctionMock = mock(ExchangeFunction.class);
    WebClient webClient = WebClient.builder()
        .exchangeFunction(exchangeFunctionMock)
        .build();

    acquiringBankClient = new AcquiringBankClient(acquiringBankBaseUrl, webClient);
  }

  @Test
  public void shouldReturnPaymentStatusAuthorizedIfSuccess() throws Exception {
    AcquiringBankResponseDTO responseDTO = new AcquiringBankResponseDTO();
    responseDTO.setAuthorized(true);
    responseDTO.setAuthorization_code("0bb07405-6d44-4b50-a14f-7ae0beff13ad");
    ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(objectMapper.writeValueAsString(responseDTO))
        .build();
    when(exchangeFunctionMock.exchange(any(ClientRequest.class))).thenReturn(Mono.just(clientResponse));

    Payment payment = createTestPayment();
    CreditCard creditCard = createTestCreditCard();
    PaymentStatus status = acquiringBankClient.authorizePayment(payment, creditCard);

    assertEquals(PaymentStatus.AUTHORIZED, status);
  }

  @Test
  public void shouldReturnPaymentStatusDeclinedIfDeclined() throws Exception {
    AcquiringBankResponseDTO responseDTO = new AcquiringBankResponseDTO();
    responseDTO.setAuthorized(false);
    responseDTO.setAuthorization_code("0bb07405-6d44-4b50-a14f-7ae0beff13ad");
    ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(objectMapper.writeValueAsString(responseDTO))
        .build();
    when(exchangeFunctionMock.exchange(any(ClientRequest.class))).thenReturn(Mono.just(clientResponse));

    Payment payment = createTestPayment();
    CreditCard creditCard = createTestCreditCard();
    PaymentStatus status = acquiringBankClient.authorizePayment(payment, creditCard);

    assertEquals(PaymentStatus.DECLINED, status);
  }

  @Test
  public void shouldReturnPaymentStatusDeclinedIfFailure() {
    when(exchangeFunctionMock.exchange(any(ClientRequest.class))).thenThrow(new RuntimeException("Connection error"));

    Payment payment = createTestPayment();
    CreditCard creditCard = createTestCreditCard();
    PaymentStatus status = acquiringBankClient.authorizePayment(payment, creditCard);

    assertEquals(PaymentStatus.DECLINED, status);
  }

  private CreditCard createTestCreditCard() {
    return new CreditCard("2222405343248877", "1234");
  }

  private Payment createTestPayment() {
    YearMonth future = YearMonth.now().plusMonths(1);

    Payment payment = new Payment();
    payment.setExpiryMonth(future.getMonthValue());
    payment.setExpiryYear(future.getYear());
    payment.setCurrency("GBP");
    payment.setAmount(100);
    return payment;
  }
}
