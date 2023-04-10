package de.oliver;

import de.oliver.commands.HologramCMD;
import de.oliver.listeners.NpcModifyListener;
import de.oliver.listeners.NpcRemoveListener;
import de.oliver.listeners.PlayerChangedWorldListener;
import de.oliver.listeners.PlayerJoinListener;
import de.oliver.utils.Metrics;
import de.oliver.utils.VersionFetcher;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyHolograms extends JavaPlugin {

    public static final String SUPPORTED_VERSION = "1.19.4";
    private static FancyHolograms instance;

    private final HologramManager hologramManager;
    private boolean muteVersionNotification;
    private boolean usingPlaceholderApi;
    private boolean usingFancyNpcs;

    public FancyHolograms() {
        instance = this;
        hologramManager = new HologramManager();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        usingPlaceholderApi = pluginManager.isPluginEnabled("PlaceholderAPI");
        usingFancyNpcs = pluginManager.isPluginEnabled("FancyNpcs");

        new Thread(() -> {
            ComparableVersion newestVersion = VersionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyHolograms plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(VersionFetcher.DOWNLOAD_URL);
                getLogger().warning("-------------------------------------------------------");
            }
        }).start();

        DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

        String serverVersion = nmsServer.getServerVersion();
        if(!serverVersion.equals(SUPPORTED_VERSION)){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + SUPPORTED_VERSION + ".");
            getLogger().warning("Disabling the FancyHologram plugin.");
            getLogger().warning("--------------------------------------------------");
            pluginManager.disablePlugin(this);
            return;
        }

        String serverSoftware = nmsServer.getServerModName();
        if(!serverSoftware.equals("Paper")){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        // register bStats
        Metrics metrics = new Metrics(this, 17990);

        // register commands
        getCommand("hologram").setExecutor(new HologramCMD());

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), instance);
        if(usingFancyNpcs){
            pluginManager.registerEvents(new NpcModifyListener(), instance);
            pluginManager.registerEvents(new NpcRemoveListener(), instance);
        }

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            hologramManager.loadHolograms();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                CraftPlayer craftPlayer = (CraftPlayer) onlinePlayer;
                ServerPlayer serverPlayer = craftPlayer.getHandle();

                for (Hologram hologram : hologramManager.getAllHolograms()) {
                    hologram.spawn(serverPlayer);
                }
            }

        }, 20L * 6);

        Bukkit.getScheduler().runTaskTimer(instance, () -> {
            if(!getConfig().isBoolean("mute_version_notification")){
                getConfig().set("mute_version_notification", false);
                saveConfig();
            }
            muteVersionNotification = getConfig().getBoolean("mute_version_notification");

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

        }, 20 * 7L, 20);

        Bukkit.getScheduler().runTaskTimer(instance, () -> {
            hologramManager.saveHolograms(false);
        }, 20*60*5, 20*60*15);
    }

    @Override
    public void onDisable() {
        hologramManager.saveHolograms(true);
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
    }

    public boolean isUsingPlaceholderApi() {
        return usingPlaceholderApi;
    }

    public boolean isUsingFancyNpcs() {
        return usingFancyNpcs;
    }

    public static FancyHolograms getInstance() {
        return instance;
    }
}
