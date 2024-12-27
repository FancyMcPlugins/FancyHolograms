package de.oliver.fancyholograms.main;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancyholograms.HologramManagerImpl;
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
import de.oliver.fancyholograms.metrics.FHMetrics;
import de.oliver.fancyholograms.storage.FlatFileHologramStorage;
import de.oliver.fancyholograms.storage.converter.FHConversionRegistry;
import de.oliver.fancyholograms.util.PluginUtils;
import de.oliver.fancylib.FancyLib;
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

public final class FancyHolograms extends JavaPlugin implements de.oliver.fancyholograms.api.FancyHolograms {

    private static @Nullable FancyHolograms INSTANCE;

    private final ExtendedFancyLogger fancyLogger;

    private final FHMetrics metrics;

    private final VersionFetcher versionFetcher;
    private final VersionConfig versionConfig;

    private final ScheduledExecutorService hologramThread;
    private final ExecutorService storageThread;

    private final HologramConfiguration configuration;
    private HologramStorage hologramStorage;

    private HologramManagerImpl hologramsManager;

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
        fancyLogger = new ExtendedFancyLogger("FancyHolograms", LogLevel.INFO, List.of(consoleAppender, jsonAppender), new ArrayList<>());

        metrics = new FHMetrics();

        versionFetcher = new MasterVersionFetcher("FancyHolograms");
        versionConfig = new VersionConfig(this, versionFetcher);

        hologramThread = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyHolograms-Hologram")
                        .build()
        );

        storageThread = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setPriority(Thread.MIN_PRIORITY + 1)
                        .setNameFormat("FancyHolograms-Storage")
                        .build()
        );

        configuration = new FHConfiguration();
    }

    public static @NotNull FancyHolograms get() {
        return Objects.requireNonNull(INSTANCE, "plugin is not initialized");
    }

    public static boolean canGet() {
        return INSTANCE != null;
    }

    @Override
    public void onLoad() {
        FHFeatureFlags.load();
        configuration.reload(this);

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(configuration.getLogLevel());
        } catch (IllegalArgumentException e) {
            logLevel = LogLevel.INFO;
        }
        fancyLogger.setCurrentLevel(logLevel);
        IFancySitula.LOGGER.setCurrentLevel(logLevel);

        hologramStorage = new FlatFileHologramStorage();

        if (!ServerSoftware.isPaper()) {
            fancyLogger.warn("""
                    --------------------------------------------------
                    It is recommended to use Paper as server software.
                    Because you are not using paper, the plugin
                    might not work correctly.
                    --------------------------------------------------
                    """);
        }

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
        new FancyLib(INSTANCE);

        registerCommands();
        registerListeners();

        versionConfig.load();
        if (!configuration.areVersionNotificationsMuted()) {
            checkForNewerVersion();
        }

        metrics.register();
        metrics.registerLegacy();

        hologramsManager.initializeTasks();

        if (configuration.isAutosaveEnabled()) {
            getHologramThread().scheduleAtFixedRate(() -> {
                if (hologramsManager != null) {
                    hologramsManager.saveHolograms();
                }
            }, configuration.getAutosaveInterval(), configuration.getAutosaveInterval() * 60L, TimeUnit.SECONDS);
        }

        FHConversionRegistry.registerBuiltInConverters();

        fancyLogger.info("Successfully enabled FancyHolograms version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        hologramsManager.saveHolograms();
        hologramThread.shutdown();
        storageThread.shutdown();

        fancyLogger.info("Successfully disabled FancyHolograms version %s".formatted(getDescription().getVersion()));

        INSTANCE = null;
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

    private void registerCommands() {
        Collection<Command> commands = Arrays.asList(new HologramCMD(this), new FancyHologramsCMD(this));

        if (configuration.isRegisterCommands()) {
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

    @Override
    public JavaPlugin getPlugin() {
        return INSTANCE;
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    public FHMetrics getMetrics() {
        return metrics;
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
    public HologramStorage getHologramStorage() {
        return hologramStorage;
    }

    public ScheduledExecutorService getHologramThread() {
        return hologramThread;
    }

    public ExecutorService getStorageThread() {
        return this.storageThread;
    }

    public HologramConfiguration getFHConfiguration() {
        return configuration;
    }
}
