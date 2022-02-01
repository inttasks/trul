package com.inttasks.trul.unit.service;

import com.inttasks.trul.pokemon.model.PokemonInfo;
import com.inttasks.trul.translator.client.FunTranslationsAPI;
import com.inttasks.trul.translator.dto.FunTranslationsResp;
import com.inttasks.trul.translator.enums.TranslatorType;
import com.inttasks.trul.translator.service.PokemonTranslator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.inttasks.trul.common.Constants.CAVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PokemonTranslatorTests {
  private static final String POKE = "poke";
  private static final String POKY = "poky";
  private static final String DESC1 = "first description";
  private static final String DESC2 = "Second description";
  private static final String YODA = "yoda translation";
  private static final String SHAKESPEARE = "shakespeare translation";

  @Autowired
  private PokemonTranslator pokemonTranslator;

  @MockBean
  private FunTranslationsAPI funTranslationsAPI;

  @Test
  void normalPokemon_shakespeare() {
    when(funTranslationsAPI.translate(DESC2, TranslatorType.SHAKESPEARE)).thenReturn(Mono.just(new FunTranslationsResp(new FunTranslationsResp.Contents(SHAKESPEARE))));
    StepVerifier.create(pokemonTranslator.translate(generateBasicPokemonInfo()))
        .assertNext(p -> {
          assertEquals(SHAKESPEARE, p.getDescription());
        })
        .verifyComplete();
  }

  @Test
  void noTranslation() {
    when(funTranslationsAPI.translate(DESC2, TranslatorType.SHAKESPEARE)).thenReturn(Mono.just(new FunTranslationsResp(new FunTranslationsResp.Contents(SHAKESPEARE))));
    PokemonInfo pokemonInfo = generateBasicPokemonInfo();
    pokemonInfo.setDescription(null);
    StepVerifier.create(pokemonTranslator.translate(pokemonInfo))
        .assertNext(p -> {
          assertNull(p.getDescription());
        })
        .verifyComplete();
  }


  @Test
  void caveHabitatPokemon_yoda() {
    when(funTranslationsAPI.translate(DESC2, TranslatorType.YODA)).thenReturn(Mono.just(new FunTranslationsResp(new FunTranslationsResp.Contents(YODA))));
    PokemonInfo pokemonInfo = generateBasicPokemonInfo();
    pokemonInfo.setHabitat(CAVE);
    StepVerifier.create(pokemonTranslator.translate(pokemonInfo))
        .assertNext(p -> {
          assertEquals(YODA, p.getDescription());
        })
        .verifyComplete();
  }

  @Test
  void legendaryPokemon_yoda() {
    when(funTranslationsAPI.translate(DESC2, TranslatorType.YODA)).thenReturn(Mono.just(new FunTranslationsResp(new FunTranslationsResp.Contents(YODA))));
    PokemonInfo pokemonInfo = generateBasicPokemonInfo();
    pokemonInfo.setLegendary(true);
    StepVerifier.create(pokemonTranslator.translate(pokemonInfo))
        .assertNext(p -> {
          assertEquals(YODA, p.getDescription());
        })
        .verifyComplete();
  }

  @Test
  void funTranslationsAPI_fail() {
    when(funTranslationsAPI.translate(any(), any())).thenReturn(Mono.error(new WebClientResponseException(503, "Something went bad!", null, null, null)));
    PokemonInfo pokemonInfo = generateBasicPokemonInfo();
    pokemonInfo.setLegendary(true);
    StepVerifier.create(pokemonTranslator.translate(pokemonInfo))
        .assertNext(p -> {
          assertEquals(DESC2, p.getDescription());
        })
        .verifyComplete();
  }


  private PokemonInfo generateBasicPokemonInfo() {
    return PokemonInfo.builder()
        .name(POKE)
        .habitat("home")
        .legendary(false)
        .description(DESC2)
        .build();
  }

}

