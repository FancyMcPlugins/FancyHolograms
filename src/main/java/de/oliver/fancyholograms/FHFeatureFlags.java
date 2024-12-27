package de.oliver.fancyholograms;

import de.oliver.fancyholograms.main.FancyHolograms;
import de.oliver.fancylib.featureFlags.FeatureFlag;
import de.oliver.fancylib.featureFlags.FeatureFlagConfig;

public class FHFeatureFlags {

    public static final FeatureFlag DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS = new FeatureFlag("disable-holograms-for-bedrock-players", "Do not show holograms to bedrock players", false);

    public static void load() {
        FeatureFlagConfig config = new FeatureFlagConfig(FancyHolograms.get());
        config.addFeatureFlag(DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS);
        config.load();
    }

}
