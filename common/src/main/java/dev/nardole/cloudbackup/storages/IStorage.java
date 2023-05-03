package dev.nardole.cloudbackup.storages;

import java.io.File;
import java.io.IOException;

public interface IStorage {
    void backupFile(String fileName, String worldName, File file) throws IOException;
}
