package com.app.videoexporter.models;

public class MediaMetadata {
    private String creationTime;
    private String width;
    private String height;
    private Video video;

    public String getCreationTime() {
        return creationTime;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public Video getVideo() {
        return video;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}

