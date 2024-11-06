package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.dto.GetPaymentResponseDTO;
import com.checkout.payment.gateway.dto.PostPaymentRequestDTO;
import com.checkout.payment.gateway.dto.PostPaymentResponseDTO;
import com.checkout.payment.gateway.dto.mappers.PaymentMapper;
import com.checkout.payment.gateway.model.Payment;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentGatewayController {

  @Autowired
  private PaymentGatewayService paymentGatewayService;

  private final PaymentMapper paymentMapper = PaymentMapper.INSTANCE;

  @GetMapping("/{id}")
  public ResponseEntity<GetPaymentResponseDTO> getPaymentById(@PathVariable UUID id) {
    Payment payment = paymentGatewayService.getPaymentById(id);

    return new ResponseEntity<>(paymentMapper.toGetPaymentResponseDto(payment), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<PostPaymentResponseDTO> processPayment(@Valid @RequestBody PostPaymentRequestDTO body) {
    Payment payment = paymentMapper.toPayment(body);
    paymentGatewayService.processPayment(payment);

    return new ResponseEntity<>(paymentMapper.toPostPaymentResponseDto(payment), HttpStatus.CREATED);
  }
}
