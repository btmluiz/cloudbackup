package dev.nardole.cloudbackup.config;

import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.CloudBackupConfig;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Deprecated(since = "1.0.7")
public class ConfigHandler {

    private static String getFileName(Class<?> configClass) {
        return configClass.getAnnotation(FileName.class).value() + ".yml";
    }

    public static Path getModConfigDir() {
        Path configDir = CloudBackupConfig.getConfigDir().resolve(CloudBackup.MOD_ID);

        if (!configDir.toFile().exists() && !configDir.toFile().mkdirs()) {
            throw new RuntimeException("Failed to create config directory");
        }

        return configDir;
    }

    private static DumperOptions getDumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        return options;
    }

    private static Representer getRepresenter(Class<?> configClass, @Nullable DumperOptions options) {
        Representer representer = new Representer(options != null ? options : getDumperOptions());
        representer.addClassTag(configClass, Tag.MAP);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        return representer;
    }

    public static <T> T loadConfig(Class<T> configClass) {
        String fileName = getFileName(configClass);

        Path path = getModConfigDir().resolve(fileName);

        if (!path.toFile().exists()) {
            return null;
        }

        LoaderOptions options = new LoaderOptions();
        Constructor constructor = new Constructor(configClass, options);
        Yaml yaml = new Yaml(constructor, getRepresenter(configClass, null));

        try {
            InputStream inputStream = Files.newInputStream(path.toFile().toPath());
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
