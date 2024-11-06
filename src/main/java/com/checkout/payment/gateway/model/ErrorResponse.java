package com.checkout.payment.gateway.model;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String[] messages) { }
