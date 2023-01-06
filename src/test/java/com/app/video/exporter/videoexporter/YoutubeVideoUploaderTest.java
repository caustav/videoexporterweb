package com.app.video.exporter.videoexporter;

import com.app.videoexporter.YoutubeVideoUploader;
import com.app.videoexporter.models.VideoMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootTest
public class YoutubeVideoUploaderTest {

    YoutubeVideoUploader youtubeVideoUploader = new YoutubeVideoUploader();

    @Test
    void testUpload_VideoAlreadyExists() throws GeneralSecurityException, IOException {
        var videoMetadata = new VideoMetadata("ID_TEST_1",
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "ForBiggerBlazes.mp4", "12/12/2022");
        youtubeVideoUploader.upload(videoMetadata, "", "");
    }
}
