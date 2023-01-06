package com.app.videoexporter;

import com.app.videoexporter.models.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class VideoExporterAsyncService implements IVideoExporterAsyncService{

    Logger logger = LoggerFactory.getLogger(VideoExporterAsyncService.class);
    @Autowired
    private IYoutubeVideoUploader videoUploader;
    @Autowired
    private GooglePhotosMetadataDownloader googlePhotosMetadataDownloader;
    @Autowired
    private GoogleSignInHandler googleSignInHandler;

    @Async
    @Override
    public void export(String code, String appToken) throws GeneralSecurityException, IOException {
        var accessToken =googleSignInHandler.getAccessTokenFromAuthorizationCode(code);
        var videoMetadataList = googlePhotosMetadataDownloader.
                findAllVideoMetadataFromGooglePhotos(accessToken);

        if (videoMetadataList.isEmpty()) {
            throw new IOException("No video found");
        }

        for (VideoMetadata vm : videoMetadataList) {
            videoUploader.upload(vm, accessToken, appToken);
            videoUploader.deleteVideo(vm, appToken);
        }
    }
}
