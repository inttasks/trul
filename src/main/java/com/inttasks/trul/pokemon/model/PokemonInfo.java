package com.inttasks.trul.pokemon.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PokemonInfo {
  private String name;
  private String description;
  private String habitat;
  @JsonProperty("isLegendary")
  private boolean legendary;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Species species;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Species {
    private String url;
  }
}
