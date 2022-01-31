package com.inttasks.trul.pokemon.service;

import com.inttasks.trul.pokemon.client.PokeAPI;
import com.inttasks.trul.pokemon.dto.SpeciesResp;
import com.inttasks.trul.pokemon.model.PokemonInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.inttasks.trul.common.Constants.EN_LANG;


@Service
public class PokemonService {

  private final PokeAPI pokeAPI;

  public PokemonService(PokeAPI pokeAPI) {
    this.pokeAPI = pokeAPI;
  }

  public Mono<PokemonInfo> getPokemon(String name) {
    return pokeAPI.getPokemonBasicInfo(name)
        .zipWhen(p -> {
          if (Objects.isNull(p.getSpecies()) || StringUtils.isBlank(p.getSpecies().getUrl()))
            return Mono.just(SpeciesResp.builder().build());
          return pokeAPI.getSpecies(p.getSpecies().getUrl());
        }, this::addSpeciesInfo);
  }

  private PokemonInfo addSpeciesInfo(PokemonInfo pokemonInfo, SpeciesResp speciesResp) {
    if (Objects.isNull(speciesResp))
      return pokemonInfo;

    pokemonInfo.setLegendary(speciesResp.isLegendary());

    if (Objects.nonNull(speciesResp.getHabitat()))
      pokemonInfo.setHabitat(speciesResp.getHabitat().getName());

    if (Objects.isNull(speciesResp.getFlavorTextEntries()))
      return pokemonInfo;
    speciesResp.getFlavorTextEntries()
        .stream()
        .filter(f -> EN_LANG.equals(f.getLanguage().getName()))
        .findFirst()
        .ifPresent(f -> pokemonInfo.setDescription(f.getFlavorText()));
    return pokemonInfo;
  }
}
