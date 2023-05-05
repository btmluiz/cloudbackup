package dev.nardole.cloudbackup.data;

import dev.nardole.cloudbackup.CloudBackup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class BackupData extends SavedData {

    private static final String NAME = CloudBackup.MOD_ID + "_backupData";

    private long lastSaved;

    private long lastFullBackup;

    private boolean paused = false;

    public BackupData(String string) {
        super(string);
    }

    public  BackupData() {
        this(NAME);
    }

    public static BackupData get(ServerLevel serverLevel) {
        return BackupData.get(serverLevel.getServer());
    }

    public static BackupData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(BackupData::new, NAME);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.lastSaved = nbt.getLong("lastSaved");
        this.lastFullBackup = nbt.getLong("lastFullBackup");
        this.paused = nbt.getBoolean("paused");
    }

    @NotNull
    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("lastSaved", this.lastSaved);
        nbt.putLong("lastFullBackup", this.lastFullBackup);
        nbt.putBoolean("paused", this.paused);
        return nbt;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public long getLastSaved() {
        return lastSaved;
    }

    public void updateSaveTime(long time) {
        this.lastSaved = time;
        this.setDirty();
    }

    public void updateFullBackupTime(long time) {
        this.lastFullBackup = time;
        this.setDirty();
    }
}
