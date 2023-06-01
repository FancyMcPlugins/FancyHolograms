package de.oliver.fancyholograms;

import de.oliver.fancyholograms.commands.FancyHologramsCMD;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.listeners.*;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.VersionFetcher;
import de.oliver.fancylib.serverSoftware.FoliaScheduler;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public class FancyHolograms extends JavaPlugin {

    public static final String SUPPORTED_VERSION = "1.19.4";
    private static FancyHolograms instance;

    private final FancyScheduler scheduler;
    private final VersionFetcher versionFetcher;
    private final HologramManager hologramManager;
    private final FancyHologramsConfig config;
    private boolean usingPlaceholderApi;
    private boolean usingMiniPlaceholders;
    private boolean usingFancyNpcs;

    public FancyHolograms() {
        instance = this;
        scheduler = ServerSoftware.isFolia()
                    ? new FoliaScheduler(instance)
                    : new BukkitScheduler(instance);
        versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancyholograms/version", "https://modrinth.com/plugin/fancyholograms/versions");
        hologramManager = new HologramManager();
        config = new FancyHologramsConfig();
    }

    @Override
    public void onEnable() {
        FancyLib.setPlugin(instance);

        PluginManager pluginManager = Bukkit.getPluginManager();
        usingPlaceholderApi = pluginManager.isPluginEnabled("PlaceholderAPI");
        usingFancyNpcs = pluginManager.isPluginEnabled("FancyNpcs");
        usingMiniPlaceholders = pluginManager.isPluginEnabled("MiniPlaceholders");

        CompletableFuture.runAsync(() -> {
            ComparableVersion newestVersion = versionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyHolograms plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        });

        DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

        String serverVersion = nmsServer.getServerVersion();
        if (!serverVersion.equals(SUPPORTED_VERSION)){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + SUPPORTED_VERSION + ".");
            getLogger().warning("Disabling the FancyHologram plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        if (!ServerSoftware.isPaper()){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        // register bStats
        Metrics metrics = new Metrics(this, 17990);

        // register commands
        getCommand("FancyHolograms").setExecutor(new FancyHologramsCMD());
        getCommand("hologram").setExecutor(new HologramCMD());

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), instance);
        pluginManager.registerEvents(new PlayerMoveListener(), instance);
        if(usingFancyNpcs){
            pluginManager.registerEvents(new NpcModifyListener(), instance);
            pluginManager.registerEvents(new NpcRemoveListener(), instance);
        }

        config.reload();

        scheduler.runTaskLater(null, 20L*6, () -> {
            hologramManager.loadHolograms();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                CraftPlayer craftPlayer = (CraftPlayer) onlinePlayer;
                ServerPlayer serverPlayer = craftPlayer.getHandle();

                for (Hologram hologram : hologramManager.getAllHolograms()) {
                    hologram.spawn(serverPlayer);
                }
            }
        });

        scheduler.runTaskTimerAsynchronously(7, 1, () -> {
            for (Hologram hologram : hologramManager.getAllHolograms()) {
                long interval = hologram.getUpdateTextInterval() * 1000L;

                if(interval < 1){
                    continue;
                }

                long lastUpdate = hologram.getLastTextUpdate();
                long nextUpdate = lastUpdate + interval;
                long current = System.currentTimeMillis();
                if(current < nextUpdate){
                    continue;
                }

                for (Player onlinePlayer : hologram.getLocation().getWorld().getPlayers()) {
                    CraftPlayer craftPlayer = (CraftPlayer) onlinePlayer;
                    ServerPlayer serverPlayer = craftPlayer.getHandle();
                    hologram.updateText(serverPlayer);
                }

                hologram.setLastTextUpdate(current);
            }
        });

        if(config.isEnableAutosave()) {
            int autosaveInterval = config.getAutosaveInterval();
            scheduler.runTaskTimerAsynchronously(autosaveInterval*60L, autosaveInterval*60L, () -> hologramManager.saveHolograms(false));
        }
    }

    @Override
    public void onDisable() {
        hologramManager.saveHolograms(true);
    }

    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public FancyHologramsConfig getFancyHologramsConfig() {
        return config;
    }

    public boolean isUsingPlaceholderApi() {
        return usingPlaceholderApi;
    }

    public boolean isUsingMiniplaceholders() {
        return usingMiniPlaceholders;
    }

    public boolean isUsingFancyNpcs() {
        return usingFancyNpcs;
    }

    public static FancyHolograms getInstance() {
        return instance;
    }
}
