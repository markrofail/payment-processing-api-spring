package com.checkout.payment.gateway.dto.mappers;

import com.checkout.payment.gateway.dto.PostPaymentRequestDTO;
import com.checkout.payment.gateway.model.CreditCard;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreditCardMapper {

  CreditCardMapper INSTANCE = Mappers.getMapper(CreditCardMapper.class);

  CreditCard toCreditCard(PostPaymentRequestDTO paymentRequestDTO);
}
