package com.inttasks.trul.pokemon.controller;

import com.inttasks.trul.pokemon.model.PokemonInfo;
import com.inttasks.trul.pokemon.service.PokemonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {

  private final PokemonService pokemonService;

  public PokemonController(PokemonService pokemonService) {
    this.pokemonService = pokemonService;
  }


  @GetMapping("/{name}")
  private  Mono<PokemonInfo> getPokemon(@PathVariable String name) {
    return pokemonService.getPokemon(name);
  }

  @GetMapping("/translated/{name}")
  private  Mono<PokemonInfo> getPokemonTranslated(@PathVariable String name) {
    return pokemonService.getPokemonTranslated(name);
  }
}
