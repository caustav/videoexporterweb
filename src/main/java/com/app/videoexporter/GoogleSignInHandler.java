package com.app.videoexporter;

import com.app.videoexporter.controller.ApiController;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.auth.Credentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GoogleSignInHandler {

    Logger logger = LoggerFactory.getLogger(GoogleSignInHandler.class);

    public static final java.io.File DATA_STORE_DIR =
            new java.io.File(VideoExporterApplication.class.getResource("/").getPath(), "credentials");

    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final int LOCAL_RECEIVER_PORT = 61984;

    private static final String CREDENTIAL_PATH = "credentials.json";

    public static final List<String> REQUIRED_SCOPES =
            ImmutableList.of(
                    "https://www.googleapis.com/auth/youtube.upload",
                    "https://www.googleapis.com/auth/youtube",
                    "https://www.googleapis.com/auth/youtubepartner",
                    "https://www.googleapis.com/auth/youtube.force-ssl",
                    "https://www.googleapis.com/auth/photoslibrary.readonly",
                    "https://www.googleapis.com/auth/photoslibrary.appendonly");

    private GoogleClientSecrets googleClientSecrets = null;

    public GoogleClientSecrets getGoogleClientSecrets() throws IOException {
        if (googleClientSecrets == null) {
            googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(new FileInputStream(CREDENTIAL_PATH)));
        }
        return googleClientSecrets;
    }

    private GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() throws IOException, GeneralSecurityException {
        return new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    getGoogleClientSecrets(),
                    REQUIRED_SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(DATA_STORE_DIR))
                    .setAccessType("offline")
                    .build();
    }

    public Credential getCredentialThroughGoogleSignIn() throws IOException, GeneralSecurityException {

        var flow = getGoogleAuthorizationCodeFlow();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(LOCAL_RECEIVER_PORT).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public String getAuthorizationUrl() throws GeneralSecurityException, IOException {
        var flow = getGoogleAuthorizationCodeFlow();
        var googleClientSecrets = getGoogleClientSecrets();
        var redirectUri = googleClientSecrets.getInstalled().getRedirectUris().get(0);

        return flow.newAuthorizationUrl().setRedirectUri(redirectUri).toString();
    }

    public Credential getCredential(String authorizationCode) throws GeneralSecurityException, IOException {
        var flow = getGoogleAuthorizationCodeFlow();
        var redirectUri = googleClientSecrets.getInstalled().getRedirectUris().get(0);

        TokenResponse response = flow.newTokenRequest(authorizationCode).setRedirectUri(redirectUri).execute();
        return flow.createAndStoreCredential(response, "user");
    }

    public String getAccessTokenFromAuthorizationCode(String authorizationCode) throws GeneralSecurityException, IOException {
        return getCredential(authorizationCode).getAccessToken();
    }
}
