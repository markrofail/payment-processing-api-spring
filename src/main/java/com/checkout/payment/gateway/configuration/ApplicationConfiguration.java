package com.checkout.payment.gateway.configuration;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationConfiguration {

  @Value("${acquiring.bank.base-url}")
  private String acquiringBankBaseUrl;

  @Bean
  public WebClient acquiringBankWebClient() {
    return WebClient.builder()
        .baseUrl(acquiringBankBaseUrl)
        .build();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofMillis(10000))
        .setReadTimeout(Duration.ofMillis(10000))
        .build();
  }
}
