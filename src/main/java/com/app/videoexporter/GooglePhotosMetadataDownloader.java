package com.app.videoexporter;

import com.app.videoexporter.models.MediaResponse;
import com.app.videoexporter.models.Search;
import com.app.videoexporter.models.VideoMetadata;
import com.app.videoexporter.models.VideoMetadataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GooglePhotosMetadataDownloader implements IGooglePhotosMetadataDownloader{

    private static final String API_ENDPOINT = "https://photoslibrary.googleapis.com/v1/mediaItems:search";

    Logger logger = LoggerFactory.getLogger(GooglePhotosMetadataDownloader.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<VideoMetadata> findAllVideoMetadataFromGooglePhotos(String accessToken) {
        return fetchAllVideoMetadata(accessToken);
    }

    @Override
    public VideoMetadataResponse findVideoMetadataFromGooglePhotos(String accessToken, String pageToken) {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        var search = Search.BuildQueryWithMediaTypes(Arrays.asList("VIDEO"), "100", pageToken);
        HttpEntity<Search> entity = new HttpEntity<>(search, headers);

        var mediaResponse = this.restTemplate.postForObject(API_ENDPOINT, entity, MediaResponse.class);
        var nextPageToken = mediaResponse.getNextPageToken();

        var items = mediaResponse.getMediaItems().stream()
                .map(mediaItem -> new VideoMetadata(mediaItem.getId(),
                        mediaItem.getBaseUrl(), mediaItem.getFilename(), mediaItem.getMediaMetadata().getCreationTime()));

        var videoMetadataResponse = new VideoMetadataResponse();
        videoMetadataResponse.setVideoMetadataList(items.collect(Collectors.toList()));
        videoMetadataResponse.setNextPageToken(nextPageToken);
        return videoMetadataResponse;
    }

    private List<VideoMetadata> fetchAllVideoMetadata(String accessToken) {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        List<VideoMetadata> videoMetadataList = new ArrayList();
        String pageToken = "";
        do {
            var search = Search.BuildQueryWithMediaTypes(Arrays.asList("VIDEO"), "100", pageToken);
            HttpEntity<Search> entity = new HttpEntity<>(search, headers);

            var mediaResponse = this.restTemplate.postForObject(API_ENDPOINT, entity, MediaResponse.class);
            pageToken = mediaResponse.getNextPageToken();

            var items = mediaResponse.getMediaItems().stream()
                                                .map(mediaItem -> new VideoMetadata(mediaItem.getId(),
                                                    mediaItem.getBaseUrl(), mediaItem.getFilename(),
                                                        mediaItem.getMediaMetadata().getCreationTime()));

            videoMetadataList.addAll(items.collect(Collectors.toList()));
            logger.info(String.format("number of video info fetched : %d\n", videoMetadataList.size()));
        } while(!(pageToken == null));
        return videoMetadataList;
    }
}
