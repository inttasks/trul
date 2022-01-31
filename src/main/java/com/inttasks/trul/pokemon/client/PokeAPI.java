package com.inttasks.trul.pokemon.client;

import com.inttasks.trul.pokemon.dto.SpeciesResp;
import com.inttasks.trul.pokemon.model.PokemonInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


@Service
public class PokeAPI {

  private final WebClient webClient;

  public PokeAPI() {
    final int size = 16 * 1024 * 1024;
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
        .build();
    this.webClient = WebClient.builder()
        .exchangeStrategies(strategies)
        .build();
  }

  public Mono<PokemonInfo> getPokemonBasicInfo(String name) {
    return webClient.get()
        .uri("https://pokeapi.co/api/v2/pokemon/" + name)
        .retrieve()
        .bodyToMono(PokemonInfo.class)
        .onErrorResume(WebClientResponseException.class, this::handleError);
  }

  public Mono<SpeciesResp> getSpecies(String url) {
    return webClient.get()
        .uri(url)
        .retrieve()
        .bodyToMono(SpeciesResp.class)
        .onErrorResume(Mono::error);
  }

  private Mono<PokemonInfo> handleError(WebClientResponseException e) {
    String message = "Something went wrong!";
    if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
      message = "Is this a new pokemon! Because we don't have any info on it.";

    return Mono.error(new ResponseStatusException(e.getStatusCode(), message));
  }

}
