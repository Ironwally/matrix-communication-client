package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * @author Réda Housni Alaoui
 * @see <a href="https://spec.matrix.org/latest/client-server-api/#mroommessage">m.room.message Matrix Spec</a>
 */
public class Message {

  protected final String body;
  protected final String format;
  protected final String formattedBody;
  protected final String type;

  protected Message(final Builder builder) {
    body = builder.body;
    format = builder.format;
    formattedBody = builder.formattedBody;
    type = builder.type;
  }

  @SuppressWarnings("unused") // Constructor needed for JsonObject creation
  @JsonCreator
  Message(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("msgtype") final String type) {
    this.body = body;
    this.format = format;
    this.formattedBody = formattedBody;
    this.type = type;
  }

  @JsonProperty("body") public String body() { return body; }
  @JsonProperty("format") public String format() { return format; }
  @JsonProperty("formatted_body") public String formattedBody() { return formattedBody; }
  @JsonProperty("msgtype") public String type() { return type; }
  @Override public int hashCode() { return Objects.hash(body, format, formattedBody, type); }

  public static Builder builder() {
    return new Builder();
  }

  protected static class Builder {
    protected String body;
    protected String format;
    protected String formattedBody;
    protected String type;

    protected Builder() {
      format = "org.matrix.custom.html";
      type = "undefined";
    }

    public Builder body(final String body) { this.body = body; return this; }
    public Builder format(final String format) { this.format = format; return this; }
    public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }
    public Builder type(final String type) { this.type = type; return this; }

    public Message build() {
      return new Message(this);
    }
}
}
