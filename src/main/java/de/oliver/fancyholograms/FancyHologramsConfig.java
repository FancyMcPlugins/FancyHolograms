package de.oliver.fancyholograms;

import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

public class FancyHologramsConfig {

    private boolean muteVersionNotification;
    private int visibilityDistance;

    public void reload(){
        FancyHolograms.getInstance().reloadConfig();
        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);

        FancyHolograms.getInstance().saveConfig();
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }
}
