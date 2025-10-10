package com.cosium.matrix_communication_client.message;

/** Matrix m.emote message */
public class MessageEmote extends Message {

  protected MessageEmote(final Builder builder) {
    super(builder);
  }

  public static final class Builder extends Message.Builder {

    public Builder() {
      super();
      this.type = "m.emote";
    }

    // Required: The emote action to perform. Maybe make as enum of list of available emojis from list
    @Override
    public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }
    @Override public MessageEmote build() { return new MessageEmote(this); }
  }
}
