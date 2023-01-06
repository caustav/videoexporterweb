package com.app.videoexporter.models;

public class VideoMetadata {

    private String id;
    private String baseUrl;
    private String name;

    private String creationTime;

    public VideoMetadata(String id, String baseUrl, String name, String creationTime) {
        this.id = id;
        this.baseUrl = baseUrl;
        this.name = name;
        this.creationTime = creationTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
