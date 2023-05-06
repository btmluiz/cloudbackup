package dev.nardole.cloudbackup.threads;

import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.config.MainConfig;
import dev.nardole.cloudbackup.data.BackupData;
import dev.nardole.cloudbackup.storages.CloudStorage;
import dev.nardole.cloudbackup.storages.IStorage;
import dev.nardole.cloudbackup.util.FileUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupThread extends Thread {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_')
            .appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .toFormatter();
    private final MinecraftServer server;

    public static final Logger LOGGER = LogManager.getLogger();

    private final boolean quiet;

    private final long lastSaved;

    private final boolean fullBackup;

    private BackupThread(@NotNull MinecraftServer server, boolean quiet, BackupData backupData) {
        this.server = server;
        this.quiet = quiet;
        this.fullBackup = true;
        if (backupData == null) {
            this.lastSaved = 0;
        } else {
            this.lastSaved = backupData.getLastSaved();
        }
        this.setName("CloudBackup");
        this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
    }

    public static boolean tryCreateBackup(MinecraftServer server) {
        BackupData backupData = BackupData.get(server);

        if (CloudBackup.getConfig().enableBackup && !backupData.isPaused() && System.currentTimeMillis() - CloudBackup.getConfig().getTimer() > backupData.getLastSaved()) {
            BackupThread thread = new BackupThread(server, false, backupData);
            thread.start();
            backupData.updateSaveTime(System.currentTimeMillis());

            if (thread.fullBackup) {
                backupData.updateFullBackupTime(System.currentTimeMillis());
            }

            return true;
        }

        return false;
    }

    public static void createBackup(MinecraftServer server, boolean quiet) {
        BackupThread thread = new BackupThread(server, quiet, null);
        thread.start();
    }

    public void deleteFiles() {
    }

    @Override
    public void run() {
        if (CloudBackup.getConfig().enableBackup) {
            try {
                this.deleteFiles();

                Files.createDirectories(CloudBackup.getConfig().getOutputPath());
                long start = System.currentTimeMillis();
                this.broadcast("backup_started", Style.EMPTY.withColor(ChatFormatting.GOLD));
                this.makeWorldBackup();
                long end = System.currentTimeMillis();
                String time = Timer.getTimer(end - start);
                this.broadcast("backup_finished", Style.EMPTY.withColor(ChatFormatting.GOLD), time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.broadcast("backup_disabled", Style.EMPTY.withColor(ChatFormatting.RED));
        }
    }

    private void broadcast(String message, Style style, Object... parameters) {
        if (!this.quiet) {
            this.server.execute(() -> this.server.getPlayerList().getPlayers().forEach(player -> {
                if (this.server.isSingleplayer() || player.hasPermissions(2)) {
                    player.sendMessage(BackupThread.component(message, parameters).withStyle(style), player.getUUID());
                }
            }));
        }
    }

    public static MutableComponent component(String key, Object... parameters) {
        return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloudbackup.chat_prefix").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)), new TranslatableComponent(key, parameters));
    }

    private void makeWorldBackup() throws IOException {
        String fileName = this.server.getWorldData().getLevelName() + "_" + LocalDateTime.now().format(FORMATTER);
        Path path = CloudBackup.getConfig().getOutputPath();

        try {
            Files.createDirectories(Files.exists(path) ? path.toAbsolutePath() : path);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        Path outputFile = path.resolve(FileUtil.findAvailableName(path, fileName, ".zip"));
        final ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(outputFile)));

        zipStream.setLevel(0);

        Path levelName = Paths.get(this.server.getWorldData().getLevelName());
        Path levelPath = this.server.getWorldPath(LevelResource.ROOT).toAbsolutePath();
        try {
            Files.walkFileTree(levelPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!file.endsWith("session.lock")) {
                        long lastModified = file.toFile().lastModified();
                        if (BackupThread.this.fullBackup || lastModified - BackupThread.this.lastSaved > 0) {
                            String completePath = levelName.resolve(levelPath.relativize(file)).toString().replace('\\', '/');
                            ZipEntry zipEntry = new ZipEntry(completePath);
                            zipStream.putNextEntry(zipEntry);
                            com.google.common.io.Files.asByteSource(file.toFile()).copyTo(zipStream);
                            zipStream.closeEntry();
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            try {
                zipStream.close();
            } catch (IOException e1) {
                e.addSuppressed(e1);
            }

            throw e;
        }

        zipStream.close();

        this.tryUploadBackup(fileName, server.getWorldPath(LevelResource.ROOT).getParent().getFileName().toString(), outputFile);
    }

    private void tryUploadBackup(String fileName, String worldName, Path outputFile) {
        MainConfig config = CloudBackup.getConfig();

        for (CloudStorage storage: CloudStorage.values()) {
            if (config.getStorageConfig(storage).enabled) {
                try {
                    IStorage iStorage = storage.getStorage().getDeclaredConstructor().newInstance();

                    iStorage.backupFile(fileName, worldName, outputFile.toFile());
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException | NullPointerException | IOException e) {
                    this.broadcast("cloudbackup.cannot_upload_backup", Style.EMPTY.withColor(ChatFormatting.RED), storage.getDisplayName());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static Date getLastBackupDate(MinecraftServer server) {
        BackupData backupData = BackupData.get(server);
        return new Date(backupData.getLastSaved());
    }

    public static Component getLastBackupDateFormatted(MinecraftServer server) {
        BackupData backupData = BackupData.get(server);

        // Get the system date time format
        DateFormat systemDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        systemDateFormat.setTimeZone(TimeZone.getDefault());

        return BackupThread.component("cloudbackup.last_backup", systemDateFormat.format(new Date(backupData.getLastSaved())));
    }

    private static class Timer {
        private static final SimpleDateFormat SECONDS = new SimpleDateFormat("s.SSS");
        private static final SimpleDateFormat MINUTES = new SimpleDateFormat("mm:ss");
        private static final SimpleDateFormat HOURS = new SimpleDateFormat("HH:mm");

        public static String getTimer(long milliseconds) {
            Date date = new Date(milliseconds);
            double seconds = milliseconds / 1000d;
            if (seconds < 60) {
                return SECONDS.format(date) + "s";
            } else if (seconds < 3600) {
                return MINUTES.format(date) + "min";
            } else {
                return HOURS.format(date) + "h";
            }
        }
    }
}
