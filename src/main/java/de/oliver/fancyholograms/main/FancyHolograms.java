package de.oliver.fancyholograms.main;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyanalytics.api.FancyAnalyticsAPI;
import de.oliver.fancyanalytics.api.metrics.MetricSupplier;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancyholograms.HologramManagerImpl;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramConfiguration;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.HologramStorage;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.FancyHologramsCMD;
import de.oliver.fancyholograms.commands.FancyHologramsTestCMD;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.config.FHConfiguration;
import de.oliver.fancyholograms.config.FHFeatureFlags;
import de.oliver.fancyholograms.hologram.version.*;
import de.oliver.fancyholograms.listeners.BedrockPlayerListener;
import de.oliver.fancyholograms.listeners.NpcListener;
import de.oliver.fancyholograms.listeners.PlayerListener;
import de.oliver.fancyholograms.listeners.WorldListener;
import de.oliver.fancyholograms.storage.FlatFileHologramStorage;
import de.oliver.fancyholograms.storage.converter.FHConversionRegistry;
import de.oliver.fancyholograms.util.PluginUtils;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import de.oliver.fancysitula.api.IFancySitula;
import de.oliver.fancysitula.api.utils.ServerVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class FancyHolograms extends JavaPlugin implements FancyHologramsPlugin {

    private static @Nullable FancyHolograms INSTANCE;
    private final ExtendedFancyLogger fancyLogger;
    private final VersionFetcher versionFetcher = new MasterVersionFetcher("FancyHolograms");
    private final VersionConfig versionConfig = new VersionConfig(this, versionFetcher);
    private final ScheduledExecutorService hologramThread = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setNameFormat("FancyHolograms-Holograms")
                    .build()
    );
    private final ExecutorService fileStorageExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setPriority(Thread.MIN_PRIORITY + 1)
                    .setNameFormat("FancyHolograms-FileStorageExecutor")
                    .build()
    );
    private FancyAnalyticsAPI fancyAnalytics;
    private HologramConfiguration configuration = new FHConfiguration();
    private HologramStorage hologramStorage = new FlatFileHologramStorage();
    private @Nullable HologramManagerImpl hologramsManager;

    public FancyHolograms() {
        INSTANCE = this;

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyHolograms/logs/FH-logs-" + date + ".txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JsonAppender jsonAppender = new JsonAppender(false, false, true, logsFile.getPath());
        this.fancyLogger = new ExtendedFancyLogger("FancyHolograms", LogLevel.INFO, List.of(consoleAppender, jsonAppender), new ArrayList<>());
    }

    public static @NotNull FancyHolograms get() {
        return Objects.requireNonNull(INSTANCE, "plugin is not initialized");
    }

    public static boolean canGet() {
        return INSTANCE != null;
    }

    @Override
    public void onLoad() {
        final var adapter = resolveHologramAdapter();

        if (adapter == null) {
            List<String> supportedVersions = new ArrayList<>(List.of("1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4"));
            supportedVersions.addAll(ServerVersion.getSupportedVersions());

            fancyLogger.warn("""
                    --------------------------------------------------
                    Unsupported minecraft server version.
                    Please update the server to one of (%s).
                    Disabling the FancyHolograms plugin.
                    --------------------------------------------------
                    """.formatted(String.join(" / ", supportedVersions)));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        hologramsManager = new HologramManagerImpl(this, adapter);

        fancyLogger.info("Successfully loaded FancyHolograms version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        getHologramConfiguration().reload(this); // initialize configuration

        new FancyLib(INSTANCE); // initialize FancyLib

        if (!ServerSoftware.isPaper()) {
            fancyLogger.warn("""
                    --------------------------------------------------
                    It is recommended to use Paper as server software.
                    Because you are not using paper, the plugin
                    might not work correctly.
                    --------------------------------------------------
                    """);
        }

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(getHologramConfiguration().getLogLevel());
        } catch (IllegalArgumentException e) {
            logLevel = LogLevel.INFO;
        }
        fancyLogger.setCurrentLevel(logLevel);
        IFancySitula.LOGGER.setCurrentLevel(logLevel);

        FHFeatureFlags.load();

        reloadCommands();

        registerListeners();

        versionConfig.load();
        if (!getHologramConfiguration().areVersionNotificationsMuted()) {
            checkForNewerVersion();
        }

        registerMetrics();

        getHologramsManager().initializeTasks();

        if (getHologramConfiguration().isAutosaveEnabled()) {
            getHologramThread().scheduleAtFixedRate(() -> {
                if (hologramsManager != null) {
                    hologramsManager.saveHolograms();
                }
            }, getHologramConfiguration().getAutosaveInterval(), getHologramConfiguration().getAutosaveInterval() * 60L, TimeUnit.SECONDS);
        }

        FHConversionRegistry.registerBuiltInConverters();

        fancyLogger.info("Successfully enabled FancyHolograms version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        hologramsManager.saveHolograms();
        hologramThread.shutdown();
        fileStorageExecutor.shutdown();
        INSTANCE = null;

        fancyLogger.info("Successfully disabled FancyHolograms version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public JavaPlugin getPlugin() {
        return INSTANCE;
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    public @NotNull VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public @NotNull VersionConfig getVersionConfig() {
        return versionConfig;
    }

    @ApiStatus.Internal
    public @NotNull HologramManagerImpl getHologramsManager() {
        return Objects.requireNonNull(this.hologramsManager, "plugin is not initialized");
    }

    @Override
    public HologramManager getHologramManager() {
        return Objects.requireNonNull(this.hologramsManager, "plugin is not initialized");
    }

    @Override
    public HologramConfiguration getHologramConfiguration() {
        return configuration;
    }

    @Override
    public void setHologramConfiguration(HologramConfiguration configuration, boolean reload) {
        this.configuration = configuration;

        if (reload) {
            configuration.reload(this);
            reloadCommands();
        }
    }

    @Override
    public HologramStorage getHologramStorage() {
        return hologramStorage;
    }

    @Override
    public void setHologramStorage(HologramStorage storage, boolean reload) {
        this.hologramStorage = storage;

        if (reload) {
            getHologramsManager().reloadHolograms();
        }
    }

    public ScheduledExecutorService getHologramThread() {
        return hologramThread;
    }

    public ExecutorService getFileStorageExecutor() {
        return this.fileStorageExecutor;
    }

    private @Nullable Function<HologramData, Hologram> resolveHologramAdapter() {
        final var version = Bukkit.getMinecraftVersion();

        // check if the server version is supported by FancySitula
        if (ServerVersion.isVersionSupported(version)) {
            return HologramImpl::new;
        }

        return switch (version) {
            case "1.20.3", "1.20.4" -> Hologram1_20_4::new;
            case "1.20.2" -> Hologram1_20_2::new;
            case "1.20", "1.20.1" -> Hologram1_20_1::new;
            case "1.19.4" -> Hologram1_19_4::new;
            default -> null;
        };
    }

    public void reloadCommands() {
        Collection<Command> commands = Arrays.asList(new HologramCMD(this), new FancyHologramsCMD(this));

        if (getHologramConfiguration().isRegisterCommands()) {
            commands.forEach(command -> getServer().getCommandMap().register("fancyholograms", command));
        } else {
            commands.stream().filter(Command::isRegistered).forEach(command ->
                    command.unregister(getServer().getCommandMap()));
        }

        if (false) {
            FancyHologramsTestCMD fancyHologramsTestCMD = new FancyHologramsTestCMD(this);
            getServer().getCommandMap().register("fancyholograms", fancyHologramsTestCMD);
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        if (PluginUtils.isFancyNpcsEnabled()) {
            getServer().getPluginManager().registerEvents(new NpcListener(this), this);
        }

        if (FHFeatureFlags.DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS.isEnabled() && PluginUtils.isFloodgateEnabled()) {
            getServer().getPluginManager().registerEvents(new BedrockPlayerListener(), this);
        }
    }

    private void checkForNewerVersion() {
        final var current = new ComparableVersion(versionConfig.getVersion());

        supplyAsync(getVersionFetcher()::fetchNewestVersion).thenApply(Objects::requireNonNull).whenComplete((newest, error) -> {
            if (error != null || newest.compareTo(current) <= 0) {
                return; // could not get the newest version or already on latest
            }

            fancyLogger.warn("""
                    
                    -------------------------------------------------------
                    You are not using the latest version of the FancyHolograms plugin.
                    Please update to the newest version (%s).
                    %s
                    -------------------------------------------------------
                    """.formatted(newest, getVersionFetcher().getDownloadUrl()));
        });
    }

    private void registerMetrics() {
        boolean isDevelopmentBuild = !versionConfig.getBuild().equalsIgnoreCase("undefined");

        Metrics metrics = new Metrics(this, 17990);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_holograms", () -> hologramsManager.getHolograms().size()));
        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> configuration.areVersionNotificationsMuted() ? "No" : "Yes"));
        metrics.addCustomChart(new Metrics.SimplePie("using_development_build", () -> isDevelopmentBuild ? "Yes" : "No"));

        fancyAnalytics = new FancyAnalyticsAPI("3b77bd59-2b01-46f2-b3aa-a9584401797f", "E2gW5zc2ZTk1OGFkNGY2ZDQ0ODlM6San");
        fancyAnalytics.getConfig().setDisableLogging(true);

        if (!isDevelopmentBuild) {
            return;
        }

        fancyAnalytics.registerMinecraftPluginMetrics(INSTANCE);
        fancyAnalytics.getExceptionHandler().registerLogger(getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(Bukkit.getLogger());
        fancyAnalytics.getExceptionHandler().registerLogger(fancyLogger);

        fancyAnalytics.registerStringMetric(new MetricSupplier<>("commit_hash", () -> versionConfig.getHash().substring(0, 7)));

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

        fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_holograms", () -> (double) hologramsManager.getHolograms().size()));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("enabled_update_notifications", () -> configuration.areVersionNotificationsMuted() ? "false" : "true"));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("fflag_disable_holograms_for_bedrock_players", () -> FHFeatureFlags.DISABLE_HOLOGRAMS_FOR_BEDROCK_PLAYERS.isEnabled() ? "true" : "false"));
        fancyAnalytics.registerStringMetric(new MetricSupplier<>("using_development_build", () -> isDevelopmentBuild ? "true" : "false"));

        fancyAnalytics.registerStringArrayMetric(new MetricSupplier<>("hologram_type", () -> {
            if (hologramsManager == null) {
                return new String[0];
            }

            return hologramsManager.getHolograms().stream()
                    .map(h -> h.getData().getType().name())
                    .toArray(String[]::new);
        }));


        fancyAnalytics.initialize();
    }

    public FancyAnalyticsAPI getFancyAnalytics() {
        return fancyAnalytics;
    }
}
