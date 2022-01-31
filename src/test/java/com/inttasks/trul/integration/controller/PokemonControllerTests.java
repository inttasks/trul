package com.inttasks.trul.integration.controller;

import com.inttasks.trul.pokemon.client.PokeAPI;
import com.inttasks.trul.pokemon.dto.SpeciesResp;
import com.inttasks.trul.pokemon.model.PokemonInfo;
import com.inttasks.trul.translator.client.FunTranslationsAPI;
import com.inttasks.trul.translator.dto.FunTranslationsResp;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.inttasks.trul.common.Constants.CAVE;
import static com.inttasks.trul.common.Constants.EN_LANG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PokemonControllerTests {

  private static final String POKE = "poke";
  private static final String DESC1 = "first description";
  private static final String DESC2 = "Second description";
  private static final String TRANSLATE2 = "Second translation";

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private PokeAPI pokeAPI;

  @MockBean
  private FunTranslationsAPI funTranslationsAPI;

  @Test
  void getPokemon_success() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(generateBasicPokemonInfo());
    when(pokeAPI.getSpecies(any())).thenReturn(generateNoneLegendaryCaveHabitatSpeciesResp());

    WebTestClient.ResponseSpec res = webTestClient
        .get().uri("/pokemon/ditto")
        .exchange().expectStatus().isOk();
  }

  @Test
  void getPokemonTranslated_success() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(generateBasicPokemonInfo());
    when(pokeAPI.getSpecies(any())).thenReturn(generateNoneLegendaryCaveHabitatSpeciesResp());
    when(funTranslationsAPI.translate(any(), any())).thenReturn(Mono.just(new FunTranslationsResp(new FunTranslationsResp.Contents(TRANSLATE2))));

    WebTestClient.ResponseSpec res = webTestClient
        .get().uri("/pokemon/translated/ditto")
        .exchange().expectStatus().isOk();
  }

  @Test
  void getPokemon_not_found() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")));
    WebTestClient.ResponseSpec res = webTestClient
        .get().uri("/pokemon/NotExits")
        .exchange().expectStatus().isNotFound();
  }

  private Mono<PokemonInfo> generateBasicPokemonInfo() {
    return Mono.just(PokemonInfo.builder()
        .name(POKE)
        .species(PokemonInfo.Species.builder().url("https://any").build())
        .build());
  }

  private Mono<SpeciesResp> generateNoneLegendaryCaveHabitatSpeciesResp() {
    return Mono.just(SpeciesResp.builder()
        .legendary(false)
        .habitat(SpeciesResp.Habitat.builder().name(CAVE).build())
        .flavorTextEntries(Lists.list(SpeciesResp.FlavorTextEntry.builder()
                .flavorText(DESC1)
                .language(SpeciesResp.FlavorTextEntry.Language.builder().name("ch").build())
                .build(),
            SpeciesResp.FlavorTextEntry.builder()
                .flavorText(DESC2)
                .language(SpeciesResp.FlavorTextEntry.Language.builder().name(EN_LANG).build())
                .build()))
        .build());
  }
}
