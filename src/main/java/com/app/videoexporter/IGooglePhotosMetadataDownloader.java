package com.app.videoexporter;

import com.app.videoexporter.models.VideoMetadata;
import com.app.videoexporter.models.VideoMetadataResponse;

import java.util.List;

public interface IGooglePhotosMetadataDownloader {
    List<VideoMetadata> findAllVideoMetadataFromGooglePhotos(String accessToken);
    VideoMetadataResponse findVideoMetadataFromGooglePhotos(String accessToken, String pageToken);
}
