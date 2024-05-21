package de.oliver.fancyholograms;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyholograms.api.*;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.commands.FancyHologramsCMD;
import de.oliver.fancyholograms.commands.FancyHologramsTestCMD;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.listeners.NpcListener;
import de.oliver.fancyholograms.listeners.PlayerListener;
import de.oliver.fancyholograms.storage.FlatFileHologramStorage;
import de.oliver.fancyholograms.version.*;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class FancyHolograms extends JavaPlugin implements FancyHologramsPlugin {

    public static final String[] SUPPORTED_VERSIONS = {"1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6"};


    @Nullable
    private static FancyHolograms INSTANCE;
    private final VersionFetcher versionFetcher = new MasterVersionFetcher("FancyHolograms");
    private final VersionConfig versionConfig = new VersionConfig(this, versionFetcher);
    private final FancyScheduler scheduler = ServerSoftware.isFolia() ? new FoliaScheduler(this) : new BukkitScheduler(this);
    private final Collection<Command> commands = Arrays.asList(new HologramCMD(this), new FancyHologramsCMD(this));
    private final ExecutorService fileStorageExecutor = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setPriority(Thread.MIN_PRIORITY + 1)
                    .setNameFormat("FancyHolograms-FileStorageExecutor")
                    .build()
    );
    private HologramConfiguration configuration = new FancyHologramsConfiguration();
    private HologramStorage hologramStorage = new FlatFileHologramStorage();
    @Nullable
    private HologramManagerImpl hologramsManager;
    private boolean isUsingViaVersion;

    public static @NotNull FancyHolograms get() {
        return Objects.requireNonNull(INSTANCE, "plugin is not initialized");
    }

    public static boolean isUsingFancyNpcs() {
        return Bukkit.getPluginManager().isPluginEnabled("FancyNpcs");
    }

    @Override
    public JavaPlugin getPlugin() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;

        final var adapter = resolveHologramAdapter();

        if (adapter == null) {
            getLogger().warning("""
                                                    
                    --------------------------------------------------
                    Unsupported minecraft server version.
                    Please update the server to one of (%s).
                    Disabling the FancyHolograms plugin.
                    --------------------------------------------------
                    """.formatted(String.join(" / ", SUPPORTED_VERSIONS)));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        hologramsManager = new HologramManagerImpl(this, adapter);
    }

    @Override
    public void onEnable() {
        getHologramConfiguration().reload(this); // initialize configuration

        FancyLib.setPlugin(this);

        if (!ServerSoftware.isPaper()) {
            getLogger().warning("""
                                                    
                    --------------------------------------------------
                    It is recommended to use Paper as server software.
                    Because you are not using paper, the plugin
                    might not work correctly.
                    --------------------------------------------------
                    """);
        }


        reloadCommands();

        registerListeners();

        versionConfig.load();
        if (!getHologramConfiguration().areVersionNotificationsMuted()) {
            checkForNewerVersion();
        }

        registerMetrics();

        getHologramsManager().initializeTasks();

        isUsingViaVersion = Bukkit.getPluginManager().getPlugin("ViaVersion") != null;

        if (getHologramConfiguration().isAutosaveEnabled()) {
            getScheduler().runTaskTimerAsynchronously(getHologramConfiguration().getAutosaveInterval() * 20L, 20L * 60L * getHologramConfiguration().getAutosaveInterval(), hologramsManager::saveHolograms);
        }
    }

    @Override
    public void onDisable() {
        hologramsManager.saveHolograms();
        fileStorageExecutor.shutdown();
        INSTANCE = null;
    }

    @Override
    public boolean isUsingViaVersion() {
        return isUsingViaVersion;
    }

    public @NotNull VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public @NotNull VersionConfig getVersionConfig() {
        return versionConfig;
    }

    public @NotNull FancyScheduler getScheduler() {
        return this.scheduler;
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

    public ExecutorService getFileStorageExecutor() {
        return this.fileStorageExecutor;
    }

    private @Nullable Function<HologramData, Hologram> resolveHologramAdapter() {
        final var version = Bukkit.getMinecraftVersion();

        return switch (version) {
            case "1.20.5", "1.20.6" -> Hologram1_20_6::new;
            case "1.20.3", "1.20.4" -> Hologram1_20_4::new;
            case "1.20.2" -> Hologram1_20_2::new;
            case "1.20", "1.20.1" -> Hologram1_20_1::new;
            case "1.19.4" -> Hologram1_19_4::new;
            default -> null;
        };
    }

    public void reloadCommands() {
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

        if (isUsingFancyNpcs()) {
            getServer().getPluginManager().registerEvents(new NpcListener(this), this);
        }
    }

    private void checkForNewerVersion() {
        final var current = new ComparableVersion(versionConfig.getVersion());

        supplyAsync(getVersionFetcher()::fetchNewestVersion).thenApply(Objects::requireNonNull).whenComplete((newest, error) -> {
            if (error != null || newest.compareTo(current) <= 0) {
                return; // could not get the newest version or already on latest
            }

            getLogger().warning("""
                                                            
                    -------------------------------------------------------
                    You are not using the latest version the FancyHolograms plugin.
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
    }

}
