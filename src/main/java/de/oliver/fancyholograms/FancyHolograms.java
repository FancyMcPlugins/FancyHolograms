package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.commands.FancyHologramsCMD;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.listeners.NpcListener;
import de.oliver.fancyholograms.listeners.PlayerListener;
import de.oliver.fancyholograms.version.Hologram1_19_4;
import de.oliver.fancyholograms.version.Hologram1_20_1;
import de.oliver.fancyholograms.version.Hologram1_20_2;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class FancyHolograms extends JavaPlugin implements FancyHologramsPlugin {

    public static final String[] SUPPORTED_VERSIONS = {"1.19.4", "1.20", "1.20.1", "1.20.2"};
    @Nullable
    private static FancyHolograms INSTANCE;
    private final VersionFetcher versionFetcher = new MasterVersionFetcher("FancyHolograms");
    private final FancyHologramsConfig configuration = new FancyHologramsConfig(this);
    private final HologramsConfig hologramsConfig = new HologramsConfig();
    private final VersionConfig versionConfig = new VersionConfig(this, versionFetcher);
    private final FancyScheduler scheduler = ServerSoftware.isFolia() ?
            new FoliaScheduler(this) :
            new BukkitScheduler(this);
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
        getConfiguration().reload(); // initialize configuration

        FancyLib.setPlugin(this);

        Metrics metrics = new Metrics(this, 17990);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_holograms", () -> hologramsManager.getHolograms().size()));

        if (!ServerSoftware.isPaper()) {
            getLogger().warning("""
                                                    
                    --------------------------------------------------
                    It is recommended to use Paper as server software.
                    Because you are not using paper, the plugin
                    might not work correctly.
                    --------------------------------------------------
                    """);
        }


        registerCommands();

        registerListeners();

        checkForNewerVersion();

        getHologramsManager().initializeTasks();

        isUsingViaVersion = Bukkit.getPluginManager().getPlugin("ViaVersion") != null;

        if (getConfiguration().isAutosaveEnabled()) {
            getScheduler().runTaskTimerAsynchronously(getConfiguration().getAutosaveInterval() * 20L, 20L * 60L * 5L, () -> {
                hologramsManager.saveHolograms();
            });
        }
    }

    @Override
    public void onDisable() {
        hologramsManager.saveHolograms();
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

    public @NotNull FancyHologramsConfig getConfiguration() {
        return this.configuration;
    }

    public @NotNull HologramsConfig getHologramsConfig() {
        return hologramsConfig;
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

    private @Nullable Function<HologramData, Hologram> resolveHologramAdapter() {
        final var version = Bukkit.getMinecraftVersion();

        return switch (version) {
            case "1.20.2" -> Hologram1_20_2::new;
            case "1.20", "1.20.1" -> Hologram1_20_1::new;
            case "1.19.4" -> Hologram1_19_4::new;
            default -> null;
        };
    }

    private void registerCommands() {
        final var hologramCommand = getCommand("hologram");
        if (hologramCommand != null) {
            hologramCommand.setExecutor(new HologramCMD(this));
        }

        final var fancyHologramsCommand = getCommand("fancyholograms");
        if (fancyHologramsCommand != null) {
            fancyHologramsCommand.setExecutor(new FancyHologramsCMD(this));
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        if (isUsingFancyNpcs()) {
            getServer().getPluginManager().registerEvents(new NpcListener(this), this);
        }
    }

    private void checkForNewerVersion() {
        versionConfig.load();

        final var current = new ComparableVersion(versionConfig.getVersion());

        supplyAsync(getVersionFetcher()::fetchNewestVersion)
                .thenApply(Objects::requireNonNull)
                .whenComplete((newest, error) -> {
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

}
