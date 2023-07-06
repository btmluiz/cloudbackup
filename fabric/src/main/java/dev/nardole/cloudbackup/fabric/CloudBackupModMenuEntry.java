package dev.nardole.cloudbackup.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.nardole.cloudbackup.ClothScreen;

public class CloudBackupModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ClothScreen::getConfigScreenByCloth;
    }
}
