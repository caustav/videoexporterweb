package com.app.videoexporter;

import com.app.videoexporter.models.VideoMetadata;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IYoutubeVideoUploader {
     void upload(VideoMetadata videoMetadata, String accessToken, String appToken) throws IOException, GeneralSecurityException;
     void deleteVideo(VideoMetadata videoMetadata, String appToken) throws IOException;
}
