package com.app.videoexporter.controller;

import com.app.videoexporter.GoogleSignInHandler;
import com.app.videoexporter.IGooglePhotosMetadataDownloader;
import com.app.videoexporter.IYoutubeVideoUploader;
import com.app.videoexporter.models.VideoMetadata;
import com.app.videoexporter.models.VideoMetadataResponse;
import com.app.videoexporter.websocket.Message;
import com.app.videoexporter.websocket.WebSocketMessageService;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@EnableAsync
@CrossOrigin
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    private GoogleSignInHandler googleSignInHandler;
    @Autowired
    private IGooglePhotosMetadataDownloader googlePhotosMetadataDownloader;
    @Autowired
    private IYoutubeVideoUploader youtubeVideoUploader;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private WebSocketMessageService webSocketMessageService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void index(HttpServletResponse resp) throws GeneralSecurityException, IOException {
        resp.sendRedirect(googleSignInHandler.getAuthorizationUrl());
    }

    @RequestMapping(value = "/auth/google/callback", method = RequestMethod.GET)
    public void AuthorizationCodeCallback(HttpServletRequest req, HttpServletResponse resp)
                                throws GeneralSecurityException, IOException {
        String code = req.getParameter("code");
        var accessToken = googleSignInHandler.getAccessTokenFromAuthorizationCode(code);
        resp.sendRedirect("http://localhost:4200/videos?accessToken=" + accessToken);
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public VideoMetadataResponse FetchVideoMetadataList(@RequestHeader(name="Authorization") String token,
                                                        String pageToken) {
        var accessToken = token.split(" ")[1];
        return googlePhotosMetadataDownloader.findVideoMetadataFromGooglePhotos(accessToken, pageToken);
    }

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public void UploadVideo(@RequestBody VideoMetadata videoMetadata,
                            @RequestHeader(name="Authorization") String token,
                            @RequestHeader(name="AppToken") String appToken)
            throws GeneralSecurityException, IOException {
        try {
            var accessToken = token.split(" ")[1];
            youtubeVideoUploader.upload(videoMetadata, accessToken, appToken);
        }catch(Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @MessageMapping("/hello")
    @SendTo("/topic/upload_status")
    public Message welcomeWSClient(String id) {
        return webSocketMessageService.getMessage();
    }
}
