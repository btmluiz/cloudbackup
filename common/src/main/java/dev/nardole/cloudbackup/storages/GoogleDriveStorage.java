package dev.nardole.cloudbackup.storages;

import com.google.api.client.auth.oauth2.Credential;
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
import com.google.auth.oauth2.GoogleCredentials;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final ResourceLocation CREDENTIALS = new ResourceLocation("cloudbackup", "credentials.json");

    public static final Logger LOGGER = LogManager.getLogger();

    private GoogleAuthorizationCodeFlow currentFlow;

    private LocalServerReceiver receiver;

    private GoogleClientSecrets clientSecrets;

    private Boolean hasError = false;

    public GoogleDriveStorage() {
        this.initGCPCredentials();
    }

    public void initFlow() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        this.currentFlow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        if (this.receiver == null) {
            LOGGER.info("Listing on backgroud");
            receiver = new LocalServerReceiver.Builder().build();
        }
    }

    private void initGCPCredentials() {
        try {
            InputStream gcp_credentials = Minecraft.getInstance().getResourceManager().getResource(CREDENTIALS).getInputStream();
            InputStreamReader gcp_reader = new InputStreamReader(gcp_credentials);
            this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, gcp_reader);
        } catch (IOException e) {
            LOGGER.error("Could initialize GCP Credentials");
            LOGGER.error(e.getMessage());
            this.hasError = true;
        }
    }

    public String getAuthorizationUrl() throws GeneralSecurityException, IOException {
        if (this.currentFlow == null || this.receiver == null) {
            this.initFlow();
        }

        return this.currentFlow.newAuthorizationUrl().setRedirectUri(this.receiver.getRedirectUri()).build();
    }

    private Credential getCredentials() {
        try {
            return this.currentFlow.loadCredential("user");
        } catch (IOException e) {
            return null;
        }
    }

    public void listen() throws IOException {
        if (this.currentFlow == null || this.receiver == null) {
            try {
                this.initFlow();
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not initialize flow");
                LOGGER.error(e.getMessage());
                return;
            }
        }

        this.receiver.stop();
        this.receiver = new LocalServerReceiver.Builder().build();
        this.currentFlow.newAuthorizationUrl().setRedirectUri(this.receiver.getRedirectUri()).build();
        this.receiver.waitForCode();
    }

    @Override
    public void backupFile(String fileName, java.io.File file) throws IOException {
        if (this.getCredentials() != null) {
            try {
                Drive service = this.getDriveService();

                File fileMetaData = new File();
                fileMetaData.setName(fileName);

                FileContent mediaContent = new FileContent("application/zip", file);

                service.files().create(fileMetaData, mediaContent)
                        .setFields("id")
                        .execute();
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not upload file: " + fileName);
                LOGGER.error(e.getMessage());
            }
        }
    }

    public Drive getDriveService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Boolean getHasError() {
        return hasError;
    }

    private GoogleCredentials getAuthenticatedCredentials() throws IOException {
        return GoogleCredentials.getApplicationDefault().createScoped(SCOPES);
    }
}
