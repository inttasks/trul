package com.inttasks.trul.translator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FunTranslationsResp {
  private Contents contents;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Contents {
    private String translated;
  }
}
