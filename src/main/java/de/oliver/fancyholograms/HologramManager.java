package de.oliver.fancyholograms;

import de.oliver.fancynpcs.FancyNpcs;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Display;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;

import java.util.*;

public class HologramManager {

    private final Map<String, Hologram> holograms; // hologram name, hologram object

    public HologramManager() {
        this.holograms = new HashMap<>();
    }

    public void saveHolograms(boolean force){
        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        for (Hologram hologram : holograms.values()) {
            if(!hologram.isSaveToFile()){
                continue;
            }

            boolean shouldSave = force || hologram.isDirty();
            if(!shouldSave){
                continue;
            }

            config.set("holograms." + hologram.getName() + ".location.world", hologram.getLocation().getWorld().getName());
            config.set("holograms." + hologram.getName() + ".location.x", hologram.getLocation().x());
            config.set("holograms." + hologram.getName() + ".location.y", hologram.getLocation().y());
            config.set("holograms." + hologram.getName() + ".location.z", hologram.getLocation().z());
            config.set("holograms." + hologram.getName() + ".location.yaw", hologram.getLocation().getYaw());
            config.set("holograms." + hologram.getName() + ".location.pitch", hologram.getLocation().getPitch());
            config.set("holograms." + hologram.getName() + ".billboard", hologram.getBillboard().getSerializedName());
            config.set("holograms." + hologram.getName() + ".scale", hologram.getScale());
            config.set("holograms." + hologram.getName() + ".text", hologram.getLines());
            config.set("holograms." + hologram.getName() + ".text_shadow", hologram.hasTextShadow());
            config.set("holograms." + hologram.getName() + ".shadow_radius", hologram.getShadowRadius());
            config.set("holograms." + hologram.getName() + ".shadow_strength", hologram.getShadowStrength());
            config.set("holograms." + hologram.getName() + ".update_text_interval", hologram.getUpdateTextInterval());
            if(hologram.getBackground() != null){
                config.set("holograms." + hologram.getName() + ".background", hologram.getBackground().getSerializedName());
            }
            if(hologram.getLinkedNpc() != null){
                config.set("holograms." + hologram.getName() + ".linkedNpc", hologram.getLinkedNpc().getName());
            }

            hologram.setDirty(false);
        }

        FancyHolograms.getInstance().saveConfig();
    }

    public void loadHolograms(){
        holograms.clear();

        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        if(!config.isConfigurationSection("holograms")){
            return;
        }

        for (String name : config.getConfigurationSection("holograms").getKeys(false)) {
            Location location = null;

            try{
                location = config.getLocation("holograms." + name + ".location");
            } catch (Exception ignored){ }

            if(location == null) {
                String worldName = config.getString("holograms." + name + ".location.world", "world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    FancyHolograms.getInstance().getLogger().info("Trying to load the world: '" + worldName + "'");
                    world = new WorldCreator(worldName).createWorld();
                }

                if (world == null) {
                    FancyHolograms.getInstance().getLogger().info("Could not load hologram '" + name + "', because the world '" + worldName + "' is not loaded");
                    continue;
                }

                double x = config.getDouble("holograms." + name + ".location.x", 0);
                double y = config.getDouble("holograms." + name + ".location.y", 0);
                double z = config.getDouble("holograms." + name + ".location.z", 0);
                float yaw = (float) config.getDouble("holograms." + name + ".location.yaw", 0);
                float pitch = (float) config.getDouble("holograms." + name + ".location.pitch", 0);

                location = new Location(world, x, y, z, yaw, pitch);
            }
            
            ChatFormatting background = config.isString("holograms." + name + ".background") ? ChatFormatting.getByName(config.getString("holograms." + name + ".background")) : null;
            float scale = (float) config.getDouble("holograms." + name + ".scale", 1f);
            List<String> text = config.getStringList("holograms." + name + ".text");
            int updateTextInterval = config.getInt("holograms." + name + ".update_text_interval", -1);
            float shadowRadius = (float) config.getDouble("holograms." + name + ".shadow_radius", 0);
            float shadowStrength = (float) config.getDouble("holograms." + name + ".shadow_strength", 1);
            boolean textShadow = config.getBoolean("holograms." + name + ".text_shadow", false);
            String linkedNpcName = config.getString("holograms." + name + ".linkedNpc");

            String billboardName = config.getString("holograms." + name + ".billboard", "center");
            Display.BillboardConstraints billboard = Display.BillboardConstraints.CENTER;
            for (Display.BillboardConstraints b : Display.BillboardConstraints.values()) {
                if(b.getSerializedName().equalsIgnoreCase(billboardName)){
                    billboard = b;
                }
            }

            if(text.size() == 0){
                text.add("<red><b>Could not load text</b></red>");
            }

            Hologram hologram = new Hologram(name, location, text, billboard, scale, background, shadowRadius, shadowStrength, updateTextInterval, textShadow, null);

            if(FancyHolograms.getInstance().isUsingFancyNpcs() && linkedNpcName != null && linkedNpcName.length() > 0){
                hologram.setLinkedNpc(FancyNpcs.getInstance().getNpcManager().getNpc(linkedNpcName));
            }

            hologram.create();
        }

    }

    public void reloadHolograms(){
        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();

        for (Hologram hologram : new ArrayList<>(getAllHolograms())) {
            for (ServerPlayer player : playerList.players) {
                hologram.remove(player);
            }
        }

        loadHolograms();
    }

    public Collection<Hologram> getAllHolograms(){
        return holograms.values();
    }

    public void addHologram(Hologram hologram){
        holograms.put(hologram.getName(), hologram);
    }

    public void removeHologram(Hologram hologram){
        holograms.remove(hologram.getName());

        FileConfiguration config = FancyHolograms.getInstance().getConfig();
        config.set("holograms." + hologram.getName(), null);
        FancyHolograms.getInstance().saveConfig();
    }

    public Hologram getHologram(String name){
        return holograms.getOrDefault(name, null);
    }

    public Map<String, Hologram> getHolograms() {
        return holograms;
    }
}
