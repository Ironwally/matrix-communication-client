package com.cosium.matrix_communication_client.media;

/**
 * Configuration for sending an attachment
 * Keeps all information necessary for sending the attachment
 */
public class AttachmentConfig {
    private String caption;
    private String formattedCaption;
    private String contentType = ""; //Mime type

    public AttachmentConfig() {}

    public AttachmentConfig caption(String caption) { this.caption = caption; return this; }
    public AttachmentConfig formattedCaption(String formattedCaption) { this.formattedCaption = formattedCaption; return this; }
    public AttachmentConfig contentType(String contentType) { this.contentType = contentType; return this; }

    public String getCaption() { return caption; }
    public String getFormattedCaption() { return formattedCaption; }
    public String getContentType() { return contentType; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AttachmentConfig cfg = new AttachmentConfig();
        public Builder caption(String caption) { cfg.caption(caption); cfg.formattedCaption(caption); return this; }
        public Builder formattedCaption(String formattedCaption) { cfg.formattedCaption(formattedCaption); return this; }
        public Builder contentType(String contentType) { cfg.contentType(contentType); return this; }
        public AttachmentConfig build() { return cfg; }
    }
}
