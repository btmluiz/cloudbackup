package dev.nardole.cloudbackup.storages;

import java.io.File;
import java.io.IOException;

public interface IStorage {
    public void backupFile(String fileName, File file) throws IOException;
}
