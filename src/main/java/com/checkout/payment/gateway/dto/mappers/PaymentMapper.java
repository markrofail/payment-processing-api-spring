package com.checkout.payment.gateway.dto.mappers;

import com.checkout.payment.gateway.dto.GetPaymentResponseDTO;
import com.checkout.payment.gateway.dto.PostPaymentRequestDTO;
import com.checkout.payment.gateway.dto.PostPaymentResponseDTO;
import com.checkout.payment.gateway.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {

  PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "cardNumberLastFour", expression = "java(Integer.parseInt(paymentRequestDTO.getCardNumber().substring(paymentRequestDTO.getCardNumber().length() - 4)))")
  Payment toPayment(PostPaymentRequestDTO paymentRequestDTO);

  PostPaymentResponseDTO toPostPaymentResponseDto(Payment payment);

  GetPaymentResponseDTO toGetPaymentResponseDto(Payment payment);
}
