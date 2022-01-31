package com.inttasks.trul.translator.enums;

import lombok.Getter;

@Getter
public enum TranslatorType {
  YODA("yoda.json"),
  SHAKESPEARE("shakespeare.json");

  private String url;

  TranslatorType(String url) {
    this.url = url;
  }
}

