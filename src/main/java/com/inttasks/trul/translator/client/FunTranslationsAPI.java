package com.inttasks.trul.translator.client;

import com.inttasks.trul.translator.dto.FunTranslationsReq;
import com.inttasks.trul.translator.dto.FunTranslationsResp;
import com.inttasks.trul.translator.enums.TranslatorType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class FunTranslationsAPI {
  private final WebClient webClient;

  public FunTranslationsAPI() {
    this.webClient = WebClient.builder()
        .baseUrl("https://api.funtranslations.com/translate/")
        .build();
  }

  public Mono<FunTranslationsResp> translate(String text, TranslatorType type) {
    FunTranslationsReq req = FunTranslationsReq.builder()
        .text(text)
        .build();
    return webClient.post()
        .uri(type.getUrl())
        .bodyValue(req)
        .retrieve()
        .bodyToMono(FunTranslationsResp.class);

  }
}
