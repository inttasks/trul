package com.inttasks.trul.unit.service;


import com.inttasks.trul.pokemon.client.PokeAPI;
import com.inttasks.trul.pokemon.dto.SpeciesResp;
import com.inttasks.trul.pokemon.model.PokemonInfo;
import com.inttasks.trul.pokemon.service.PokemonService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.inttasks.trul.common.Constants.CAVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PokemonServiceTests {

  private static final String POKE = "poke";
  private static final String DESC1 = "first description";
  private static final String DESC2 = "Second description";

  @SpyBean
  private PokeAPI pokeAPI;

  @Autowired
  private PokemonService pokemonService;

  @Test
  void getPokemon_success() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(generateBasicPokemonInfo());
    when(pokeAPI.getSpecies(any())).thenReturn(generateNoneLegendaryCaveHabitatSpeciesResp());
    StepVerifier.create(pokemonService.getPokemon(POKE))
        .assertNext(p -> {
          assertEquals(POKE, p.getName());
          assertFalse(p.isLegendary());
          assertEquals(DESC2, p.getDescription());
          assertEquals(CAVE, p.getHabitat());
        })
        .verifyComplete();
  }

  @Test
  void getPokemon_no_eng_desc() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(generateBasicPokemonInfo());
    when(pokeAPI.getSpecies(any())).thenReturn(generateNoneLegendaryCaveHabitatSpeciesResp().map(s -> {
      s.getFlavorTextEntries().remove(1);
      return s;
    }));
    StepVerifier.create(pokemonService.getPokemon(POKE))
        .assertNext(p -> {
          assertEquals(POKE, p.getName());
          assertFalse(p.isLegendary());
          assertNull(p.getDescription());
          assertEquals(CAVE, p.getHabitat());
        })
        .verifyComplete();
  }

  @Test
  void getPokemon_incomplete() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(generateIncompletePokemonInfo());
    StepVerifier.create(pokemonService.getPokemon(POKE))
        .assertNext(p -> {
          assertEquals(POKE, p.getName());
          assertNull(p.getDescription());
          assertNull(p.getHabitat());
        })
        .verifyComplete();
  }


  @Test
  void getPokemon_not_found() {
    when(pokeAPI.getPokemonBasicInfo(any())).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")));
    StepVerifier.create(pokemonService.getPokemon(POKE))
        .expectError(ResponseStatusException.class)
        .verify();
  }


  private Mono<PokemonInfo> generateBasicPokemonInfo() {
    return Mono.just(PokemonInfo.builder()
        .name(POKE)
        .species(PokemonInfo.Species.builder().url("https://any").build())
        .build());
  }

  private Mono<PokemonInfo> generateIncompletePokemonInfo() {
    return Mono.just(PokemonInfo.builder()
        .name(POKE)
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
                .language(SpeciesResp.FlavorTextEntry.Language.builder().name("en").build())
                .build()))
        .build());
  }

}

