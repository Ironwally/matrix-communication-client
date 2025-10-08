package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * @author Réda Housni Alaoui
 */
public class Message {

  protected final String body;
  protected final String format;
  protected final String formattedBody;
  protected final String type;

  protected Message(Builder builder) {
    body = builder.body;
    format = builder.format;
    formattedBody = builder.formattedBody;
    type = builder.type;
  }

  @SuppressWarnings("unused") // Constructor needed for JsonObject creation
  @JsonCreator
  Message(
      @JsonProperty("body") String body,
      @JsonProperty("format") String format,
      @JsonProperty("formatted_body") String formattedBody,
      @JsonProperty("msgtype") String type) {
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

  public static class Builder {
    protected String body;
    protected String format;
    protected String formattedBody;
    protected String type;

    private Builder() {
      format = "org.matrix.custom.html";
      type = "undefined";
    }

    // constructor for extended classes
    protected Builder(Builder base) {
      this.format = base.format;
      this.type = base.type;
    }

    public Builder body(String body) { this.body = body; return this; }
    public Builder format(String format) { this.format = format; return this; }
    public Builder formattedBody(String formattedBody) { this.formattedBody = formattedBody; return this; }
    public Builder type(String type) { this.type = type; return this; }
    public Message build() { return new Message(this); }

    // New methods. First decide which type of message
    public MessageText.Builder text(String content) { return new MessageText.Builder(this).body(content).formattedBody("<b>"+content+"</b>"); }
    public MessageImage.Builder image() { return new MessageImage.Builder(this); }
    public MessageFile.Builder file(String content, String url) { return new MessageFile.Builder(this).body(content).formattedBody("<b>"+content+"</b>").url(url); }
    public MessageAudio.Builder audio(String content, String url) { return new MessageAudio.Builder(this).body(content).formattedBody("<b>"+content+"</b>").url(url); }
    public MessageEmote.Builder sticker(String content) { return new MessageEmote.Builder(this).body(content).formattedBody("<b>"+content+"</b>"); }
    public MessagePoll.Builder poll(String question, String[] answers) { return new MessagePoll.Builder(this).question(question).answers(answers); }
  }
}
