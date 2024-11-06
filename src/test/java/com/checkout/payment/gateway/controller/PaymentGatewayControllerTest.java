package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.dto.PostPaymentRequestDTO;
import com.checkout.payment.gateway.dto.mappers.PaymentMapper;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  final static String BASE_URL = "/api/v1/payments";

  @Autowired
  private MockMvc mvc;

  @Autowired
  PaymentsRepository paymentsRepository;

  private final PaymentMapper paymentMapper = PaymentMapper.INSTANCE;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldReturnCorrectPayment() throws Exception {
    Payment payment = new Payment();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("%s/%s".formatted(BASE_URL, payment.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void shouldReturnNotFoundForNonExistentPayment() throws Exception {
    UUID randomId = UUID.randomUUID();

    mvc.perform(MockMvcRequestBuilders.get("%s/%s".formatted(BASE_URL, randomId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value("NOT_FOUND"))
        .andExpect(jsonPath("$.messages").value("Payment not found"));
  }

  PostPaymentRequestDTO createPayload() {
    PostPaymentRequestDTO payload = new PostPaymentRequestDTO();
    payload.setCardNumberLastFour(1234);
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2025);
    payload.setCurrency("USD");
    payload.setAmount(100);

    return payload;
  }

  @Test
  void shouldReturnCreatedForValidPaymentRequest() throws Exception {
    PostPaymentRequestDTO payload = createPayload();

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.status").value("Authorized"));
  }

  @Test
  void shouldReturnBadRequestForInvalidCardNumber() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCardNumberLastFour(-1); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value(
            "Card number last four digits must be between 0000 and 9999"));
  }

  @Test
  void shouldReturnBadRequestForInvalidExpiryMonth() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setExpiryMonth(13);        // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value(
            "Expiry month must be between 1 and 12"));
  }

  @Test
  void shouldReturnBadRequestForInvalidExpiryYear() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setExpiryYear(2020);       // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value(
            "Expiry year must be current year or later"));
  }


  @Test
  void shouldReturnBadRequestForInvalidCurrency() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCurrency("");  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value(
            "Currency must be provided"));
  }

  @Test
  void shouldReturnBadRequestForInvalidAmount() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setAmount(-50);  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value(
            "Amount must be greater than zero"));
  }

  @Test
  void shouldReturnBadRequestForMultipleInvalidField() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCurrency("");  // Invalid data
    payload.setAmount(-50);  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages[0]").value(
            "Amount must be greater than zero"))
        .andExpect(jsonPath("$.messages[1]").value(
            "Currency must be provided"));
  }
}
