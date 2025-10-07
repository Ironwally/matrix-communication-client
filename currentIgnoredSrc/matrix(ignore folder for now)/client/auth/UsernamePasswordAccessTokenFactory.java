package com.cosium.matrix.client.auth;

import com.cosium.matrix.client.core.LoginInput;
import com.cosium.matrix.client.core.MatrixUnprotectedApi;
import com.cosium.matrix.client.util.Lazy;

class UsernamePasswordAccessTokenFactory implements AccessTokenFactory {

  private final Lazy<String> accessToken;

  UsernamePasswordAccessTokenFactory(
      Lazy<MatrixUnprotectedApi> api, String username, String password) {
    accessToken =
        Lazy.of(
            () ->
                api.get()
                    .login(
                        new LoginInput(
                            "m.login.password",
                            new LoginInput.Identifier("m.id.user", username),
                            password))
                    .accessToken());
  }

  @Override
  public String build() {
    return accessToken.get();
  }
}
