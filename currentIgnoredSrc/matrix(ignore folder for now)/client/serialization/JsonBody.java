package com.cosium.matrix.client.serialization;

class JsonBody<T> {

  private final int responseStatusCode;
  private final T body;

  JsonBody(int responseStatusCode, T body) {
    this.responseStatusCode = responseStatusCode;
    this.body = body;
  }

  boolean isNotFound() {
    return responseStatusCode == 404;
  }

  T parse() {
    if (responseStatusCode != 200) {
      throw new IllegalStateException(
          String.format("Response has status code %s instead of 200", responseStatusCode));
    }
    return body;
  }
}
