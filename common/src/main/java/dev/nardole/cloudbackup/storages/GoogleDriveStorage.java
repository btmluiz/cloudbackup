package dev.nardole.cloudbackup.storages;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.collect.ImmutableList;
import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.config.MainConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleDriveStorage implements IStorage {

    private static final String APPLICATION_NAME = "Cloud Backup";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final String OAUTH2_SCOPE_USERINFO_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";

    private static final List<String> SCOPES = ImmutableList.of(DriveScopes.DRIVE_FILE, OAUTH2_SCOPE_USERINFO_PROFILE, "email");

    private static final ResourceLocation CREDENTIALS = new ResourceLocation("cloudbackup", "credentials.json");

    public static final Logger LOGGER = LogManager.getLogger();

    private final GoogleAuthorizationCodeFlow authorizationCodeFlow;

    private final LocalServerReceiver receiver;

    private String browserUrl;

    private Drive service;

    private Credential userCredentials;

    public GoogleDriveStorage() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream gcp_credentials = Minecraft.getInstance().getResourceManager().open(CREDENTIALS);
        InputStreamReader gcp_reader = new InputStreamReader(gcp_credentials);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, gcp_reader);
        authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        receiver = new LocalServerReceiver.Builder().build();
    }

    private Credential getCredentials() {
        if (this.userCredentials != null) {
            return this.userCredentials;
        }

        try {
            LOGGER.info("Loading credentials");
            userCredentials = this.authorizationCodeFlow.loadCredential("user");
            if (userCredentials == null) {
                LOGGER.info("No credentials found");
            } else {
                LOGGER.info("Credentials loaded");
                LOGGER.info("Access token: " + userCredentials.getAccessToken());
            }
            return userCredentials;
        } catch (IOException e) {
            LOGGER.error("Could not load credentials", e);
            return null;
        }
    }

    @Override
    public void backupFile(String fileName, String worldName, java.io.File file) throws IOException {
        if (this.getCredentials() != null) {
            try {
                service = this.getDriveService();

                MainConfig.StorageConfig config = CloudBackup.getConfig().googleDrive;
                String folderId = getFolderId(service, config.uploadDir);


                if (config.uploadDir != null && folderId == null) {
                    folderId = createFolder(service, config.uploadDir);
                }

                if (config.makeWorldDir) {
                    folderId = createFolder(service, worldName, folderId);
                }

                File fileMetaData = new File();
                fileMetaData.setName(fileName);

                if (folderId != null) {
                    fileMetaData.setParents(Collections.singletonList(folderId));
                }

                FileContent mediaContent = new FileContent("application/zip", file);

                service.files().create(fileMetaData, mediaContent)
                        .setFields("id")
                        .execute();
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not upload file: " + fileName, e);
            }
        }
    }

    private static String getFolderId(Drive service, String folderName) {
        try {
            FileList result = service.files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and name='" + folderName + "'")
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                return null;
            } else {
                return files.get(0).getId();
            }
        } catch (IOException e) {
            LOGGER.error("Could not get folder: " + folderName, e);
        }
        return null;
    }

    private static String createFolder(Drive service, String folderName, @Nullable String parentId) {
        File folderMetaData = new File();
        folderMetaData.setName(folderName);
        folderMetaData.setMimeType("application/vnd.google-apps.folder");

        if (parentId != null) {
            folderMetaData.setParents(Collections.singletonList(parentId));
        }

        try {
            File folder = service.files().create(folderMetaData)
                    .setFields("id")
                    .execute();
            return folder.getId();
        } catch (IOException e) {
            LOGGER.error("Could not create folder: " + folderName);
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    private static String createFolder(Drive service, String folderName) {
        return createFolder(service, folderName, null);
    }

    public Drive getDriveService() throws GeneralSecurityException, IOException {
        if (service != null) {
            return service;
        }

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void disconnect() throws IOException {
        this.authorizationCodeFlow.getCredentialDataStore().clear();
        userCredentials = null;
    }

    public boolean isConnected() {
        return this.getCredentials() != null;
    }

    public ThreadedReceiver getReceiver() {
        return new ThreadedReceiver();
    }

    public String getBrowserUrl() {
        return browserUrl;
    }

    public class ThreadedReceiver extends Thread {
        @Override
        public void run() {
            try {
                if (authorizationCodeFlow.loadCredential("user") != null) {
//                    disconnect();
                    return;
                }

                String redirectUri = receiver.getRedirectUri();
                AuthorizationCodeRequestUrl authorizationUrl = authorizationCodeFlow.newAuthorizationUrl().setRedirectUri(redirectUri);
                browserUrl = authorizationUrl.build();

                String code = receiver.waitForCode();
                TokenResponse response = authorizationCodeFlow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

                authorizationCodeFlow.createAndStoreCredential(response, "user");
            } catch (IOException e) {
                LOGGER.error("Could not stop receiver", e);
            } finally {
                try {
                    receiver.stop();
                } catch (IOException e) {
                    LOGGER.error("Could not stop receiver", e);
                }
            }
        }
    }
}
