package com.app.videoexporter.models;

import java.util.List;

public class VideoMetadataResponse {

    public List<VideoMetadata> getVideoMetadataList() {
        return videoMetadataList;
    }

    public void setVideoMetadataList(List<VideoMetadata> videoMetadataList) {
        this.videoMetadataList = videoMetadataList;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    private List<VideoMetadata> videoMetadataList;
    private String nextPageToken;

}
