package de.oliver.fancyholograms;

import de.oliver.fancylib.ConfigHelper;
import org.bukkit.configuration.file.FileConfiguration;

public class FancyHologramsConfig {

    private int visibilityDistance;

    public void reload(){
        FancyHolograms.getInstance().reloadConfig();
        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        visibilityDistance = (int) ConfigHelper.getOrDefault(config, "visibility_distance", 20);

        FancyHolograms.getInstance().saveConfig();
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }
}
