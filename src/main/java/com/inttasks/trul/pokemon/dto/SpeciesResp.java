package com.inttasks.trul.pokemon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpeciesResp {
  private Habitat habitat;
  @JsonProperty("flavor_text_entries")
  private List<FlavorTextEntry> flavorTextEntries;
  @JsonProperty("is_legendary")
  private boolean legendary;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class FlavorTextEntry {
    @JsonProperty("flavor_text")
    private String flavorText;
    private Language language ;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Language {
      private String name;
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Habitat {
    private String name;
  }
}
