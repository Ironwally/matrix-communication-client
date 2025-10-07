package com.cosium.matrix.client.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginOutput {

  private final String accessToken;

  @JsonCreator
  public LoginOutput(@JsonProperty("access_token") String accessToken) {
    this.accessToken = accessToken;
  }

  public String accessToken() {
    return accessToken;
  }
}
