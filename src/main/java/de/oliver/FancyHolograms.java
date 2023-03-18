package de.oliver;

import de.oliver.commands.HologramCMD;
import de.oliver.listeners.PlayerJoinListener;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
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

    public FancyHolograms() {
        instance = this;
        hologramManager = new HologramManager();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

        String serverVersion = nmsServer.getServerVersion();
        if(!serverVersion.equals(SUPPORTED_VERSION)){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Unsupported minecraft server version.");
            getLogger().warning("Please update the server to " + SUPPORTED_VERSION + ".");
            getLogger().warning("Disabling NPC plugin.");
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

        // register commands
        getCommand("hologram").setExecutor(new HologramCMD());

        // register listeners
        pluginManager.registerEvents(new PlayerJoinListener(), instance);

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            hologramManager.loadHolograms();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                CraftPlayer craftPlayer = (CraftPlayer) onlinePlayer;
                ServerPlayer serverPlayer = craftPlayer.getHandle();

                for (Hologram hologram : hologramManager.getAllHolograms()) {
                    hologram.spawn(serverPlayer);
                }
            }

        }, 20L * 5);
    }

    @Override
    public void onDisable() {
        hologramManager.saveHolograms();
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public static FancyHolograms getInstance() {
        return instance;
    }
}
