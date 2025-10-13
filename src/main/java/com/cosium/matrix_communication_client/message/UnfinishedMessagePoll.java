package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Simplified Matrix poll message (non-interactive placeholder) */
public class MessagePoll extends Message {
  private final String question;
  private final List<String> options;

  protected MessagePoll(final Builder builder) {
    super(builder);
    this.question = builder.question;
    this.options = List.copyOf(builder.answers);
  }

  @SuppressWarnings("unused") // Constructor needed for JsonObject for sending message to homeserver // Constructor needed for JsonObject for sending message to homeserver
  @JsonCreator
  MessagePoll(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("msgtype") final String type,
      @JsonProperty("question") final String question,
      @JsonProperty("options") final List<String> options) {
    super(body, format, formattedBody, type);
    this.question = question;
    this.options = options == null ? List.of() : List.copyOf(options);
  }

  @JsonProperty("question") public String question() { return question; }
  @JsonProperty("options") public List<String> options() { return options; }

  public static final class Builder extends Message.Builder {

    private boolean hidden;
    private String question;
    private List<String> answers = List.of();

    public Builder() {
      super();
      this.type = "m.poll";
    }

    // Must Override all methods otherwise returns Builder from Superclass
    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }

    public Builder hidden() { this.hidden = true; return this; }
    public Builder open() { this.hidden = false; return this; }
    public Builder question(final String question) { this.question = question; return this; }
    public Builder answers(final String[] answers) { this.answers = List.of(answers); return this; }

    @Override public MessagePoll build() {
      return new MessagePoll(this);
    }
  }
}
