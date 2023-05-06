package de.oliver.fancyholograms;

import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

public class FancyHologramsConfig {

    private boolean muteVersionNotification;
    private boolean enableAutosave;
    private int autosaveInterval;
    private int visibilityDistance;

    public void reload(){
        FancyHolograms.getInstance().reloadConfig();
        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        enableAutosave = (boolean) ConfigHelper.getOrDefault(config, "enable_autosave", true);
        autosaveInterval = (int) ConfigHelper.getOrDefault(config, "autosave_interval", 15);
        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);

        FancyHolograms.getInstance().saveConfig();
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
    }

    public boolean isEnableAutosave() {
        return enableAutosave;
    }

    public int getAutosaveInterval() {
        return autosaveInterval;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }
}
