package com.cosium.matrix_communication_client.message;

/** Matrix m.emote message
 * @see <a href="https://spec.matrix.org/latest/client-server-api/#memote">m.emote matrix spec</a>
 *
 * This message is similar to m.text except that the sender is ‘performing’ the action contained in the body key,
 * similar to /me in IRC. This message should be prefixed by the name of the sender.
 * This message could also be represented in a different colour to distinguish it from regular m.text messages.
 */
public class MessageEmote extends Message {

  protected MessageEmote(final Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Message.Builder {

    public Builder() {
      super();
      this.type = "m.emote";
    }

    // TODO: Maybe make as enum of list of available emojis from list
    // Must Override all methods otherwise returns Builder from Superclass
    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }

    public Builder action(final String action) { this.body = action; return this; }

    @Override public MessageEmote build() {
      return new MessageEmote(this);
    }
  }
}
