package com.app.videoexporter;

import com.app.videoexporter.models.VideoMetadata;
import com.app.videoexporter.websocket.WebSocketMessageService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class YoutubeVideoUploader implements IYoutubeVideoUploader{

    Logger logger = LoggerFactory.getLogger(YoutubeVideoUploader.class);

    static String TEMP_FOLDER_NAME = ".temp";

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private WebSocketMessageService webSocketMessageService;

    private static final String APPLICATION_NAME = "Video Exporter";
    private static final JsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();

    private YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, GSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void uploadVideo(VideoMetadata videoMetadata, String accessToken, String appToken) throws IOException, GeneralSecurityException {
        YouTube youtubeService = getService();

        var videoFileDownloadLocation = getVideoFileDownloadLocation(videoMetadata, appToken);
        var videoFile = new File(videoFileDownloadLocation);
        var bis = new BufferedInputStream(new FileInputStream(videoFile));

        var mediaContent = new InputStreamContent("application/octet-stream", bis);
        mediaContent.setLength(videoFile.length());

        Video video = new Video();
        var videoSnippet  = new VideoSnippet();
        videoSnippet.setTitle(videoMetadata.getName());
        videoSnippet.setTags(List.of(videoMetadata.getId()));
        video.setSnippet(videoSnippet);

        YouTube.Videos.Insert request = youtubeService.videos().insert(List.of("snippet"), video, mediaContent);
        var videoUploaded = request.setOauthToken(accessToken).execute();

        logger.info(String.format("video with title: %s and id: %s, uploaded successfully.",
                videoUploaded.getSnippet().getTitle(), videoUploaded.getId()));

        bis.close();
    }

    private String getVideoFileDownloadLocation(VideoMetadata videoMetadata, String appToken) throws IOException {
        var url = new URL(String.format("%s=dv", videoMetadata.getBaseUrl()));
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

        var fileLocation = String.format("%s/%s/", TEMP_FOLDER_NAME, appToken);

        Files.createDirectories(Paths.get(fileLocation));

        var videoFileDownloadLocation = fileLocation + videoMetadata.getName();

        try(FileOutputStream fileOutputStream = new FileOutputStream(videoFileDownloadLocation);
            FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return videoFileDownloadLocation;
    }

    @Async
    @Override
    public void upload(VideoMetadata videoMetadata, String accessToken, String appToken) throws GeneralSecurityException, IOException {
        try{
            //uploadVideo(videoMetadata, accessToken, appToken);
            Thread.sleep(3000);
            template.convertAndSend(String.format("/topic/%s", appToken),
                    webSocketMessageService.createMessage(0));
        } catch(Exception e) {
            template.convertAndSend(String.format("/topic/%s", appToken),
                    webSocketMessageService.createMessage(-1));
            throw new IOException(e);
        }
    }

    @Async
    @Override
    public void deleteVideo(VideoMetadata videoMetadata, String appToken) throws IOException {
        var videoFileDownloadLocation = String.format("%s/%s/%s", TEMP_FOLDER_NAME, appToken, videoMetadata.getName());
        var file = new File(videoFileDownloadLocation);
        if (!file.delete()) {
            throw new IOException("Unable to delete video");
        }
    }
}
