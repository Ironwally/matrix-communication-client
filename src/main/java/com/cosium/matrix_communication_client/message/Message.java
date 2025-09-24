package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * @author Réda Housni Alaoui
 */
public class Message {

  private final String body;
  private final String format;
  private final String formattedBody;
  private final String type;
  private final long timestamp;
  private final long id;

  protected Message(Builder builder) {
    body = builder.body;
    format = builder.format;
    formattedBody = builder.formattedBody;
    type = builder.type;
    this.timestamp = builder.timestamp;
    this.id = builder.id;
  }

  @JsonCreator
  Message(
      @JsonProperty("body") String body,
      @JsonProperty("format") String format,
      @JsonProperty("formatted_body") String formattedBody,
      @JsonProperty("msgtype") String type,
      @JsonProperty("timestamp") Long timestamp,
      @JsonProperty("id") Long id) {
    this.body = body;
    this.format = format;
    this.formattedBody = formattedBody;
    this.type = type;
    this.timestamp = timestamp == null ? 0L : timestamp;
    this.id = id == null ? 0L : id;
  }

  @JsonProperty("body") public String body() { return body; }
  @JsonProperty("format") public String format() { return format; }
  @JsonProperty("formatted_body") public String formattedBody() { return formattedBody; }
  @JsonProperty("msgtype") public String type() { return type; }
  // Do not serialize timestamp and id in outgoing payloads to match Matrix API expectations
  // TODO: Check if api has similar attributes and replace if so
  @JsonIgnore public long timestamp() { return timestamp; }
  @JsonIgnore public long id() { return id; }
  @Override public int hashCode() { return Objects.hash(body, format, formattedBody, type, timestamp, id); }

  public static Builder builder() { 
    return new Builder(); 
  }

  public static class Builder {
    protected String body;
    protected String format;
    protected String formattedBody; 
    protected String type;
    protected long timestamp;
    protected long id;

    private Builder() {
      format = "org.matrix.custom.html";
      type = "undefined";
      timestamp = System.currentTimeMillis();
      id = 0L;
    }

    // constructor for extended classes
    protected Builder(Builder base) {
      this.format = base.format;
      this.timestamp = base.timestamp;
      this.id = base.id;
      this.type = base.type;
    }

    public Builder body(String body) { this.body = body; return this; }
    public Builder format(String format) { this.format = format; return this; }
    public Builder formattedBody(String formattedBody) { this.formattedBody = formattedBody; return this; }
    public Builder type(String type) { this.type = type; return this; }
    public Builder timestamp(long timestamp) { this.timestamp = timestamp; return this; }
    public Builder id(long id) { this.id = id; return this; }
    public Message build() { return new Message(this); }

    // New methods. First decide which type of message
    public MessageText.Builder text(String content) { return new MessageText.Builder(this).body(content).formattedBody("<b>"+content+"</b>"); }
    public MessageImage.Builder image(String content, String url) { return new MessageImage.Builder(this).body(content).formattedBody("<b>"+content+"</b>").url(url); }
    public MessageFile.Builder file(String content, String url) { return new MessageFile.Builder(this).body(content).formattedBody("<b>"+content+"</b>").url(url); }
    public MessageAudio.Builder audio(String content, String url) { return new MessageAudio.Builder(this).body(content).formattedBody("<b>"+content+"</b>").url(url); }
    public MessageEmote.Builder sticker(String content) { return new MessageEmote.Builder(this).body(content).formattedBody("<b>"+content+"</b>"); }
    public MessagePoll.Builder poll(String question, String[] answers) { return new MessagePoll.Builder(this).question(question).answers(answers); }
  }
}
