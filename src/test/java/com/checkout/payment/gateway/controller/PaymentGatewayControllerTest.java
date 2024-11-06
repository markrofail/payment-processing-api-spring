package com.checkout.payment.gateway.controller;


import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.client.AcquiringBankClient;
import com.checkout.payment.gateway.dto.PostPaymentRequestDTO;
import com.checkout.payment.gateway.dto.mappers.PaymentMapper;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.CreditCard;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.time.YearMonth;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @MockBean
  private AcquiringBankClient bankClient;

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
    payload.setCardNumber("2222405343248877");
    payload.setCvv("123");
    payload.setExpiryMonth(12);
    payload.setExpiryYear(2025);
    payload.setCurrency("USD");
    payload.setAmount(100);

    return payload;
  }

  @Test
  void shouldReturnCreatedForValidPaymentRequest() throws Exception {
    PostPaymentRequestDTO payload = createPayload();

    Payment payment = paymentMapper.toPayment(payload);
    payment.setId(UUID.randomUUID());
    payment.setStatus(PaymentStatus.AUTHORIZED);
    when(bankClient.authorizePayment(any(Payment.class), any(CreditCard.class))).thenReturn(PaymentStatus.AUTHORIZED);

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.status").value("Authorized"));
  }

  @Test
  void shouldReturnBadRequestForEmptyCardNumber() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCardNumber(""); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages", containsInAnyOrder("Card number must be provided", "Card number must be between 14 and 19 digits")));
  }


  @Test
  void shouldReturnBadRequestForSmallCardNumber() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCardNumber("1234"); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("Card number must be between 14 and 19 digits"));
  }

  @Test
  void shouldReturnBadRequestForLargeCardNumber() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCardNumber("12341234123412341234"); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("Card number must be between 14 and 19 digits"));
  }

  @Test
  void shouldReturnBadRequestForEmptyCardCVV() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCvv(""); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages", containsInAnyOrder("CVV must be provided", "CVV must be 3 or 4 digits")));
  }

  @Test
  void shouldReturnBadRequestForLargeCardCVV() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCvv("12345"); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("CVV must be 3 or 4 digits"));
  }

  @Test
  void shouldReturnBadRequestForSmallCardCVV() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCvv("1"); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("CVV must be 3 or 4 digits"));
  }

  @Test
  void shouldReturnBadRequestForInvalidExpiryMonth() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setExpiryMonth(13);        // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("Expiry month must be between 1 and 12"));
  }

  @Test
  void shouldReturnBadRequestForInvalidExpiryYear() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setExpiryYear(2020);       // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages", containsInAnyOrder("Expiry date must be in the future", "Expiry year must be current year or later")));
  }


  @Test
  void shouldReturnBadRequestForInvalidExpiryDate() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    YearMonth past = YearMonth.now().minusMonths(1);
    payload.setExpiryYear(past.getYear());        // Invalid data
    payload.setExpiryMonth(past.getMonthValue()); // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("Expiry date must be in the future"));
  }

  @Test
  void shouldReturnBadRequestForEmptyCurrency() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCurrency("");  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages", containsInAnyOrder("Currency must be one of USD, EUR, or GBP", "Currency must be provided")));
  }

  @Test
  void shouldReturnBadRequestForUnrecognizedCurrency() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCurrency("EGP");  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("Currency must be one of USD, EUR, or GBP"));
  }

  @Test
  void shouldReturnBadRequestForInvalidAmount() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setAmount(-50);  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages").value("Amount must be greater than zero"));
  }

  @Test
  void shouldReturnBadRequestForMultipleInvalidFields() throws Exception {
    PostPaymentRequestDTO payload = createPayload();
    payload.setCurrency("");  // Invalid data
    payload.setAmount(-50);  // Invalid data

    mvc.perform(MockMvcRequestBuilders.post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.messages", containsInAnyOrder(
            "Currency must be one of USD, EUR, or GBP",
            "Currency must be provided",
            "Amount must be greater than zero"
        )));
  }
}
