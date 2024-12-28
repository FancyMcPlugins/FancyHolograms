package de.oliver.fancyholograms.config;

import de.oliver.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.featureFlags.FeatureFlag;
import de.oliver.fancylib.featureFlags.FeatureFlagConfig;

public class FHFeatureFlags {

    public static final FeatureFlag DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS = new FeatureFlag("disable-holograms-for-bedrock-players", "Do not show holograms to bedrock players", false);
    public static final FeatureFlag DISABLE_HOLOGRAMS_FOR_OLD_CLIENTS = new FeatureFlag("disable-holograms-for-old-clients", "Do not show holograms to clients with a version older than 1.19.4", false);

    public static void load() {
        FeatureFlagConfig config = new FeatureFlagConfig(FancyHologramsPlugin.get());
        config.addFeatureFlag(DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS);
        config.addFeatureFlag(DISABLE_HOLOGRAMS_FOR_OLD_CLIENTS);
        config.load();
    }

}
