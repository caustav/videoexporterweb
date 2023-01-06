package com.app.videoexporter.models;

public class MediaItem {
    private String id;
    private String productUrl;
    private String baseUrl;
    private String mimeType;
    MediaMetadata mediaMetadata;
    private String filename;

    public String getId() {
        return id;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public MediaMetadata getMediaMetadata() {
        return mediaMetadata;
    }

    public String getFilename() {
        return filename;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setMediaMetadata(MediaMetadata mediaMetadata) {
        this.mediaMetadata = mediaMetadata;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}