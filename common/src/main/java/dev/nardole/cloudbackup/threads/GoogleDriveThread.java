package dev.nardole.cloudbackup.threads;

import dev.nardole.cloudbackup.storages.GoogleDriveStorage;

public class GoogleDriveThread extends Thread {
    GoogleDriveStorage googleDriveStorage;

    public GoogleDriveThread(GoogleDriveStorage googleDriveStorage) {
        this.googleDriveStorage = googleDriveStorage;
    }

    @Override
    public void run() {
        try {
//            googleDriveStorage.startConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
