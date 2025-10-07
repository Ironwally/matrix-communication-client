package com.cosium.matrix.client.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginInput {

  private final String type;
  private final Identifier identifier;
  private final String password;

  public LoginInput(String type, Identifier identifier, String password) {
    this.type = type;
    this.identifier = identifier;
    this.password = password;
  }

  @JsonProperty("type")
  String type() {
    return type;
  }

  @JsonProperty("identifier")
  public Identifier identifier() {
    return identifier;
  }

  @JsonProperty("password")
  public String password() {
    return password;
  }

  public static class Identifier {
    private final String type;
    private final String user;

  public Identifier(String type, String user) {
      this.type = type;
      this.user = user;
    }

    @JsonProperty("type")
  public String type() {
      return type;
    }

    @JsonProperty("user")
  public String user() {
      return user;
    }
  }
}
