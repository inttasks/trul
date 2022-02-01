package com.inttasks.trul.translator.service;

import com.inttasks.trul.pokemon.model.PokemonInfo;
import com.inttasks.trul.translator.client.FunTranslationsAPI;
import com.inttasks.trul.translator.enums.TranslatorType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.inttasks.trul.common.Constants.CAVE;


@Service
public class PokemonTranslator {
  private final FunTranslationsAPI funTranslationsAPI;

  public PokemonTranslator(FunTranslationsAPI funTranslationsAPI) {
    this.funTranslationsAPI = funTranslationsAPI;
  }

  public Mono<PokemonInfo> translate(PokemonInfo pokemonInfo) {
    if (Objects.isNull(pokemonInfo.getDescription()))
      return Mono.just(pokemonInfo);

    return funTranslationsAPI.translate(pokemonInfo.getDescription(), getTranslatorType(pokemonInfo))
        .map(resp -> {
          pokemonInfo.setDescription(resp.getContents().getTranslated());
          return pokemonInfo;
        })
        .onErrorReturn(pokemonInfo);
  }

  private TranslatorType getTranslatorType(PokemonInfo pokemonInfo) {
    if (pokemonInfo.isLegendary() || CAVE.equals(pokemonInfo.getHabitat()))
      return TranslatorType.YODA;
    return TranslatorType.SHAKESPEARE;
  }
}
