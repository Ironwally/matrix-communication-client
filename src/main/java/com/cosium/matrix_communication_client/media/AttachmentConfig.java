package com.cosium.matrix_communication_client.media;

/**
 * Configuration for sending an attachment
 */
public class AttachmentConfig {
    private OwnedTransactionId txnId; /** Fixed transaction id. Optional. Otherwise generated random. */
    private AttachmentInfo info; /** Type-specific metadata about the attachment. */
    private Thumbnail thumbnail; /** Optional */
    private String caption; /** Optional */
    private FormattedBody formattedCaption; /** Optional */
    private Mentions mentions; /** Intentional mentions to be included in the media event. */
    private Reply reply; /** Reply parameters for the attachment (replied-to event and thread-related metadata). */

    public AttachmentConfig() {}
    /** @deprecated Replaced by builder() */
    @Deprecated
    public static AttachmentConfig newConfig() { return new AttachmentConfig(); }

    public AttachmentConfig txnId(OwnedTransactionId txnId) { this.txnId = txnId; return this; }
    public AttachmentConfig info(AttachmentInfo info) { this.info = info; return this; }
    public AttachmentConfig thumbnail(Thumbnail thumbnail) { this.thumbnail = thumbnail; return this; }
    public AttachmentConfig caption(String caption) { this.caption = caption; return this; }
    public AttachmentConfig formattedCaption(FormattedBody formattedCaption) { this.formattedCaption = formattedCaption; return this; }
    public AttachmentConfig mentions(Mentions mentions) { this.mentions = mentions; return this; }
    public AttachmentConfig reply(Reply reply) { this.reply = reply; return this; }

    public OwnedTransactionId getTxnId() { return txnId; }
    public AttachmentInfo getInfo() { return info; }
    public Thumbnail getThumbnail() { return thumbnail; }
    public String getCaption() { return caption; }
    public FormattedBody getFormattedCaption() { return formattedCaption; }
    public Mentions getMentions() { return mentions; }
    public Reply getReply() { return reply; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AttachmentConfig cfg = new AttachmentConfig();
        public Builder txnId(OwnedTransactionId txnId) { cfg.txnId(txnId); return this; }
        public Builder info(AttachmentInfo info) { cfg.info(info); return this; }
        public Builder thumbnail(Thumbnail thumbnail) { cfg.thumbnail(thumbnail); return this; }
        public Builder caption(String caption) { cfg.caption(caption); return this; }
        public Builder formattedCaption(FormattedBody formattedCaption) { cfg.formattedCaption(formattedCaption); return this; }
        public Builder mentions(Mentions mentions) { cfg.mentions(mentions); return this; }
        public Builder reply(Reply reply) { cfg.reply(reply); return this; }
        public AttachmentConfig build() { return cfg; }
    }
}
