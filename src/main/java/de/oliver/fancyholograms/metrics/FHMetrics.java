package de.oliver.fancyholograms.metrics;

import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.config.FHFeatureFlags;
import de.oliver.fancyholograms.main.FancyHolograms;
import de.oliver.fancylib.Metrics;
import org.bukkit.Bukkit;

public class FHMetrics {

    private ExtendedFancyLogger logger;
    private FancyAnalyticsAPI fancyAnalytics;
    private boolean isDevelopmentBuild = true;

    public FHMetrics() {
        logger = FancyHolograms.get().getFancyLogger();
        isDevelopmentBuild = !FancyHolograms.get().getVersionConfig().getBuild().equalsIgnoreCase("undefined");
    }

    public void register() {
        fancyAnalytics = new FancyAnalyticsAPI("3b77bd59-2b01-46f2-b3aa-a9584401797f", "E2gW5zc2ZTk1OGFkNGY2ZDQ0ODlM6San");
        fancyAnalytics.getConfig().setDisableLogging(true);

        fancyAnalytics.registerMinecraftPluginMetrics(FancyHolograms.get());
        fancyAnalytics.getExceptionHandler().registerLogger(FancyHolograms.get().getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(Bukkit.getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(logger);

        HologramManager manager = FancyHolograms.get().getHologramManager();

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> FancyHolograms.get().getVersionConfig().getHash().substring(0, 7)));

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("server_size", () -> {
            long onlinePlayers = Bukkit.getOnlinePlayers().size();

            if (onlinePlayers == 0) {
                return "empty";
            }

            if (onlinePlayers <= 25) {
                return "small";
            }

            if (onlinePlayers <= 100) {
                return "medium";
            }

            if (onlinePlayers <= 500) {
                return "large";
            }

            return "very_large";
        }));

        fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_holograms", () -> (double) manager.getHolograms().size()));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_update_notifications", () -> FancyHolograms.get().getFHConfiguration().areVersionNotificationsMuted() ? "false" : "true"));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("fflag_disable_holograms_for_bedrock_players", () -> FHFeatureFlags.DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS.isEnabled() ? "true" : "false"));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("using_development_build", () -> isDevelopmentBuild ? "true" : "false"));

        fancyAnalytics.registerStringArrayMetric(new MetricSupplier<>("hologram_type", () -> {
            if (manager == null) {
                return new String[0];
            }

            return manager.getHolograms().stream()
                    .map(h -> h.getData().getType().name())
                    .toArray(String[]::new);
        }));


        fancyAnalytics.initialize();
    }

    public void registerLegacy() {
        Metrics metrics = new Metrics(FancyHolograms.get(), 17990);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_holograms", () -> FancyHolograms.get().getHologramManager().getHolograms().size()));
        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> FancyHolograms.get().getFHConfiguration().areVersionNotificationsMuted() ? "No" : "Yes"));
        metrics.addCustomChart(new Metrics.SimplePie("using_development_build", () -> isDevelopmentBuild ? "Yes" : "No"));
    }

    public FancyAnalyticsAPI getFancyAnalytics() {
        return fancyAnalytics;
    }
}
